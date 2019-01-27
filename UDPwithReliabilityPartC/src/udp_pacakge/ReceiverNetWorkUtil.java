package udp_pacakge;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ReceiverNetWorkUtil {
	private static final int ECHOMAX = 512; // Maximum size of echo datagram packet payload

	byte[] recvBuf = new byte[ECHOMAX];
	DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
	DatagramPacket receivePacket, sendpacket;
	echoData data;
	DatagramSocket clientSocket,serverSocket,socket;
	InetAddress serverAddress;
	int serverPort;

	public ReceiverNetWorkUtil(int serverPort) {
		this.serverPort = serverPort;
	}
	
	public void createServerSocket() throws SocketException {
		socket = new DatagramSocket(serverPort);;
	}

	public void setReceivePacket() throws SocketException {
		receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
	}
	
	public void serverPrint(){
		System.out.println("Handling client at " + receivePacket.getAddress().getHostAddress() + " on port " + receivePacket.getPort());
	}

	public echoData getData() throws IOException, ClassNotFoundException {
		socket.receive(receivePacket); // Receive packet from client
		recvBuf = receivePacket.getData();
		ByteArrayInputStream byteStream2 = new ByteArrayInputStream(recvBuf);
		ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream2));
		data = (echoData) is.readObject();
		//System.out.println("Client name: " + " Got: " + data.getSentence());
		is.close();
		return data;
	}
	
	public void sendAck(echoData data) throws IOException, ClassNotFoundException {	
		//System.out.println("Client name: " + data.getName() + " echo: " + data.getData());
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(ECHOMAX); //create a byte array big enough to hold serialized object
		ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream)); //wrap the byte stream with an Object stream
		os.flush();
		os.writeObject(data); //now write the data to the object output stream (still have not sent the packet)
		os.flush();
		//retrieves byte array
		byte[] sendBuf = byteStream.toByteArray();  //get a byte array from the serialized object	
		DatagramSocket clientSocket = new DatagramSocket(); //create a new socket to send out the object
		DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, receivePacket.getAddress(), receivePacket.getPort()); //fill in the datagram packet
		clientSocket.send(sendPacket); // Send the object
		os.close();
		clientSocket.close();
	}
}
