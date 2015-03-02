package com.poritz.tetrisgame;
import java.awt.*; 
import java.awt.event.*;
import javax.swing.*;
import java.util.Random; 
import java.util.Arrays; 

/* ~Changes since the original~
 * Features:
 * -DONE: Introducing keyboard controls, rather than moving via user input with scanner
 * -DONE: More responsive controls (updating the board every 0.1 seconds rather than every 0.5)
 * -DONE: Make a level system, where the player reaches a new level every x points, with the pieces falling faster 
 * -Add a pause feature
 * 
 * Bugs:
 * -FIXED: Completing multiple rows only clears the first one (until the next piece lands)
 * -FIXED: Game overs for no reason? --> Setting board[i + 1] = board[i] in clearRow() made 2 rows into the same reference object
 * -FIXED: Couldn't rotate at the right edge or bottom of the board in some instances
 * -FIXED: Possible to overlap pieces if you rotate just at the right moment (currently the canRotate() method only checks the left/right edges of the board as boundaries)
 * -FIXED: Can also move left/right through pieces in certain cases (the algorithm didn't cover a bunch of cases)
 * 
 *  Misc:
 *  -Code cleaned up in various places (cut unused code, shortened code in some places)
 *  (Note: I am aware that the code can still be significantly condensed, but I chose to leave it in a form I believe is most readable)
 *  -Added some comments for myself
 */

public class Board  {
	static Color[] levelColors = {Color.BLUE, new Color(0x006600), new Color(0xA32900), new Color(0x4B4BBA), new Color(0x7C5329)};
	static Random rand = new Random();
	Graphics g; 
	boolean [][] board;
	Color[][] boardColors;
	boolean [][] currentPiece;
	Color currentColor; 
	int row;
	int col; 
	int score;
	int level;
	boolean gameOver;

