package udp_package;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

public class Receiver {
	
	private static void createFile () {
		File file = new File("/Users/yingjianwu/Documents/Java/LeetcodePractice/linkedList/UDPwithReliabilityPartA/src/MobyDick_out.txt");
		  
		//Create the file
		try {
			if (file.createNewFile())
			{
			    System.out.println("File is created!");
			} else {
			    System.out.println("File already exists.");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void readToFile(String line) throws IOException {
		File fout = new File("/Users/yingjianwu/Documents/Java/LeetcodePractice/linkedList/UDPwithReliabilityPartA/src/MobyDick_out.txt");
		FileOutputStream fos = new FileOutputStream(fout,true);
	 
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		bw.write(line);
		bw.newLine();
		bw.close();
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
	
		ReceiverBuffer rcb= null;
		echoData receivedSerializedData, echoSerializedData;
		
		StringBuilder res = new StringBuilder();//check whether we get the same text;
		
//		if (args.length != 1) { // Test for correct argument list
//			throw new IllegalArgumentException("Parameter(s): <Port>");
//		}
		
		if (args.length != 1) { // Test for correct argument list
			throw new IllegalArgumentException("Parameter(s): <Port>");
		}
		
		int portnum = Integer.parseInt(args[0]);

//		int servPort = Integer.parseInt(args[0]);
		ReceiverNetWorkUtil nu = new ReceiverNetWorkUtil(portnum);
		nu.createServerSocket();
		nu.setReceivePacket();
		
		System.out.println("I am an Echo Server and I am waiting on port # " + portnum);

		boolean start = false;
		//Create the File:
		createFile();
		
		while (true) { // Run forever, receiving and echoing datagrams from any client
			receivedSerializedData = nu.getData();
			if (!start) {
				rcb = new ReceiverBuffer(receivedSerializedData.getBufferSize());
				start = true;
			}
			
			//nu.serverPrint();
			boolean end = receivedSerializedData.getEnd();
			if (end) {
				System.out.println("------------------------------------");
				System.out.println("Finish reading all of the line");
				echoData signalEnd = new echoData(true,true);
				nu.sendAck(signalEnd);
				checkSame c = new checkSame();
				c.readData();
				System.out.println(c.getString().equals(res.toString()));
				break;
			}
			
			int seqNumber = receivedSerializedData.getSequenceNumber(); //now just get the data (not the name)
			String sentence = receivedSerializedData.getSentence();
			
			
			//check whether valid to send or not
			int expected = rcb.getExpectedNumber();
			if (seqNumber == expected ) {
				System.out.println("Got the data I want");
				System.out.println(seqNumber);
				System.out.println(sentence);
				//Write the line into the file
				//readToFile(sentence);
				
				res.append(sentence);
				System.out.println("-------------------------------------");
				echoSerializedData = receivedSerializedData;
				nu.sendAck(echoSerializedData);
				rcb.increaExpectedNumber();
			} else {
				System.out.println("The number you give me is " + seqNumber);
				System.out.println("I am Expecting " + rcb.getExpectedNumber());
				System.out.println("Not the data I want!");
				echoSerializedData = new echoData(expected);
				nu.sendAck(echoSerializedData);
			}
		}
	}

}
