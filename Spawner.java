package com.mechwreck;

/*
 * Implemented by all spawner classes. A spawner's job is to store
 * spawn data, be sent across the network then spawn the object on
 * all devices.
 */
public interface Spawner {
	
	/**
	 * Called when the object should be spawned.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Object is spawned.
	 */
	public void spawn(GameWorld gameWorld);

}
