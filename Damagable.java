package com.mechwreck;

import com.badlogic.gdx.math.Vector2;

/*
 * All damageable objects implement this class.
 */
public interface Damagable {

	/**
	 * Called when the object takes damage.
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public void takeDamage(Vector2 position, float radius, float amount);

}
