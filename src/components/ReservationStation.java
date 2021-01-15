package components;

import engine.Engine;
import utility.OP;

public class ReservationStation {
	private boolean busy;
	private OP op;
	private double vJ, vK;
	private ReservationStation qJ, qK;
	public int A;
	private double result = 0;
	private int timeRemExec;
	private Engine engine;
	public OP getOp() {
		return op;
	}

	public String getop() {
		if(op==OP.ADD)
			return "ADD";
		else if (op==OP.SUB) 
			return "SUB";
		else if (op==OP.MUL) 
			return "MUL";	
		else if (op==OP.DIV) 
			return "DIV";	
		else if (op==OP.STORE) 
			return "STORE";	
		else if (op==OP.LOAD) 
			return "LOAD";	
		else return"null";
		
	}

	public double getvJ() {
		return vJ;
	}



	public double getvK() {
		return vK;
	}

	
	public String getqJ() {
		if(qJ==null)
			return"empty";
		else return"ref";
	}



	public String getqK() {
		if(qK==null)
			return"empty";
		else return"ref";	}


	public String getA() {
		return A+"";
	}


	public String getResult() {
		return result+"";
	}



	public String getTimeRemExec() {
		return timeRemExec+"";
	}






	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	public ReservationStation(Engine engine) {
		busy = false;
		this.engine = engine;
		
	}
	
	public boolean isBusy() {
		return busy;
	}
	public String getBusy() {
		if(busy)
			return "true";
		else 
			return"false";

	}
	public void issueInst(OP op, double vJ, double vK, ReservationStation qJ, ReservationStation qK, int A) {
		this.op = op;
		this.vJ = vJ;
		this.vK = vK;
		this.qJ = qJ;
		this.qK = qK;
		this.A = A;
		timeRemExec = op.execTime();
		busy = true;
	}
	
	public void flushInst(){
		busy = false;
	}
	
	private void setResult() {
		switch(this.op) {
        case ADD: result =  vJ + vK;return;
        case SUB: result =  vJ - vK;return;
        case MUL: result =  vJ * vK;return;
        case DIV: result =  vJ / vK;return;
        case LOAD: result = engine.mem.readDouble(A);return;
        case STORE:engine.mem.writeDouble(A, this.vJ);return;

        default: result =  0;return;
		}
	}
	
	public void run() {
		if(qJ==null && qK==null && busy) {
			timeRemExec--;
			if(timeRemExec == 0) {
				setResult();
			}
			else if(timeRemExec == -1 && this.op != OP.STORE) {
				busy = false;
				engine.publish(this, result);
			}
			else if(timeRemExec == -1 && this.op == OP.STORE){
				busy = false;
			}
		}
	}
	public void writeBack(ReservationStation rs ,double res) {
		if(rs==qJ)
		{
			vJ =res;
			qJ=null;
		}
		
		if(rs==qK)
		{
			vK =res ;
			qK=null;
		}
	}
	
	public String toString() {
		String output = "";
		
		output += "isBusy: " + busy + "  ";
		output += "OP: " + op + "  ";
		output += "vJ: " + vJ +"  ";
		output += "vK: " + vK + "\n";
		
		output += "qJ: " + qJ + "  ";
		output += "qK: " + qK + "  ";
		output += "A: " + A +"\n";
		
		output += "result: " + result + "  ";
		output += "Time Remaining: " + timeRemExec + "\n\n\n";
		
		return output;
	}
}
