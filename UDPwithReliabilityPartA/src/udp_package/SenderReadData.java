package udp_package;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SenderReadData implements Runnable {
	WindowController window;
	SenderNetWorkUtil send;
	
	public SenderReadData(WindowController window, SenderNetWorkUtil s) {
		this.window = window;
		this.send  = s;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			readData();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void prepareData(String line) throws ClassNotFoundException, IOException {
		//make sure the window size is consistent
		window.putData(line);
		echoData data = window.canSend();
		//if the data is null, either means that the window is full or there is no data at that position
		if (data != null) {
			//add the timeout;
			sendData(data);
			System.out.println("Poof, send the data!");
			window.increaseNextSequence();
		}
	}
	
	//if we can send the nextSequence out;
	
	private void retransmit() {
		//restart timer again;
	}
	

	private void sendData(echoData echo)  {
		System.out.println("Send: " + echo.getSequenceNumber() + " " + echo.getSentence());
		try {
			send.sendData(echo);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void readData() throws FileNotFoundException, IOException, ClassNotFoundException {
		try (BufferedReader br = new BufferedReader(new FileReader("/Users/yingjianwu/Documents/Java/LeetcodePractice/linkedList/UDPwithReliabilityPartA/src/MobyDick.txt"))) {
		    String line;
		    while ((line = br.readLine()) != null || window.getBufferSize() != 0) {
		    	//if window_size is not full;
		    	this.prepareData(line);
		    }
		    br.close();
		}
		
		System.out.println("Finish Reading data"); 
		window.setEnd();
		send.sendData(new echoData(true));
	}
}