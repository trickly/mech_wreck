package com.mechwreck.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
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
 * The main menu of the game.
 */
public class MainMenuScreen implements Screen {

	private Game game;

	private OrthographicCamera camera;

	private ParticleEffect white;
	
	private Background background;
	
	private Stage ui;
	
	/**
	 * Creates a new main menu screen.
	 */
	public MainMenuScreen(Game game) {
		this.game = game;

		white = new ParticleEffect();
		white.load(Gdx.files.internal("effects/whiteExplosion.p"), Gdx.files.internal("effects"));
		white.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		background = new Background();
		
		ui = new Stage();
		Skin skin = SkinManager.getSkin();
		
		Table table = new Table();
		table.setFillParent(true);
		
		table.row().top().left().pad(30);

		TextButton quitButton = new TextButton("QUIT", skin.get(TextButtonStyle.class));
		quitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		table.add(quitButton);
		
		table.row().expand().top().center();

		table.add(new Label("MECH WRECK", skin.get(LabelStyle.class)));
		
		table.row().bottom().left().padLeft(30);
		
		TextButton hostButton = new TextButton("HOST", skin.get(TextButtonStyle.class));
		hostButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MainMenuScreen.this.game.setScreen(new HostScreen(MainMenuScreen.this.game));
				dispose();
			}
		});
		table.add(hostButton);
		
		table.row().bottom().left().padLeft(30);

		TextButton joinButton = new TextButton("JOIN", skin.get(TextButtonStyle.class));
		joinButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MainMenuScreen.this.game.setScreen(new JoinScreen(MainMenuScreen.this.game));
			}
		});
		table.add(joinButton);

		table.row().bottom().left().padLeft(30);

		TextButton helpButton = new TextButton("HELP", skin.get(TextButtonStyle.class));
		helpButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MainMenuScreen.this.game.setScreen(new HelpScreen(MainMenuScreen.this.game));
			}
		});
		table.add(helpButton);

		table.row().bottom().left().padLeft(30).padBottom(30);

		TextButton creditsButton = new TextButton("CREDITS", skin.get(TextButtonStyle.class));
		creditsButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MainMenuScreen.this.game.setScreen(new CreditsScreen(MainMenuScreen.this.game));
			}
		});
		table.add(creditsButton);
		
		ui.addActor(table);
		
		camera = new OrthographicCamera();
	}

	/**
	 * Renders the main menu.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public void render(float delta) {
		background.render(camera);
		
		ui.act();
		ui.draw();
	}
	
	/**
	 * Called when the menu is resized.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The screen is resized.
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
	 * Called when the game is paused.
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
	 * Called when the game is resumed.
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
	 * The main menu is not disposed.
	 * post:
	 * The main menu is disposed.
	 */
	@Override
	public void dispose() {
		background.dispose();
		ui.dispose();
	}
	
}
