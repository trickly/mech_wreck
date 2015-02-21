package com.mechwreck.wireless;

/**
 * Represents a single players input state.
 */
public class InputState {
	
	private boolean moveRight;
	private boolean moveLeft;
	private boolean jump;
	
	/**
	 * Creates a new input state.
	 */
	public InputState() {
	}
	
	/**
	 * Returns true if the player is moving right.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public boolean isMoveRight() {
		return moveRight;
	}
	
	/**
	 * Sets whether or not the player is moving right.
	 * 
	 * pre:
	 * None.
	 * post:
	 * moveRight is set.
	 */
	public void setMoveRight(boolean moveRight) {
		this.moveRight = moveRight;
	}

	/**
	 * Returns true if the player is moving left.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public boolean isMoveLeft() {
		return moveLeft;
	}

	/**
	 * Sets whether or not the player is moving left.
	 * 
	 * pre:
	 * None.
	 * post:
	 * moveRight is set.
	 */
	public void setMoveLeft(boolean moveLeft) {
		this.moveLeft = moveLeft;
	}

	/**
	 * Returns true if the player is jumping.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public boolean isJump() {
		return jump;
	}

	/**
	 * Sets whether or not the player is jumping.
	 * 
	 * pre:
	 * None.
	 * post:
	 * moveRight is set.
	 */
	public void setJump(boolean jump) {
		this.jump = jump;
	}

}
