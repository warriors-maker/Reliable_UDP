package udp_package;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

public class Receiver {
	
	private static void createFile () {
		File file = new File("/Users/yingjianwu/Documents/Java/LeetcodePractice/linkedList/UDPwithReliabilityPartD/src/MobyDick_out.txt");
		  
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
	
	private static void writeFile(String line) throws IOException {
		File fout = new File("/Users/yingjianwu/Documents/Java/LeetcodePractice/linkedList/UDPwithReliabilityPartD/src/MobyDick_out.txt");
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
		
		if (args.length != 1) { // Test for correct argument list
			throw new IllegalArgumentException("Parameter(s): <Port>");
		}
		
		int portnum = Integer.parseInt(args[0]);
		int count = 0;
//		int servPort = Integer.parseInt(args[0]);
		ReceiverNetWorkUtil nu = new ReceiverNetWorkUtil(portnum);
		nu.createServerSocket();
		nu.setReceivePacket();
		
		System.out.println("I am an Echo Server and I am waiting on port # " + portnum);

		boolean start = false;
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
			
			Random ran = new Random();
			int x = ran.nextInt(40) + 20; //sleep between
			try {
				Thread.sleep(x);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
			
			//check whether valid to send or not
			
			int expected = rcb.getExpectedNumber();
			if (seqNumber == expected ) {
				rcb.increaExpectedNumber();
				count++;
				System.out.println("Finished" + count);
				System.out.println("Got the data I want");
				System.out.println(seqNumber);
				System.out.println(sentence);
				//write to the file
				writeFile(sentence);
				res.append(sentence);
				System.out.println("-------------------------------------");
				
				//create randomNumber
				int random = (int) (Math.random() * 100);
				
				//trigger lost packets
				if (random < 25) {
					System.out.println("The ack sending is going to be lost!");
				} else {
					echoSerializedData = receivedSerializedData;
					nu.sendAck(echoSerializedData);
				}
				
			} else {
				int random = (int) (Math.random() * 100);
				if (random < 25) {
					System.out.println("The ack for " + rcb.getExpectedNumber() +"is lost");
				} else {
					//When not my expected number
					System.out.println("The number you give me is " + seqNumber);
					System.out.println("I am Expecting " + rcb.getExpectedNumber());
					System.out.println("Not the data I want!");
					echoSerializedData = new echoData(rcb.getExpectedNumber());
					nu.sendAck(echoSerializedData);
				}
			}
		}
	}
}
