package udp_package;

import java.io.IOException;
import java.util.List;

public class SenderReceiveAck implements Runnable{
	WindowController window;
	SenderNetWorkUtil send;
	
	public SenderReceiveAck (WindowController window, SenderNetWorkUtil send) {
		this.window = window;
		this.send = send;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				if(readAck()) {
					System.out.println("--------------------------------");
					System.out.println("The Client gets what I send, Done");
				}
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private boolean readAck() throws ClassNotFoundException, IOException {
		//Once you get an ack, and make sure that the window_size is consistent
		echoData data = send.getAck();
		//indicate that the client has finished reading;
		if(data.getStop()) {
			return true;
		}
		
		int seq = data.getSequenceNumber();
		int ack = data.getAckNumber();
		
		if (ack == window.getSequence()) {
			//stop timer
		} else {
			//restart timer;
		}
		
		window.moveWindow(ack);
		//can send the next window out;
		return false;
	}
}
