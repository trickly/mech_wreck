package com.mechwreck.gameobjects;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * Individual planet bits that make up the planet.
 */
public class PlanetBit {
	
	private boolean filled;
	private boolean fixtureDestroyed;
	private Fixture fixture;
	
	/**
	 * Creates a new planet bit.
	 */
	public PlanetBit() {
		filled = true;
	}
	
	/**
	 * Returns true if the bit is filled.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public boolean isFilled() {
		return filled;
	}
	
	/**
	 * Sets the planet bit to be filled.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The planet bit is filled.
	 */
	public void setFilled(boolean filled) {
		this.filled = filled;
		//Prevents infinite mech jumps.
		if(!filled && !fixtureDestroyed) {
			for(Contact contact : fixture.getBody().getWorld().getContactList()) {
				if(contact.isTouching()) {
					if(contact.getFixtureA() == fixture) {
						if(contact.getFixtureB().getBody().getUserData() instanceof Mech) {
							Mech mech = (Mech)contact.getFixtureB().getBody().getUserData();
							if(contact.getFixtureB() == mech.getSensor()) {
								mech.setContacts(mech.getContacts() - 1);
							}
						}
					}
					if(contact.getFixtureB() == fixture) {
						if(contact.getFixtureA().getBody().getUserData() instanceof Mech) {
							Mech mech = (Mech)contact.getFixtureA().getBody().getUserData();
							if(contact.getFixtureA() == mech.getSensor()) {
								mech.setContacts(mech.getContacts() - 1);
							}
						}
					}
				}
			}
			fixture.getBody().destroyFixture(fixture);
			fixtureDestroyed = true;
		}
	}
	
	/**
	 * Set's the planet bit's fixture.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The planet bit's fixture has been set.
	 */
	public void setFixture(Fixture fixture) {
		this.fixture = fixture;
		fixtureDestroyed = false;
	}
	 
}
