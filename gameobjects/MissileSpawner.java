package com.mechwreck.gameobjects;

import com.badlogic.gdx.math.Vector2;
import com.mechwreck.GameWorld;
import com.mechwreck.Spawner;

/**
 * Spawner responsible for spawning missiles.
 */
public class MissileSpawner implements Spawner {
	
	private Vector2 position;
	private Vector2 velocity;

	/**
	 * Creates a new missile spawner.
	 */
	public MissileSpawner() {
	}
	
	/**
	 * Creates a new missile spawner.
	 */
	public MissileSpawner(Vector2 position, Vector2 velocity) {
		this.position = position;
		this.velocity = velocity;
	}

	/**
	 * Creates a new missile spawner.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Missile is spawned.
	 */
	@Override
	public void spawn(GameWorld gameWorld) {
		new Missile(gameWorld, position, velocity);
	}

}
