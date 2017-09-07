public class RowCol {
	
	private int row;
	private int col;
	
	public RowCol(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	public boolean equals(RowCol other) {
		if (row == other.getRow() && col == other.getCol())
			return true;
		return false;
	}
	
	public String toString() {
		return row + " x " + col;
	}
}
