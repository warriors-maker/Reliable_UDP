package udp_package;

import java.io.Serializable;

public class echoData implements Serializable{
	private String sentence;
	private int sequenceNumber;
	private int ackNumber;
	private boolean end;
	private boolean stopSending;
	
	private int BufferSize;
	
	public echoData(boolean end) {
		this.end = end;
	}
	
	public boolean getStop() {
		return this.stopSending;
	}
	
	public echoData(int ackNumber) {
		this.ackNumber = ackNumber;
	}
	
	public echoData(boolean end, boolean stopSending) {
		this.end = end;
		this.stopSending = stopSending;
	}
	
	public echoData(String sentence, int sequenceNumber, int BufferSize) {
		this.sentence = sentence;
		this.sequenceNumber = sequenceNumber;
		this.ackNumber = (sequenceNumber + 1) % BufferSize;
		this.BufferSize = BufferSize;
	}
	
	public int getBufferSize() {
		return this.BufferSize;
	}
	
	public boolean getEnd() {
		return this.end;
	}
	public String getSentence() {
		return this.sentence;
	}
	
	public int getSequenceNumber() {
		return this.sequenceNumber;
	}
	
	public int getAckNumber() {
		return this.ackNumber;
	}
}
