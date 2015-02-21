package com.mechwreck.gameobjects;

import com.badlogic.gdx.math.Vector2;

/**
 * Data required to sync a particle across devices.
 */
public class ParticleSyncData {
	
	private Vector2 position;
	private Vector2 velocity;
	private boolean dead;

	/**
	 * Creates new particle sync data.
	 */
	public ParticleSyncData() {
	}

	/**
	 * Creates new particle sync data.
	 */
	public ParticleSyncData(Particle particle) {
		this.position = particle.getBody().getPosition();
		this.velocity = particle.getBody().getLinearVelocity();
		this.dead = particle.isDead();
	}
	
	/**
	 * Syncs the particle with this data.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The particle is synced.
	 */
	public void receive(Particle particle) {
		if(dead) {
			particle.dispose();
		} else {
			particle.getBody().setTransform(position, particle.getBody().getAngle());
			particle.getBody().setLinearVelocity(velocity);
		}
	}

}
