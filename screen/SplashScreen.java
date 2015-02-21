package com.mechwreck.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Displays the splash screen at the start of the game.
 */
public class SplashScreen implements Screen {
	
	private Music music;
	private Game game;
	private SpriteBatch batch;
	private Sprite splash;
	private float time;
	
	/**
	 * Creates a new splash screen.
	 */
	public SplashScreen(Game game) {
		this.game = game;
		music = Gdx.audio.newMusic(Gdx.files.internal("sounds/Busy Being Born.mp3"));
		music.setVolume(1.0f);
		music.setLooping(true);
		music.play();
	}
	
	/**
	 * Renders the splash screen.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The splash screen is rendered.
	 */
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		splash.setColor(1,1,1, (float)Math.sin(time / 2));
		splash.draw(batch);
		batch.end();
		
		time += Gdx.graphics.getRawDeltaTime();
		if(time > Math.PI * 2) {
			game.setScreen(new MainMenuScreen(game));
		}
		
	}

	/**
	 * Called when the screen is resized.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public void resize(int width, int height) {
	}

	/**
	 * Called when the screen is first shown.
	 * 
	 * pre:
	 * None
	 * post:
	 * The screen is initialized.
	 */
	@Override
	public void show() {
		batch = new SpriteBatch();
		
		Texture sands = new Texture("textures/splash.png");
		splash = new Sprite (sands);
		splash.setSize(750,200);
		splash.setPosition((Gdx.graphics.getWidth() - splash.getWidth()) / 2, (Gdx.graphics.getHeight() - splash.getHeight()) / 2);
		
		time = 0;
	}

	/**
	 * Called when the screen is hidden.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public void hide() {
	}

	/**
	 * Called when the screen is paused.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public void pause() {
	}

	/**
	 * Called when the screen is resumed.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public void resume() {
	}

	/**
	 * Called when the screen is disposed.
	 * 
	 * pre:
	 * The screen is not disposed.
	 * post:
	 * The screen is disposed.
	 */
	@Override
	public void dispose() {
	}

}
