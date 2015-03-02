
/*
December 28th 2014
Liran Weizman 
Semester Project
Intro to Programming with Professor Broder!
 */
// Modified version by Danny Poritz, February 1st 2015 

package com.poritz.tetrisgame;

public class Tetris {

	// return the time in seconds 
	public static double timeInSeconds(){
		return System.currentTimeMillis() / 1000.0;
	}        

	public static void main(String[] args) { 
		
		Board board = new Board(); 

		System.out.print ("Welcome to Tetris Java! This is an exciting game!"); 
		System.out.print (" We are taking the game of your past and making it the game of the present.");
		System.out.println("In order to play this game, \n there are three keys that you have to become familiar with."); 
		System.out.println("To move left: Press left. \nTo move right: Press right. \nTo move your piece down faster, Press down.\nTo rotate your piece: Press up. "); 
		System.out.println("Lets hope you enjoy the game." ); 

		board.run();
        
		double lastDrawTime = timeInSeconds();
		double lastMoveTime = timeInSeconds();
		
		while(!board.gameOver()) { 

			// Every 0.7 to 0.3 seconds (depending on the game level) move the piece down
			if (timeInSeconds() > (lastDrawTime + board.getSpeed())) {

				board.movePieceDown();
				
				lastDrawTime = timeInSeconds();
			}
			
			// Every 0.1 seconds update the board
			if (timeInSeconds() > (lastMoveTime + .1)) {

				//Draw the Board  
				board.printBoard(); 
				
				//Draw the Piece
				board.drawPiece();
				
				lastMoveTime = timeInSeconds();
			}
		}
	}
}