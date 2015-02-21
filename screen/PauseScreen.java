package com.mechwreck.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mechwreck.SkinManager;

/**
 * Screen that is displayed when the game is paused.
 */
public class PauseScreen implements Screen {
	
	private Game game;
	private GameScreen gameScreen;
	private Stage ui;

	/**
	 * Creates a new pause screen for a given game screen.
	 */
	public PauseScreen(Game game, GameScreen gameScreen) {
		this.game = game;
		this.gameScreen = gameScreen;
		
		ui = new Stage();
		Skin skin = SkinManager.getSkin();

		Table table = new Table();
		table.setFillParent(true);
		
		table.add(new Label("Paused", skin.get(LabelStyle.class)));
		
		table.row().expand().center();
		
		TextButton resumeButton = new TextButton("Resume", skin.get(TextButtonStyle.class));
		resumeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				unpauseGame();
			}
		});
		table.add(resumeButton);

		table.row().expand().center();
		
		TextButton menuButton = new TextButton("Main Menu", skin.get(TextButtonStyle.class));
		menuButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				PauseScreen.this.game.setScreen(new MainMenuScreen(PauseScreen.this.game));
				PauseScreen.this.gameScreen.dispose();
				dispose();
			}
		});
		table.add(menuButton);
		
		ui.addActor(table);
		
	}

	/**
	 * Unpauses the game.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The game is unpaused.
	 */
	private void unpauseGame() {
		game.setScreen(gameScreen);
		gameScreen = null;
		dispose();
	}

	/**
	 * Renders the pause screen.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The screen is rendered.
	 */
	@Override
	public void render(float delta) {
		gameScreen.render(delta);
		ui.act();
		ui.draw();
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
		ui.dispose();
	}

}
