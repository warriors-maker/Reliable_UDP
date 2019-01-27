package udp_package;

import java.io.IOException;
import java.net.SocketTimeoutException;
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

	private void retransmit() {
		int base = window.getBase();
		for (int i = 0; i < window.getFixedWindowSize(); i++) {
			int index = (base + i) % window.getFixedBuffer();
			if (window.getWindow().get(index) == null) {
				return;
			} else {
				echoData data = window.getWindow().get(index);
				try {
					System.out.println("Resend:" + data.getSequenceNumber());
					send.sendData(data);
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private boolean readAck() throws ClassNotFoundException, IOException {
		//Once you get an ack, and make sure that the window_size is consistent
		echoData data = null;
		//set the timeout for the first Guy
		if(window.getBase() == window.getSequence() && window.getBufferSize() != 0) {
			//set the timeout
			System.out.println("Set Timeout for the first Guy");
			send.getSocket().setSoTimeout(100);
			System.out.println("Hi!");
		}
		
		try {
			data = send.getAck();
		} catch(SocketTimeoutException e) {
			//if timeout we retransmit the data;
			this.retransmit();
			send.getSocket().setSoTimeout(100);
			return false;
		}
		
		//indicate that the client has finished reading;
		if(data.getStop()) {
			return true;
		}
		
		int seq = data.getSequenceNumber();
		int ack = data.getAckNumber();
		
		if (window.getBase() == ack) {
			//stop timer
			
		} else {
			//restart timer;
			send.getSocket().setSoTimeout(100);
		}
		
		window.moveWindow(ack);
		//can send the next window out;
		return false;
	}
}
