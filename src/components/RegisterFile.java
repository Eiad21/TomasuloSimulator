package components;

import utility.*;
import components.*;
import java.util.*;

public class RegisterFile {
	public RFEntry[] registerFile;
	
	public RegisterFile() {
		registerFile = new RFEntry[5];
		for(int i = 0;i<registerFile.length;i++) {
			registerFile[i] = new RFEntry();
			registerFile[i].value = i+1;
		}
	}
	public RFEntry getEntry(int index) {
		return registerFile[index];
	}
	
	public void writeBack(ReservationStation rs ,double res) {
		for(RFEntry x:this.registerFile) {
			if(rs==x.qI)
			{
				x.value =res ;
				x.qI=null;
			}	
		}
	}
	
	public String toString() {
		String out = "";
		for(int i = 0;i<registerFile.length;i++) {
			out+= registerFile[i] + "\n";
		}
		return out;
	}
}
