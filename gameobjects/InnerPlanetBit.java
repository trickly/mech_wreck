package com.mechwreck.gameobjects;

/**
 * A planet bit that does not get destroyed into bits. Used in the inner rings of the planet
 * so that there will always be a center core of the planet.
 */
public class InnerPlanetBit extends PlanetBit {
	
	/**
	 * Does nothing.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public void setFilled(boolean filled) {
	}

}
