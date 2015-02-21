package com.mechwreck.wireless;

/**
 * Message sent when input is received.
 */
public class InputMessage {
	
	private int player;
	private InputType type;
	private boolean down;
	private float shootAngle;
	private int weapon;
	
	/**
	 * Creates a new input message.
	 */
	public InputMessage() {
	}

	/**
	 * Creates a new input message.
	 */
	public InputMessage(int player, InputType type, boolean down, float shootAngle, int weapon) {
		this.player = player;
		this.type = type;
		this.down = down;
		this.shootAngle = shootAngle;
		this.weapon = weapon;
	}
	
	/**
	 * Gets which player input this input.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public int getPlayer() {
		return player;
	}
	
	/**
	 * Gets the type of input.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public InputType getType() {
		return type;
	}
	
	/**
	 * Gets whether the input is down or up.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public boolean isDown() {
		return down;
	}
	
	/**
	 * Gets the angle of the shot if this is shooting input.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public float getShootAngle() {
		return shootAngle;
	}
	
	/**
	 * Gets the shot's weapon if this is a shooting input.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public int getWeapon() {
		return weapon;
	}
	
	/**
	 * List of all the types of input.
	 */
	public static enum InputType {
		MOVE_RIGHT,
		MOVE_LEFT,
		JUMP,
		SHOOT
	}

}
