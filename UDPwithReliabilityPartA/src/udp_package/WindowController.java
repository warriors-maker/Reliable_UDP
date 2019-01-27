package udp_package;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class WindowController {
	private int w_size;
	private int b_size;
	private List<echoData> window;
	
	private int base;
	private int nextSequence;
	private int buffer;
	private boolean end; //end of the transmite file;
	
	
	//private int unAck;
	
	public WindowController(int w_size) {
		this.w_size = w_size;
		this.b_size = this.w_size * 2;
		this.base = 0;
		this.nextSequence = 0;
		//this.unAck = 0;
		this.buffer = 0;
		this.end = false;
		
		window = new ArrayList<>();
		for (int i = 0; i < b_size; i++) {
			window.add(i,null);
		}
		//ackSet = new HashSet<>();
	}
	
	
	public synchronized int getSequence() {
		return this.nextSequence;
	}
	
	public synchronized void increaseNextSequence() {
		this.nextSequence = (this.nextSequence + 1) % this.b_size;
	}
	
	public synchronized List<echoData> getWindow() {
		return this.window;
	}
	
	public int getFixedWindowSize() {
		return this.w_size;
	}
	
	public boolean getEnd() {
		return this.end;
	}
	public int getFixedBuffer() {
		return this.b_size;
	}
	
	public synchronized int getBase() {
		return this.base;
	}
	
	public synchronized void setBase(int base) {
		this.base = base;
	}
	
	
//	public synchronized int getUnAck() {
//		return this.unAck;
//	}
	
	public synchronized int getBufferSize() {
		return this.buffer;
	}
	
	private synchronized void increaseBuffer() {
		this.buffer++;
	}
	
	private synchronized void decreaseBuffer() {
		this.buffer--;
	}
	
//	public synchronized boolean stopWindow() {
//		return this.unAck == w_size;
//	}
	
	public synchronized boolean bufferFull() {
		return this.buffer == this.b_size;
	}
	
	private synchronized boolean inRange(int seq) {
		for (int i = 0; i < w_size; i++) {
			if (seq == (this.base + i) % b_size) {
				return true;
			}
		}
		return false;
	}
	
//	public synchronized void increaseUnAck() {
//		this.unAck++;
//	}
	
	//if we can send, we return the data;
	//cannot send if not in the range
	//or even if in range, need to check whether there is a data there.
	public synchronized echoData canSend() {
		int cur = this.getSequence();
		int base = this.getBase();
		//when there is a circular in which pivot is bigger than the nextSeq.
		
		if (cur < base) {
			cur += this.getFixedBuffer();
		}
		
		if (cur <= base + this.getFixedWindowSize()) {
			System.out.println("The sending Sequence is " + cur);
			cur = cur % this.b_size;
			echoData data = this.window.get(cur);
			//reach the case when there is nothing in the window
			//make sure that the data we sent is filled in or that the buffersize is not 0;
			if (data == null || this.getBufferSize() == 0) {
//				System.out.println("End: Current seq is" + seq);
//				System.out.println("End: Current buffer_Space is" + window.getBufferSize());
				return null;
			}
			return data;
		}
		return null;
	}
	
	//if enough buffer space
	private synchronized void addData(int index, echoData echo) {
		System.out.println("Put the data in index " + index);
		window.set(index % b_size, echo);
		//can increase our window size iff the window_Size != 4
		increaseBuffer();
	}
	
	//set end for the client to know that we sent all data
	public void setEnd() {
		this.end = true;
	}
	
	
	private synchronized void removeData(int index) {
		//System.out.println("Seq " + index + "is removed") ;
		window.set(index,null);
		decreaseBuffer();
	}
	
	
	private synchronized void waitOnWindow() {
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public synchronized void addtoSet(int seq) {
//		ackSet.add(seq);
//	}
	
	public synchronized void moveWindow(int ack) {
		//remove the data from our window according to the ack;
		while (base != ack) {
			this.removeData(base);//when removing we are shrinking our data size;
			//also move out base
			base = (base + 1) % this.b_size;
		}
		
		System.out.println("----------------------");
		System.out.println("Currentpivot is" + base);
		System.out.println("CurrentBuffer space is" + this.buffer);
		notifyAll(); //notify the sending data
	}
	
	public synchronized void printWindow() {
		for (int i = 0; i < window.size(); i++) {
			echoData e = window.get(i);
			if (e == null) {
				System.out.print(i+": null, ");
			} else {
				System.out.print(i + ", ");
			}
		}
		System.out.println();
	}
	
	//put the data if there is enough buffer space, or wait
	public synchronized void putData(String line) {
		//while the buffer space is also filled up, donot move
		while (this.bufferFull()) {
    		System.out.println("All window and bufferspaces are filled up, please wait for ack");
    		this.waitOnWindow();;
    	}
		
		
		int base = this.getBase();
    	int bufferSize = this.getBufferSize();
    	int total = this.getFixedBuffer();
    	int seq = (base + bufferSize) % total;
    	
    	//make sure that the text we read in is not null;
    	//handle the case when finish reading but the buffer space is not filled
		if (line != null) {
			//add the data to the window;
			System.out.println("Add Data to Window at index " + seq);
			System.out.println("Current Base is " + base);
			System.out.println("Current buffer_Space is" + bufferSize);
			echoData echo = new echoData(line, seq, this.b_size);
			this.addData(seq, echo);
		}
		
		System.out.println("-----------------------------------");
		System.out.println("New window look like: ");
		this.printWindow();
		System.out.println("-----------------------------------");
	}
	
}
