package com.mechwreck.wireless;

import com.badlogic.gdx.math.Vector2;

/**
 * Message sent to initialize the game.
 */
public class InitializeMessage {
	
	private Vector2[] planetPositions;
	private Vector2[] mechPositions;
	
	/**
	 * Creates a new Initialize message.
	 */
	public InitializeMessage() {
	}
	
	/**
	 * Creates a new Initialize message.
	 */
	public InitializeMessage(Vector2[] planetPositions, Vector2[] mechPositions) {
		this.planetPositions = planetPositions;		
		this.mechPositions = mechPositions;
	}
	
	/**
	 * Gets an array of the planets positions.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public Vector2[] getPlanetPositions() {
		return planetPositions;
	}
	
	/**
	 * Gets an array of the mechs positions.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public Vector2[] getMechPositions() {
		return mechPositions;
	}

}
