package utility;

public enum OP {
	ADD, SUB, MUL, DIV, LOAD, STORE;
	
	public int execTime(){ 
        switch(this) {
	        case ADD:
	        case SUB: return 4;
	        case MUL: 
	        case DIV: return 4;
	        case LOAD:
	        case STORE: return 4;
	        default: return 0;
        }
    }

}
