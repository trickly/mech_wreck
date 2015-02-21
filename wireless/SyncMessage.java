package com.mechwreck.wireless;

import com.mechwreck.Spawner;

/**
 * Message sent to sync between server/client.
 */
public class SyncMessage {
	
	private Object[] syncData;
	private Spawner[] spawners;

	/**
	 * Creates a new sync message.
	 */
	public SyncMessage() {
	}
	
	/**
	 * Creates a new sync message.
	 */
	public SyncMessage(Object[] syncData, Spawner[] spawners) {
		this.syncData = syncData;
		this.spawners = spawners;
	}
	
	/**
	 * Gets the sync data for this message.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public Object[] getSyncData() {
		return syncData;
	}
	
	/**
	 * Get the spawners for this message.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public Spawner[] getSpawners() {
		return spawners;
	}
	
}
