package com.mechwreck.screen;

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
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mechwreck.Background;
import com.mechwreck.SkinManager;
import com.mechwreck.wireless.GameOverMessage;
import com.mechwreck.wireless.StartGameRequest;
import com.mechwreck.wireless.WifiHelper;

/**
 * Game over screen shown to the client.
 */
public class ClientGameOverScreen implements Screen {

	private Game game;
	
	private String name;
	
	private Client client;
	private Listener listener;

	private OrthographicCamera camera;
	private Background background;
	private Stage ui;
	
	/**
	 * Creates a new Client Game Over Screen.
	 */
	public ClientGameOverScreen(Game game, String name,boolean winner) {
		this.game = game;
		this.name = name;
		background = new Background();
		camera = new OrthographicCamera();
		
		ui = new Stage();
		Skin skin = SkinManager.getSkin();
		
		Table table = new Table();
		table.setFillParent(true);
		
		TextButton backButton = new TextButton("BACK", skin.get(TextButtonStyle.class));
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				WifiHelper.closeClient();
				ClientGameOverScreen.this.game.setScreen(new MainMenuScreen(ClientGameOverScreen.this.game));
			}
		});
		table.add(backButton).left().pad(30);

		table.row().expand().center();

		table.add(new Label("Game Over", skin.get(LabelStyle.class)));
		
		table.row().expand().center();

		table.add(new Label(winner ? "You Win!" : "You Lose", skin.get(LabelStyle.class)));

		ui.addActor(table);
		
		client = WifiHelper.openClient();
		listener = new Listener() {
			@Override
			public void received(Connection connection, Object object) {
				if(object instanceof StartGameRequest) {
					StartGameRequest message = (StartGameRequest)object;
					ClientGameOverScreen.this.game.setScreen(new ClientGameScreen(ClientGameOverScreen.this.game, message.getPlayer(), ClientGameOverScreen.this.name));
					dispose();
				} else if(object instanceof GameOverMessage) {
					//ClientGameOverScreen.this.game.setScreen(new MainMenuScreen(ClientGameOverScreen.this.game));
					//dispose();
				}
			}
		};
		client.addListener(listener);
	}

	/**
	 * Renders the GameOverScreen
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
	 * Called when the screen is resized.
	 * 
	 * pre:
	 * None
	 * post:
	 * The screen is correctly sized.
	 */
	@Override
	public void resize(int w, int h) {
		camera.setToOrtho(false, w, h);
		camera.update();
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
	 * Called when the screen is hidden
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
	 * None.
	 */
	@Override
	public void dispose() {
		ui.dispose();
		client.removeListener(listener);
		background.dispose();
	}

}
