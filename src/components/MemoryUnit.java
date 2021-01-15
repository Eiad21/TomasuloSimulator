package components;

public class MemoryUnit {
public double[] memo ;
	public MemoryUnit() {
	memo =new double[50] ;
	}
	
	public double readDouble(int c) {
		return memo[c];
	}
	
	public void writeDouble(int c, double value) {
		 memo[c]=value;
	}

}
