package engine;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import javax.naming.spi.DirStateFactory.Result;
import javax.xml.transform.Templates;

import components.InstructionUnit;
import components.MemoryUnit;
import components.RegisterFile;
import components.ReservationStation;
import utility.Instruction;
import utility.OP;
import utility.RFEntry;

public class Engine {
	int PC = 0;
	int cycle = 1;
	
	public RegisterFile registerFile;
	public InstructionUnit insUnit;
	public MemoryUnit mem ;
	
	public ReservationStation[] muls;
	public ReservationStation[] adds;
	public ReservationStation[] loads;
	public ReservationStation[] stores;
	
	public Queue<RFEntry> buffer;
	String [] test = {"add 0 1 2","sub 0 1 2"};
	
	public Engine() {
		adds = new ReservationStation[3];
		muls = new ReservationStation[2];
		loads = new ReservationStation[4];
		stores = new ReservationStation[4];
		
		buffer = new LinkedList<RFEntry>();
		
		for(int i = 0;i<adds.length;i++) {
			adds[i] = new ReservationStation(this);
		}
		for(int i = 0;i<muls.length;i++) {
			muls[i] = new ReservationStation(this);
		}
		for(int i = 0;i<loads.length;i++) {
			loads[i] = new ReservationStation(this);
		}
		for(int i = 0;i<stores.length;i++) {
			stores[i] = new ReservationStation(this);
		}
		insUnit= new InstructionUnit();
		registerFile = new RegisterFile();
		mem=new MemoryUnit();
	}
	
	public void publish(ReservationStation rs, double result) {
		RFEntry rf = new RFEntry(result, rs);
		buffer.add(rf);
	}
	
	public void reserveLocation(ReservationStation rs, int dest) {
		registerFile.registerFile[dest].qI = rs;
	}
	
	public void writeBack() {
		RFEntry rf = buffer.poll();
		if(rf!=null) {
			for(int i = 0;i<adds.length;i++) {
				adds[i].writeBack(rf.qI, rf.value);
			}
			for(int i = 0;i<muls.length;i++) {
				muls[i].writeBack(rf.qI, rf.value);
			}
			for(int i = 0;i<stores.length;i++) {
				stores[i].writeBack(rf.qI, rf.value);
			}
			registerFile.writeBack(rf.qI, rf.value);
		}
	}
	
	public void execute() {
		for(int i = 0;i<adds.length;i++) {
			adds[i].run();
		}
		for(int i = 0;i<muls.length;i++) {
			muls[i].run();
		}
		for(int i = 0;i<stores.length;i++) {
			stores[i].run();
		}
		for(int i = 0;i<loads.length;i++) {
			loads[i].run();
		}

	}
	
	public void issue() {
		if(insUnit.instructionUnit.size()<=PC) {
			System.out.println("cant issue");
			return;
		}
		Instruction inst = insUnit.instructionUnit.get(PC);
		ReservationStation rs = null;
		
		// Check for structural hazard adds
		if(inst.op==OP.ADD||inst.op==OP.SUB) {
			for(int i=0;i<adds.length;i++) {
				if(!adds[i].isBusy()) {
					rs=adds[i];
					break;
				}
			}
		}
		
		// Check for structural hazard muls
		if(inst.op==OP.MUL||inst.op==OP.DIV) {
			for(int i=0;i<muls.length;i++) {
				if(!muls[i].isBusy()) {
					rs=muls[i];
					break;
				}
			}
		}
		// HERE: checked isBusy
		// Check if we can perform store
		if(inst.op==OP.STORE) {
			// Dependency on previous store
			for(int i=0;i<stores.length;i++) { 
				if(stores[i].isBusy() && stores[i].A==inst.reg2) { 
					return;
				}
			}
			// Dependency on previous load
			for(int i=0;i<loads.length;i++) { 
				if(stores[i].isBusy() && loads[i].A==inst.reg2) { 
					return;
				}
			}
			// HERE: commented
			// Check for structural hazard stores
			for(int i=0;i<stores.length;i++) {
				if(!stores[i].isBusy()) {
					rs=stores[i];            
					break;
				}
			}
		}
		//HERE: again isBusy
		// Check if we can perform load
		if(inst.op==OP.LOAD) {
			System.out.println("FOUND LOAD");
			for(int i=0;i<stores.length;i++) { 
				if(stores[i].isBusy() && stores[i].A==inst.reg2) {
					return;
				}
			}
			
			// Check for structural hazard loads
			for(int i=0;i<loads.length;i++) {
				if(!loads[i].isBusy()) {
					rs=loads[i];
					break;
				}
			}
		}
		if(rs!=null&&(inst.op==OP.ADD||inst.op==OP.SUB||inst.op==OP.MUL||inst.op==OP.DIV)) {
			RFEntry entry1 = registerFile.getEntry(inst.reg1);
			RFEntry entry2 = registerFile.getEntry(inst.reg2);
			double vJ = entry1.value;
			double vK = entry2.value;
			ReservationStation qJ = entry1.qI;
			ReservationStation qK = entry2.qI;
			
			reserveLocation(rs, inst.dest);
			rs.issueInst(inst.op, vJ, vK, qJ, qK, inst.A);
			
			PC++;
		}
		if(rs!=null&&(inst.op==OP.STORE||inst.op==OP.LOAD)) {
			RFEntry entry1 = registerFile.getEntry(inst.reg1);
			double vJ = entry1.value;
			ReservationStation qJ = entry1.qI;
			
			if(inst.op == OP.LOAD) {
				reserveLocation(rs, inst.dest);
			}
			rs.issueInst(inst.op, vJ, 0, qJ, null, inst.A);
			
			PC++;
		}
		
	}
	
	
	public void displayCycleInfo() {
		
		System.out.println(registerFile);
		System.out.println("*********************");
		for(int i = 0;i<adds.length;i++) {
			if(adds[i].isBusy())
				System.out.println(adds[i].toString());
		}
		for(int i = 0;i<muls.length;i++) {
			if(muls[i].isBusy())
				System.out.println(muls[i].toString());
		}
		for(int i = 0;i<stores.length;i++) {
			if(stores[i].isBusy())
				System.out.println(stores[i].toString());
		}
		for(int i = 0;i<loads.length;i++) {
			if(loads[i].isBusy())
				System.out.println(loads[i].toString());
		}
	}
	// Writing Back
	// Execute
	// Issue
	
