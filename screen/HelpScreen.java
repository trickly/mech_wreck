package com.mechwreck.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mechwreck.Background;
import com.mechwreck.SkinManager;

/**
 * Screen dedicated to displaying the tutorial for the game. The tutorial should
 * include objective, gameplay controls, list of power-ups, and list of weapons
 */
public class HelpScreen implements Screen, InputProcessor {

	private Game game;

	private OrthographicCamera camera;
	private Background background;
	
	private SpriteBatch spriteBatch;
	private Texture help1;
	private Texture help2;
	
	private Stage ui;
	
	int page = 0;

	/**
	 * Creates a new help Screen.
	 */
	public HelpScreen(Game game) {
		this.game = game;
		camera = new OrthographicCamera();
		background = new Background();
		
		ui = new Stage();
		Skin skin = SkinManager.getSkin();
		
		TextButton backButton = new TextButton("BACK", skin.get(TextButtonStyle.class));
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				HelpScreen.this.game.setScreen(new MainMenuScreen(HelpScreen.this.game));
			}
		});
		backButton.setPosition(30, 30);

		ui.addActor(backButton);
		
		spriteBatch = new SpriteBatch();
		help1 = new Texture(Gdx.files.internal("textures/helpscreen.png"));
		help2 = new Texture(Gdx.files.internal("textures/helpscreen2.png"));
	}

	/**
	 * Renders the help screen.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The help screen is rendered.
	 */
	@Override
	public void render(float delta) {
		background.render(camera);
		ui.act();
		ui.draw();
		spriteBatch.begin();
		if(page == 0) {
			spriteBatch.draw(help1, 100, 100, Gdx.graphics.getWidth() - 200,Gdx.graphics.getHeight() - 150);
		} else {
			spriteBatch.draw(help2, 100, 100, Gdx.graphics.getWidth() - 200,Gdx.graphics.getHeight() - 150);
		}
		spriteBatch.end();
	}

	/**
	 * Called when the screen is resized.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The screen is resized.
	 */
	@Override
	public void resize(int width, int height) {
		camera.setToOrtho(false);
		ui.setViewport(width, height);
	}

	/**
	 * Called when the screen is first shown.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Input is registered.
	 */
	@Override
	public void show() {
		((InputMultiplexer) Gdx.input.getInputProcessor()).addProcessor(ui);
		((InputMultiplexer) Gdx.input.getInputProcessor()).addProcessor(this);
	}

	/**
	 * Called when the screen is hidden.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Input is unregistered.
	 */
	@Override
	public void hide() {
		((InputMultiplexer) Gdx.input.getInputProcessor()).removeProcessor(ui);
		((InputMultiplexer) Gdx.input.getInputProcessor()).removeProcessor(this);
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
	 * Help screen is not disposed.
	 * post:
	 * Help screen is disposed.
	 */
	@Override
	public void dispose() {
		background.dispose();
		ui.dispose();
		spriteBatch.dispose();
		help1.dispose();
		help2.dispose();
	}
	
	/**
	 * Moves to the next help page.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Next page is shown.
	 */
	private void nextPage() {
		page++;
		if(page > 1) {
			page = 0;
		}
	}

	/**
	 * Called when a key is pressed.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Next page is shown.
	 */
	@Override
	public boolean keyDown(int keycode) {
		nextPage();
		return false;
	}

	/**
	 * Called when a key is released.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	/**
	 * Called when a key is typed.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	/**
	 * Called when touch screen is pressed.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		nextPage();
		return false;
	}

	/**
	 * Called when touch screen is released.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	/**
	 * Called when touch screen is dragged.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	/**
	 * Called when the mouse is moved.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	/**
	 * Called when the mouse is scrolled.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}
