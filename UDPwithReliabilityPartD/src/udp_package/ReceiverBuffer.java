package udp_package;

public class ReceiverBuffer {
	private int expectedNumber;
	private int BufferSize;
	
	public ReceiverBuffer (int BufferSize) {
		this.expectedNumber = 0;
		this.BufferSize = BufferSize;
	}
	
	public int getExpectedNumber() {
		return this.expectedNumber;
	}
	
	public void increaExpectedNumber() {
		this.expectedNumber = (this.expectedNumber + 1) % BufferSize;
	}
	
}
