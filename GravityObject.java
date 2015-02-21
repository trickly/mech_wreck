package com.mechwreck;

import com.badlogic.gdx.math.Vector2;

/**
 * An interface implemented by anything that has gravity.
 */
public interface GravityObject {

	public final static float GRAVITY_CONSTANT = 5000;

	/**
	 * Calculates the gravity applied by this object on other objects.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public Vector2 calculateAcceleration(Vector2 position, float mass);

}
