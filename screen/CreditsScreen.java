package com.mechwreck.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mechwreck.Background;
import com.mechwreck.SkinManager;

/**
 * Screen dedicated to displaying the credits for the game. The credits should
 * include things like attribution for creative commons material used.
 */
public class CreditsScreen implements Screen {

	private Game game;

	private OrthographicCamera camera;
	private Background background;

	private Stage ui;

	/**
	 * Creates a new Credits Screen.
	 */
	public CreditsScreen(Game game) {
		this.game = game;
		
		
		ui = new Stage();
		Skin skin = SkinManager.getSkin();
		
		Table table = new Table();
		table.setFillParent(true);
		
		table.row().top().left().pad(30);

		TextButton backButton = new TextButton("BACK", skin.get(TextButtonStyle.class));
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				CreditsScreen.this.game.setScreen(new MainMenuScreen(CreditsScreen.this.game));
			}
		});
		table.add(backButton);

		table.row().expand().center();
		
		table.add(new Label("Credits", skin.get(LabelStyle.class)));
		
		table.row().expand().left().pad(30);

		table.add(new Label("Anthony Sandrin", skin.get(LabelStyle.class)));

		table.row().expand().left().pad(30);

		table.add(new Label("Brandon Assing", skin.get(LabelStyle.class)));

		table.row().expand().left().pad(30);

		table.add(new Label("Mario Ponce Tovar", skin.get(LabelStyle.class)));

		table.row().expand().left().pad(30);

		table.add(new Label("Patrick Lee", skin.get(LabelStyle.class)));

		table.row().expand().left().pad(30);
		
		table.add(new Label("Mentor: Mr. Ganuelas", skin.get(LabelStyle.class)));

		table.row().expand().left().pad(30);

		table.add(new Label("Date: 22, Jan. 2014", skin.get(LabelStyle.class)));

		table.row().expand().left().pad(30);

		table.add(new Label("Course: ICS 4U1", skin.get(LabelStyle.class)));
	
		ui.addActor(table);

		camera = new OrthographicCamera();
		background = new Background();
	}

	/**
	 * Renders the credits screen.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The screen is rendered.
	 */
	@Override
	public void render(float delta) {
		background.render(camera);
		ui.act();
		ui.draw();
	}

	/**
	 * Called when the screen is resized.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The screen is rendered.
	 */
	@Override
	public void resize(int width, int height) {
		ui.setViewport(width, height);
		camera.setToOrtho(false);
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
		background.dispose();
	}

}
