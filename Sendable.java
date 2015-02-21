package com.mechwreck;

/*
 * All classes with messages to be sent between host and client implement this class.
 */
public interface Sendable {
	
	/**
	 * Called to get the object's sync data.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public Object send();
	
	/**
	 * Called when the object should receive sync data.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The object is synced.
	 */
	public void recieve(Object object);

}
