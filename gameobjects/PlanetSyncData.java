package com.mechwreck.gameobjects;

/**
 * Data used to sync planets.
 */
public class PlanetSyncData {

	private float health;
	
	/**
	 * Creates a new planet sync data.
	 */
	public PlanetSyncData() {
	}
	
	/**
	 * Creates a new planet sync data from a planet.
	 */
	public PlanetSyncData(Planet planet) {
		health = planet.getHealth();
	}
	
	/**
	 * Syncs a planet using this data.
	 */
	public void receive(Planet planet) {
		planet.setHealth(health);
		if(health <= 0) {
			planet.dispose();
		}
	}
	
}
