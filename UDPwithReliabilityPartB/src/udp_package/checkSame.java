package udp_package;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class checkSame {
	StringBuilder s = new StringBuilder();
	
	public void readData() throws FileNotFoundException, IOException {
		try (BufferedReader br = new BufferedReader(new FileReader("/Users/yingjianwu/Documents/Java/LeetcodePractice/linkedList/UDPwithReliabilityPartB/src/MobyDick.txt"))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	//if window_size is not full;
		    	s.append(line);
		    }
		    br.close();
		}
	}
	
	public String getString() {
		return this.s.toString();
	}
}
