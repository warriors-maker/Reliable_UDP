package udp_package;

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

public class SenderNetWorkUtil {
	private static final int ECHOMAX = 512; // Maximum size of echo datagram packet payload

	byte[] recvBuf = new byte[ECHOMAX];//receive the packet
	byte[] sendBuf = new byte[ECHOMAX];//send the packet
	DatagramPacket receivePacket, sendpacket;
	echoData data;
	DatagramSocket clientSocket,serverSocket,socket;
	InetAddress serverAddress;
	int serverPort;


	public SenderNetWorkUtil(InetAddress serverAddress,int serverPort) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
	}

	public void createClientSocket() throws SocketException {
		clientSocket = new DatagramSocket();;
	}
	
	private void setReceivePacket() throws SocketException { //create a UDP packet to receive frome a server
		receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
	}
	
	public void setSendPacket() throws SocketException { //create a UDP packet to send to a server
		sendpacket = new DatagramPacket(sendBuf, sendBuf.length, serverAddress, serverPort); 
	}

	public void clientPrint(){
		System.out.println("Handling client at " + receivePacket.getAddress().getHostAddress() + " on port " + receivePacket.getPort());
	}
	
	//return the ack;
	public echoData getAck() throws IOException, ClassNotFoundException {
		setReceivePacket();
		clientSocket.receive(receivePacket); // Receive packet from client
		//System.out.println("hereh2");
		//System.out.println("a FROM Server: the size of byte data in packet is " + receivePacket.getLength());
		recvBuf = receivePacket.getData();
		ByteArrayInputStream byteStream2 = new ByteArrayInputStream(recvBuf);
		ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream2));
		data = (echoData) is.readObject();
		int ack = data.getAckNumber();
		System.out.println("Get the ack " + ack);
		//System.out.println("Client name: " + data.getName() + " Got: " + data.getData());
		is.close();
		return data;
	}
	
	//send the data from the server;
	public void sendData(echoData data) throws IOException, ClassNotFoundException {	
		//System.out.println("Client name: " + data.getName() + " echo: " + data.getData());
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(ECHOMAX); //create a byte array big enough to hold serialized object
		ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream)); //wrap the byte stream with an Object stream
		os.flush();
		os.writeObject(data); //now write the data to the object output stream (still have not sent the packet)
		os.flush();
		//retrieves byte array
		byte[] sendBuf = byteStream.toByteArray();  //get a byte array from the serialized object	
		DatagramPacket sendPacket = new DatagramPacket(sendBuf, sendBuf.length, serverAddress, serverPort); //fill in the datagram packet
		clientSocket.send(sendPacket); // Send the object
		os.close();
	}
	
	public void closeSocket(){
		clientSocket.close();
	}
}
