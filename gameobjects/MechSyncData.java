package com.mechwreck.gameobjects;

import com.badlogic.gdx.math.Vector2;

/**
 * Data that must be synced for each mech.
 */
public class MechSyncData {
	
	private Vector2 position;
	private Vector2 velocity;
	private float health; 
	
	/**
	 * Creates a new mech sync data.
	 */
	public MechSyncData() {
	}

	/**
	 * Creates a new mech sync data using the given mech.
	 */
	public MechSyncData(Mech mech) {
		position = mech.getBody().getPosition().cpy();
		velocity = mech.getBody().getLinearVelocity().cpy();
		health = mech.getHealth();
	}
	
	/*
	 * Receives the mech's sync data.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The mech is synced.
	 */
	public void receive(Mech mech) {
		mech.getBody().setTransform(position, 0);
		mech.getBody().setLinearVelocity(velocity);
		mech.setHealth(health);
		if(health <= 0) {
			mech.dispose();
		}
	}

}
