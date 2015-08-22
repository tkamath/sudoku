package sudoku;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class Main {
	
	
	private static ArrayList<GridButton> allSquares;
	private static int filled;
	private final static HashMap<Integer, String> fixed = new HashMap<Integer, String>();

	public static void main(String[] args) {
		
		allSquares = new ArrayList<GridButton>();
		filled = 0;
		File file = promptForGridInfo();
		
		JFrame frame = new JFrame("Sudoku");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(450, 450);
		frame.setLayout(new GridLayout(9, 9));
		
		if (file != null) {
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
				String str = "";
				int i = 0;
				while ((str = bufferedReader.readLine()) != null) {
					if (!str.isEmpty()) {
						fixed.put(i, str);
					}
					i += 1;
				}
				bufferedReader.close();
			} catch (IOException | NumberFormatException e) {
				e.printStackTrace();
			}
		}
		
		for (int k = 0; k < 81; k += 1) {
			final GridButton button = new GridButton(k);
			if (button.getSubGrid() % 2 == 0) {
				button.setBackground(new Color(235, 230, 120));
			} else {
				button.setBackground(new Color(190, 200, 210));
			}
			allSquares.add(button);
		}
		
		for (final GridButton button : allSquares) {
			Font buttonFont = new Font(Font.SANS_SERIF, Font.BOLD, 16); 
			button.setFont(buttonFont);
			final JTextField field = new JTextField();
			field.setFont(buttonFont);
			field.setBackground(null);
			
			if (fixed.containsKey(button.getIndex())) {
				button.setForeground(new Color(130, 0, 65));
				try {
					int num = Integer.parseInt(fixed.get(button.getIndex()));
					enterValue (button, field, num);
					field.setEditable(false);
					field.setVisible(false);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				
			}
			
			field.addKeyListener(new KeyListener () {

				@Override
				public void keyPressed(KeyEvent arg0) {
				
				}

				@Override
				public void keyReleased(KeyEvent arg0) {
				
				}

				@Override
				public void keyTyped(KeyEvent arg0) {
					if (arg0.getKeyChar() == KeyEvent.VK_ENTER) {
						if (!field.getText().isEmpty() || !button.getText().isEmpty()) {
							try {
								if (!field.getText().isEmpty()) {
									int num = Integer.parseInt(field.getText());
									if (num == 0 || num > 9) {
										sendErrorMsg("Input must be an integer from 1 to 9");
									} else {
										if (button.getForbidden().contains(num)) {
											sendErrorMsg("Each integer from 1 to 9 may only appear once in a row, column, or subgrid");
										} else {
											enterValue(button, field, new Integer(num));
										}
									}
								} else {
									enterValue(button, field, (Integer) null);
								}
							} catch (NumberFormatException e) {
								e.printStackTrace();
								sendErrorMsg("Input must be an integer from 1 to 9");
							}
						}
					}
				}
				
			});
			

			field.setVisible(false);
			button.add(field);
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (!fixed.containsKey(button.getIndex())) {
						field.setVisible(true);
						field.setEditable(true);
					}
				}
				
			});
			button.setVisible(true);
			frame.add(button);
		}
		frame.setVisible(true);
		
	}
	
	static void sendErrorMsg(String str) {
		JOptionPane.showMessageDialog(null, str, "Error", JOptionPane.ERROR_MESSAGE);			
	}
	
	static void sendVictoryMsg() {
		JOptionPane.showMessageDialog(null, "You have solved the puzzle!", "Congrats!", JOptionPane.INFORMATION_MESSAGE);			
	}
	
	static File promptForGridInfo() {
		String title = "Enter grid information";
		String instruction = "Enter path to csv file with grid info";
		Object result = JOptionPane.showInputDialog(null, instruction, title, JOptionPane.QUESTION_MESSAGE);
		if (result == null || result.toString().isEmpty()) {
			return null;
		} else {
			File file = new File(result.toString());
			if (file.exists()) {
				return file;
			}
			sendErrorMsg("File does not exist");
			return promptForGridInfo();
		}
	}
	
	static void handleRestOfSubGrid (Integer newVal, Integer oldVal, int index, int row, int col) {
		if (row % 3 == 0) {
			if (col % 3 == 0) {
				handleIndices(newVal, oldVal, index + 10, index + 11, index + 19, index + 20);
			} else if (col % 3 == 1) {
				handleIndices(newVal, oldVal, index + 8, index + 10, index + 17, index + 18);
			} else {
				handleIndices(newVal, oldVal, index + 7, index + 8, index + 16, index + 17);
			}
		} else if (row % 3 == 1) {
			if (col % 3 == 0) {
				handleIndices(newVal, oldVal, index - 8, index - 7, index + 10, index + 11);
			} else if (col % 3 == 1) {
				handleIndices(newVal, oldVal, index - 10, index - 8, index + 8, index + 10);
			} else {
				handleIndices(newVal, oldVal, index - 11, index - 10, index + 7, index + 8);
			}
		} else {
			if (col % 3 == 0) {
				handleIndices(newVal, oldVal, index - 17, index - 16, index - 8, index - 7);
			} else if (col % 3 == 1) {
				handleIndices(newVal, oldVal, index - 19, index - 17, index - 10, index - 8);
			} else {
				handleIndices(newVal, oldVal, index - 20, index - 19, index - 11, index - 10);
			}
		}
	}
	
	static void handleIndices(Integer newVal, Integer oldVal, int num1, int num2, int num3, int num4) {
		HashSet<Integer> squaresLeft = new HashSet<Integer>();
		squaresLeft.add(num1);
		squaresLeft.add(num2);
		squaresLeft.add(num3);
		squaresLeft.add(num4);
		
		for (int ind: squaresLeft) {
			GridButton button = allSquares.get(ind);
			if (oldVal != (Integer) null) {
				button.allowNum(oldVal.intValue());
			}
			if (newVal != (Integer) null) {
				button.forbidNum(newVal.intValue());
			}
		}
	}
	
	static void enterValue (GridButton button, JTextField field, Integer num) {
		boolean changeVal = false;
		boolean deleteVal = false;
		Integer oldVal = (Integer) null;
		
		try {
			if (!button.getText().isEmpty()) {
				int old = Integer.parseInt(button.getText());
				if (num == (Integer) null || old != num.intValue()) {
					oldVal = new Integer(old);
					if (num == (Integer) null) {
						deleteVal = true;
					} else {
						changeVal = true;
					}
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		if (deleteVal) {
			button.setText("");
		} else {
			button.setText(String.valueOf(num.intValue()));
		}
		
		field.setEditable(false);
		field.setVisible(false);
		
		if (deleteVal) {
			filled -= 1;
		} else if (!changeVal) {
			filled += 1;
		}
		
		if (filled == 81) {
			sendVictoryMsg();
			return;
		}
		
		int currentIndex = button.getIndex();
		int currentRow = button.getRow();
		int currentCol = button.getCol();

		for (int c = 0; c < 9; c += 1) {
			if (currentCol != c) {
				GridButton newButton = allSquares.get(currentRow * 9 + c);
				if (deleteVal || changeVal) {
					newButton.allowNum(oldVal.intValue());
				}

				if (!deleteVal) {
					newButton.forbidNum(num.intValue());
				}
			}
		}
		for (int r = 0; r < 9; r += 1) {
			if (currentRow != r) {
				GridButton newButton = allSquares.get(r * 9 + currentCol);
				if (deleteVal || changeVal) {
					newButton.allowNum(oldVal.intValue());
				}
				if (!deleteVal) {
					newButton.forbidNum(num.intValue());
				}
			}
		}
		handleRestOfSubGrid(num, oldVal, currentIndex, currentRow, currentCol);
	}
}
