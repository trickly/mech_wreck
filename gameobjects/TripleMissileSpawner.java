package com.mechwreck.gameobjects;

import com.badlogic.gdx.math.Vector2;
import com.mechwreck.GameWorld;
import com.mechwreck.Spawner;

/**
 * Spawner class for the triple missile weapon.
 */
public class TripleMissileSpawner implements Spawner {
	
	private Vector2 position;
	private Vector2 velocity;

	/**
	 * Creates a new triple missile spawner.
	 */
	public TripleMissileSpawner() {
	}
	
	/**
	 * Creates a new triple missile spawner.
	 */
	public TripleMissileSpawner(Vector2 position, Vector2 velocity) {
		this.position = position;
		this.velocity = velocity;
	}

	/**
	 * Spawns a new triple missile projectile.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Triple missile spawned.
	 */
	@Override
	public void spawn(GameWorld gameWorld) {
		new TripleMissile(gameWorld, position, velocity);
	}

}
