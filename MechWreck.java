package com.mechwreck;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.mechwreck.gameobjects.ParticleEffects;
import com.mechwreck.screen.SplashScreen;
import com.mechwreck.wireless.WifiHelper;

/**
 * Entrance point for the application. Starts the splash screen.
 */
public class MechWreck extends Game {

	/**
	 * Called when the game is created.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Game is created/started.
	 */
	@Override
	public void create() {		
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.input.setInputProcessor(new InputMultiplexer());
		Texture.setEnforcePotImages(false);

		setScreen(new SplashScreen(this));
		
		//Fixes lag bug
		ParticleEffects.YOLO();

	}
	
	/*
	 * Clears the screen then renders the current screen.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Game is rendered.
	 */
	@Override
	public void render() {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		super.render();
	}
	
	/*
	 * Disposes of Mech Wreck
	 */
	@Override
	public void dispose() {
		WifiHelper.closeClient();
		WifiHelper.closeServer();
		getScreen().dispose();
		super.dispose();
	}
}