	public void runCycle() {
		writeBack();
		execute();
		issue();		
		displayCycleInfo();
		System.out.println("---------------------------------");
	}
	
	public static void main(String[]args) {
		Engine eng = new Engine();
		eng.insUnit.instructionUnit.add(new Instruction(OP.STORE, 3, 0, 0, 2));
		eng.insUnit.instructionUnit.add(new Instruction(OP.LOAD, 1, 0, 0, 2));
		  GUI f = new GUI(eng);
		  f.engine=eng; 
		    f.setVisible(true);
	}
	

	public String[][]  getInsts() {
		String [][] temp =new String[this.insUnit.instructionUnit.size()][1];
		for(int i=0;i<temp.length;i++) {
				temp[i][0]=insUnit.instructionUnit.get(i).toString();
		}
		return temp;
	}
	
	public String[][]  getRegFile() {
		String [][] temp =new String[this.registerFile.registerFile.length][2];
		for(int i=0;i<temp.length;i++) {
				temp[i][0]=registerFile.registerFile[i].value+"";
				temp[i][1]=registerFile.registerFile[i].qI+"";
		}
		return temp;
	}
	
	public String[][]  getMem() {
		String [][] temp =new String[this.mem.memo.length][1];
		for(int i=0;i<temp.length;i++) {
				temp[i][0]=mem.memo[i]+"";
		}
		return temp;
	}
	
	public String[][]  getAdds() {
		String [][] temp =new String[this.adds.length][7];
		for(int i=0;i<temp.length;i++) {
				temp[i][0]=adds[i].getBusy();
				temp[i][1]=adds[i].getop();
				temp[i][2]=""+adds[i].getvJ();
				temp[i][3]=""+adds[i].getvK();
				temp[i][4]=adds[i].getqJ();
				temp[i][5]=adds[i].getqK();
				temp[i][6]=adds[i].getTimeRemExec();

			
		}
		return temp;
	}
	
	public String[][]  getMuls() {
		String [][] temp =new String[this.muls.length][7];
		for(int i=0;i<temp.length;i++) {
				temp[i][0]=muls[i].getBusy();
				temp[i][1]=muls[i].getop();
				temp[i][2]=""+muls[i].getvJ();
				temp[i][3]=""+muls[i].getvK();
				temp[i][4]=muls[i].getqJ();
				temp[i][5]=muls[i].getqK();
				temp[i][6]=muls[i].getTimeRemExec();

			
		}
		return temp;
	}
	
	public String[][]  getLoads() {
		String [][] temp =new String[this.loads.length][4];
		for(int i=0;i<temp.length;i++) {
				temp[i][0]=loads[i].getBusy();
				temp[i][1]=loads[i].getop();
				temp[i][2]=loads[i].getA();
				temp[i][3]=loads[i].getTimeRemExec();
		}
		return temp;
	}
	
	public String[][]  getStores() {
		String [][] temp =new String[this.stores.length][6];
		for(int i=0;i<temp.length;i++) {
				temp[i][0]=stores[i].getBusy();
				temp[i][1]=stores[i].getop();
				temp[i][2]=""+stores[i].getvJ();
				temp[i][3]=stores[i].getqJ();
				temp[i][4]=stores[i].getA();
				temp[i][5]=stores[i].getTimeRemExec();
		}
		return temp;
	}
}