	public void run() {

		//This starts the Game using Drawing Panel 
		DrawingPanel game = new DrawingPanel (400,600); 
		game.setBackground(Color.GRAY); 

		//Sets up Graphics and Font   
		g = game.getGraphics(); 

		g.setFont (new Font ("monospaced",Font.BOLD + Font.ITALIC, 50)); 
		g.drawString("TETRIS 2015", 20, 40); 

		//We initialize the board as a 2-D boolean array, the initial values of the board is false  
		board = new boolean [20][10];     
		boardColors = new Color[20][10];
		for (int i = 0; i < boardColors.length; i++) {
			Arrays.fill(boardColors[i], levelColors[0]);
		}

		// Attach key bindings
		JPanel panel = game.getPanel();

		panel.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "left");
		panel.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "right");
		panel.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "down");
		panel.getInputMap().put(KeyStroke.getKeyStroke("UP"), "rotate");

		panel.getActionMap().put("left", new AbstractAction() {
			public void actionPerformed(ActionEvent e) { movePieceLeft(); }
		});
		panel.getActionMap().put("right", new AbstractAction() {
			public void actionPerformed(ActionEvent e) { movePieceRight(); }
		});
		panel.getActionMap().put("down", new AbstractAction() {
			public void actionPerformed(ActionEvent e) { movePieceDown(); }
		});
		panel.getActionMap().put("rotate", new AbstractAction() {
			public void actionPerformed(ActionEvent e) { rotate(); }
		});
		
		level = 1;

		//We then draw a New Piece       
		newPiece();

	}

	// Creates a random new piece
	public void newPiece() { 
		int num = rand.nextInt(7);
		currentPiece = Pieces.PIECES[num]; 
		currentColor = Pieces.COLORS[num]; // The corresponding color has the same position
		row = 0;
		col = 4; // Start in the middle of the board
	}
	
   /*This method draws the board out on Drawing Panel so it looks like a grid. 
   It also draws out the score and level*/ 
	public void printBoard () {
		for (int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[0].length; j++) {
				g.setColor(boardColors[i][j]);
				g.fillRect(100 + (21 * j), 100 + (21 * i), 20, 20); // Horizontal position first
			}
            
			// Draw the score and level boxes
			g.setColor(Color.GRAY);
			g.fillRect(100, 50, 350, 50);
			g.setColor(Color.GREEN); 
			g.setFont (new Font ("monospaced",Font.BOLD + Font.ITALIC, 25));        
			g.drawString("Score: " + score, 130, 70);
			g.setFont (new Font ("monospaced",Font.BOLD + Font.ITALIC, 18));
			g.drawString("Level: " + level, 150, 90);
		}

	}
	
	/*Game Over Method*/ 
	public boolean gameOver() { 
		if (gameOver) {
			g.setColor(Color.YELLOW);
			g.fillRect(100, 310, 210, 100);
			g.setColor(Color.BLUE); 
			g.setFont (new Font ("monospaced",Font.BOLD + Font.ITALIC, 30));        
			g.drawString("GAME OVER" , 120, 350);
			g.setFont (new Font ("monospaced", Font.BOLD, 16)); 
			g.drawString(" Your score is:" + score, 130, 380);
			return true;
		}
		return false;
	}

	/*This method checks to see the Pieces True/False value,
   if its True, it will draw the shape onto the Board and fill the 
   rectangle.*/  
	public void drawPiece() {
		for (int i = 0; i < currentPiece.length; i++) {
			for (int j = 0; j < currentPiece[0].length; j++) {
				if (currentPiece[i][j]) {
					g.setColor(currentColor);
					g.fillRect((col + j) * 21 + 100, (row + i) * 21 + 100, 20, 20);
				}
			}
		}
	}

	public void movePieceDown() {
		if (canMoveDown()) {
			row++;
		}
		else {
			saveToBoard();
		}
	}

	public void movePieceLeft() {
		if (canMoveLeft()) {
			col--;
		}
	}

	public void movePieceRight() {
		if (canMoveRight()) {
			col++;
		}
	}

	public boolean canMoveLeft() { 
		if (col == 0) {
			return false;
		}
		return fitsInBoard(currentPiece, 0, -1);
	}

	public boolean canMoveRight() { 
		if (col + currentPiece[0].length > 9) {
			return false;
		}
		return fitsInBoard(currentPiece, 0, 1);
	}
  
	public boolean canMoveDown() {
		if (row + currentPiece.length > 19) {
			return false;
		}
		return fitsInBoard(currentPiece, 1, 0);
	}

	/*This saves the piece to the board when it is no longer moving*/ 
	public void saveToBoard() {
		for (int i = 0; i < currentPiece.length; i++) {
			for (int j = 0; j < currentPiece[0].length; j++) {
                // Save piece's position and color to the board
				if (currentPiece[i][j]) { 
				    board[row + i][col + j] = true;
					boardColors[row + i][col + j] = currentColor;
				}
			}
		}
		//After the piece is saved, it checks to see if the row is full
		checkRows();  
		//A new piece than comes out as well.
		newPiece();
		//If that new piece doesn't fit, it's game over
		if (!fitsInBoard(currentPiece)) {
			gameOver = true;
		}
	}

    //Checks if a row is full, and if it is, clear it
	public void checkRows() { 

		boolean[] fullRow = new boolean[10]; 
		Arrays.fill(fullRow, true); // Represents a full row

		for (int i = board.length - 1; i >= 0; i--) {
			if (Arrays.equals(board[i], fullRow)) { // Check each row to see if its the same as a full row
				clearRow(i); 
				i++;  // Check the current row again, in case two rows were filled at the same time (the next row would have shifted down one, and now the current row is full again)
				score += 100;
				setLevel();
			}
		}
	}
	
	/*This method clears the row that is full*/ 
	public void clearRow(int row) { 
		
		// Every row shifts down one
		for (int i = row - 1; i >= 0; i--) {
			board[i + 1] = Arrays.copyOf(board[i], board[i].length); // Bug note: The original board[i + 1] = board[i] caused problems because then they refereed to the same array
			boardColors[i + 1] = Arrays.copyOf(boardColors[i], boardColors[i].length);
		} 
        
		// Reset top row
		Arrays.fill(board[0], false); 
		Arrays.fill(boardColors[0], levelColors[level - 1]); 
	}

	//This method enables the piece to be rotated.     
	public void rotate () {

		boolean[][] rotatedPiece = new boolean[currentPiece[0].length][currentPiece.length];

		for (int i = 0; i < currentPiece[0].length; i++) {
			boolean[] col = new boolean[currentPiece.length]; // Each column of the original piece becomes a row in the rotated piece	
			for (int j = 0; j < currentPiece.length; j++) {
				col[j] = currentPiece[j][i];			
			}		
			rotatedPiece[currentPiece[0].length - 1 - i] = col; // 1st column becomes last row, 2nd column becomes next to last row, etc
		}

		if (fitsInBoard(rotatedPiece)) {
			currentPiece = rotatedPiece;
		}  
	}
	
	public boolean fitsInBoard(boolean[][] piece) {
		return fitsInBoard(piece, 0, 0);
	}

	//This checks if a piece – shifted down and right by an amount – fits in the board
	public boolean fitsInBoard(boolean[][] piece, int downShift, int rightShift) {
		if (row + (piece.length - 1) > 19 || col + (piece[0].length - 1) > 9) {
			return false;
		}
		for (int i = 0; i < piece.length; i++) {
			for (int j = 0; j < piece[0].length; j++) {
				if (piece[i][j] && board[row + i + downShift][col + j + rightShift]) {
					return false;
				}
			}
		}
	    return true;
	}
	
	// Checks if the player has gotten to the next level, and changes the background color
	public void setLevel() {
		if (level < 5 && score >= level * 2000) {
			level++;
			for (int i = 0; i < board.length; i++) {
				for(int j = 0; j < board[0].length; j++) {
					if (boardColors[i][j] == levelColors[level - 2]) {  // Old background color
						boardColors[i][j] = levelColors[level - 1];  // New background color
					}
				}
			}
		}	
	}
	
	// A formula for the speed that the piece should be falling. The game gets faster at higher levels
	public double getSpeed() {
		return (8 - level) / 10.0;
	}
}
