package com.mechwreck.screen;

import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.kryonet.Connection;
import com.mechwreck.Background;
import com.mechwreck.SkinManager;
import com.mechwreck.wireless.GameOverMessage;
import com.mechwreck.wireless.StartGameRequest;
import com.mechwreck.wireless.WifiHelper;

/**
 * Screen shown when the game is over on the hosts device.
 */
public class HostGameOverScreen implements Screen {

	private Game game;
	
	private String name;

	private OrthographicCamera camera;
	private Background background;
	private Stage ui;
	
	private ArrayList<Connection> connections;
	
	/**
	 * Creates a new Game Over Screen.
	 */
	public HostGameOverScreen(Game game, ArrayList<Connection> connections, String name, boolean winner) {
		this.game = game;
		this.name = name;
		background = new Background();
		camera = new OrthographicCamera();
		this.connections = connections;
		
		ui = new Stage();
		Skin skin = SkinManager.getSkin();
		
		Table table = new Table();
		table.setFillParent(true);
		
		//TextButton backButton = new TextButton("BACK", skin.get(TextButtonStyle.class));
		//backButton.addListener(new ClickListener() {
		//	@Override
		//	public void clicked(InputEvent event, float x, float y) {
		//		HostGameOverScreen.this.game.setScreen(new MainMenuScreen(HostGameOverScreen.this.game));
		//	}
		//});
	//	table.add(backButton);

		table.row().center().colspan(2);

		table.add(new Label("Game Over", skin.get(LabelStyle.class)));
		
		table.row().expand().center().colspan(2);
		
		table.add(new Label(winner ? "You Win!" : "You Lose", skin.get(LabelStyle.class)));
		
		table.row().expand().center().colspan(2);

		table.add(new Label("Play Again?", skin.get(LabelStyle.class)));		

		table.row().expand().center().pad(30);

		TextButton noButton = new TextButton("No", skin.get(TextButtonStyle.class));
		noButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				for(int i = 0; i < HostGameOverScreen.this.connections.size(); i++) {
					HostGameOverScreen.this.connections.get(i).sendTCP(new GameOverMessage());
				}
				HostGameOverScreen.this.game.setScreen(new MainMenuScreen(HostGameOverScreen.this.game));
				WifiHelper.closeServer();
				dispose();
			}
		});

		TextButton yesButton = new TextButton("Yes", skin.get(TextButtonStyle.class));
		yesButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				for(int i = 0; i < HostGameOverScreen.this.connections.size(); i++) {
					HostGameOverScreen.this.connections.get(i).sendTCP(new StartGameRequest(i + 1));
				}
				HostGameOverScreen.this.game.setScreen(new HostGameScreen(HostGameOverScreen.this.game, HostGameOverScreen.this.connections, HostGameOverScreen.this.name));
				dispose();
			}
		});
		table.add(yesButton);

		table.add(noButton);

		
		ui.addActor(table);
	}

	/**
	 * Renders the GameOverScreen
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
	 * The screen is resized.
	 */
	@Override
	public void resize(int width, int height) {
		camera.setToOrtho(false, width, height);
		camera.update();
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
		background.dispose();
	}
	
}
