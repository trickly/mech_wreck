package com.mechwreck;

/**
 * Implemented by all classes that should be updated.
 */
public interface Updatable {

	/**
	 * Called when the object sould be updated.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Object is updated.
	 */
	public void update();

}
