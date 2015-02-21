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
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mechwreck.Background;
import com.mechwreck.SkinManager;
import com.mechwreck.wireless.JoinRequest;
import com.mechwreck.wireless.NameRequest;
import com.mechwreck.wireless.NameResponse;
import com.mechwreck.wireless.StartGameRequest;
import com.mechwreck.wireless.WifiHelper;

/**
 * Screen where the game's host waits for other players to join.
 */
public class HostScreen implements Screen {
	
	private Game game;
	private Server server;
	private String serverName;
	private ArrayList<String> playerNames;
	private ArrayList<Connection> playerConnections;
	private int currentRequestId;
	
	private Stage ui;
	private Skin skin;
	private TextField nameField;
	private Table playerTable;
	
	private OrthographicCamera camera;
	private Background background;

	/**
	 * Generates the host screen.
	 */
	public HostScreen(Game game) {
		this.game = game;
		
		serverName = "Unknown";
		playerNames = new ArrayList<String>();
		playerConnections = new ArrayList<Connection>();
		
		camera = new OrthographicCamera();
		background = new Background();
		
		ui = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		skin = SkinManager.getSkin();
		
		Table table = new Table();
	    table.setFillParent(true);
		table.add(new Label("Host", skin.get(LabelStyle.class))).colspan(2);

		table.row().expand(true, false).center();

		table.add(new Label("Name:", skin.get(LabelStyle.class)));

		nameField = new TextField("", skin.get(TextFieldStyle.class));
		nameField.setMessageText("Name");
		nameField.setTextFieldListener(new TextFieldListener() {
			@Override
			public void keyTyped(TextField textField, char key) {
				serverName = nameField.getText();
			}
		});
		table.add(nameField).fill();
		
		table.row().center();
		table.add(new Label("Players", skin.get(LabelStyle.class)));
		
		table.row().expand().center();
		playerTable = new Table();
		table.add(playerTable);

		table.row().bottom().center();

		TextButton backButton = new TextButton("Back", skin.get(TextButtonStyle.class));
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				HostScreen.this.game.setScreen(new MainMenuScreen(HostScreen.this.game));
				WifiHelper.closeServer();
				dispose();
			}
		});
		table.add(backButton);

		TextButton confirmButton = new TextButton("Start", skin.get(TextButtonStyle.class));
		confirmButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				for(int i = 0; i < playerConnections.size(); i++) {
					playerConnections.get(i).sendTCP(new StartGameRequest(i + 1));
				}
				HostScreen.this.game.setScreen(new HostGameScreen(HostScreen.this.game, playerConnections, serverName));
				dispose();
			}
		});
		table.add(confirmButton);

		ui.addActor(table);
		
		currentRequestId = 0;

		server = WifiHelper.openServer();
		server.addListener(new Listener() {
			@Override
			public void received(Connection connection, Object object) {
				if(object instanceof NameRequest) {
					if(!serverName.equals("")) {
						NameRequest request = (NameRequest)object;
						connection.sendTCP(new NameResponse(request.getRequestId(), request.getIndex(), serverName));
					}
				} else if(object instanceof JoinRequest) {
					playerConnections.add(connection);
					refreshNameList();
				} else if(object instanceof NameResponse) {
					NameResponse response = (NameResponse)object;
					if(response.getRequestId() == currentRequestId) {
						playerNames.add(Math.min(response.getIndex(), playerNames.size()), response.getName());
						playerTable.clear();
						for(int i = 0; i < playerNames.size(); i++) {
							playerTable.add(new Label(playerNames.get(i), skin.get(LabelStyle.class)));
							playerTable.row();
						}//response.getName()
						//playerNames.get(i)
					}
					synchronized (connection) {
						connection.notifyAll();
					}
				}
			}

			@Override
			public void disconnected(Connection connection) {
				if(playerConnections.remove(connection)) {
					refreshNameList();
				}
			}
		});
	}
	
	/**
	 * Refreshes the list of players who have joined.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The list of player names is refreshed.
	 */
	private void refreshNameList() {
		currentRequestId++;
		playerNames.clear();
		playerTable.clear();
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i = 0; i < playerConnections.size(); i++) {
					synchronized(playerConnections.get(i)) {
						playerConnections.get(i).sendTCP(new NameRequest(currentRequestId, i));
						try {
							playerConnections.get(i).wait(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}

	/**
	 * Renders the host screen
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
		((InputMultiplexer)Gdx.input.getInputProcessor()).addProcessor(ui);
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
		((InputMultiplexer)Gdx.input.getInputProcessor()).removeProcessor(ui);
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

	/*
	 * Disposes of the host screen.
	 * 
	 * pre:
	 * The screen is not disposed.
	 * post:
	 * The screen is disposed.
	 */
	@Override
	public void dispose() {
		background.dispose();
		ui.dispose();
	}

}
