package com.mechwreck.wireless;

/**
 * Message sent when the game is over.
 */
public class GameOverMessage {
	
	private int winner;
	
	public GameOverMessage() {
		
	}
	
	public GameOverMessage(int winner) {
		this.winner = winner;
	}
	
	public int getWinner() {
		return winner;
	}

}
