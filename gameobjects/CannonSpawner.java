package com.mechwreck.gameobjects;

import com.badlogic.gdx.math.Vector2;
import com.mechwreck.GameWorld;
import com.mechwreck.Spawner;

/**
 * Class that spawns a cannon projectile.
 */
public class CannonSpawner implements Spawner {
	
	private Vector2 position;
	private Vector2 velocity;

	/**
	 * Creates a new Cannon Spawner.
	 */
	public CannonSpawner() {
	}
	
	/**
	 * Creates a new Cannon Spawner.
	 */
	public CannonSpawner(Vector2 position, Vector2 velocity) {
		this.position = position;
		this.velocity = velocity;
	}

	/**
	 * Spawns a cannon projectile.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Cannon projectile is spawned.
	 */
	@Override
	public void spawn(GameWorld gameWorld) {
		new Cannon(gameWorld, position, velocity);
	}

}
