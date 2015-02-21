package com.mechwreck;

import com.badlogic.gdx.graphics.Camera;

/*
 * All renderable objects implement this class.
 */
public interface Renderable {

	/**
	 * Called when the object needs to be rendered.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Object is rendered.
	 */
	public void render(Camera camera);

}
