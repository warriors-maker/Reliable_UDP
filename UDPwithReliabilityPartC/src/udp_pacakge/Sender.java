package udp_pacakge;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Sender {
	
	private static int windowSize() {
		String number = inputWindowSize();
		while (!valid(number)) {
			System.out.println("Must be a number great than 0 for the Window Size");
			number = inputWindowSize();
		}
		return Integer.parseInt(number);
	}
	
	private static boolean valid (String input) {
		if (input == null || input.isEmpty()) {
			return false;
		}
		for (int i = 0; i < input.length(); i++) {
			if (!Character.isDigit(input.charAt(i))) {
				return false;
			}
		}
		return input.charAt(0) != '0';
	}
	
	private static String inputWindowSize() {
		String text = "";
		BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
		try {
			// First time you need to send your name to the server.
			System.out.println("Enter Your WindowSize:");
			text = cin.readLine();
		} catch (Exception e) {
			System.exit(1);
		}
		return text;
	}
	
	public static void main (String[] args) throws SocketException, UnknownHostException {
		int windowSize = windowSize();
		
		if ((args.length != 2)) { // Test for correct # of args
			throw new IllegalArgumentException("Parameter(s): <Server> <Port>");
		}
		
		WindowController window = new WindowController(windowSize);
		String hostname = args[0];
		InetAddress serverAddress = InetAddress.getByName(hostname);//InetAddress.getByName(args[0]); // Server address
		int servPort = Integer.parseInt(args[1]); //Integer.parseInt(args[1]); // get port number
		
		
		SenderNetWorkUtil nu = new SenderNetWorkUtil(serverAddress, servPort); //create a connection to a server
		nu.createClientSocket();
		
		Thread sendData = new Thread(new SenderReadData(window, nu));
		sendData.start();
		
		Thread receiveAck = new Thread(new SenderReceiveAck(window, nu));
		receiveAck.start();
		
		try {
			sendData.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("SendData closed.");
		try {
			receiveAck.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
