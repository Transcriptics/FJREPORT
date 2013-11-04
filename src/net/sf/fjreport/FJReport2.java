package net.sf.fjreport;

public class FJReport2 {
	
	// my plan about 0.5
	
	// for fjreport 0.5, only 3 state, 
	// design state = drawling_state + cell_state
	// support zoom
	// support 1*n, m * n pages layout
	// support nested cell
	
	public static final int STATE_DESIGN = 1;
	public static final int STATE_EDIT = 2;
	public static final int STATE_PREVIEW = 3;
	
	
	private boolean readOnly;
	
	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setState(int st) {
		
		if (st == STATE_DESIGN) {
			
		} else if (st == STATE_EDIT) {
			
		} else if (st == STATE_PREVIEW) {
			
		}
		
	}
	
	

}
