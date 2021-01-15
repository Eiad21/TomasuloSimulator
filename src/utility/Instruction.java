package utility;

public class Instruction {
public OP op ;
public int dest;
public int reg1;
public int reg2;
public int A;
public Instruction(OP op, int dest, int reg1, int reg2, int A) {
	super();
	this.op = op;
	this.dest = dest;
	this.reg1 = reg1;
	this.reg2 = reg2;
	this.A = A;
}

public String toString() {
	if(op == OP.ADD || op == OP.SUB || op == OP.MUL || op == OP.DIV)
		return op +" F"+dest+" F"+reg1+" F"+reg2;
	else if(op == op.LOAD)
		return op + " F"+dest+" A("+A+")";
	else
		return op + " F"+reg1+" A("+A+")";
}
}
