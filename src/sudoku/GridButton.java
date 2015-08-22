package sudoku;

import java.util.HashSet;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class GridButton extends JButton {

	private int row;
	private int col;
	private int index;
	private int subGrid;
	private HashSet<Integer> forbidden;
	
	
	public GridButton(int index) {
		this.index = index;
		this.row = index / 9;
		this.col = index % 9;
		this.forbidden = new HashSet<Integer> ();
		if (row < 3) {
			subGrid = col / 3;
		} else if (row < 6) {
			subGrid = (col / 3) + 3;
		} else {
			subGrid = (col / 3) + 6;
		}
	}
	
	public void forbidNum(int num) {
		forbidden.add(num);
	}
	
	public void allowNum(int num) {
		if (forbidden.contains(num)) {
			forbidden.remove(num);
		}
	}
	
	int getIndex() {
		return this.index;
	}
	
	int getRow() {
		return this.row;
	}
	
	int getCol() {
		return this.col;
	}
	
	int getSubGrid() {
		return this.subGrid;
	}
	
	HashSet<Integer> getForbidden() {
		return this.forbidden;
	}

}
