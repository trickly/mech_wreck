package com.mechwreck.wireless;

/**
 * Request sent when a game is started.
 */
public class StartGameRequest {
	
	private int player;
	
	/**
	 * Creates a new start game request.
	 */
	public StartGameRequest() {
	}

	/**
	 * Creates a new start game request.
	 */
	public StartGameRequest(int player) {
		this.player = player;
	}
	
	/**
	 * Gets the player index for the recipient of this request.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public int getPlayer() {
		return player;
	}
	
}
