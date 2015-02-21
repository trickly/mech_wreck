package com.mechwreck.screen;

import java.io.IOException;
import java.net.InetAddress;

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
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mechwreck.SkinManager;
import com.mechwreck.wireless.JoinRequest;
import com.mechwreck.wireless.NameRequest;
import com.mechwreck.wireless.NameResponse;
import com.mechwreck.wireless.StartGameRequest;
import com.mechwreck.wireless.WifiHelper;

/**
 * The screen where the player waits for the host to start the game.
 */
public class PartyScreen implements Screen {
	
	private Game game;
	private String name;
	
	private Client client;
	private InetAddress address;
	private Listener listener;
	
	private Stage ui;
	private Table playerTable;
	
	/**
	 * Creates a new party screen.
	 */
	public PartyScreen(Game game, InetAddress address, String name) {
		this.game = game;
		this.address = address;
		this.name = name;
		
		client = WifiHelper.openClient();
		listener = new Listener() {
			@Override
			public void received(Connection connection, Object object) {
				if(object instanceof NameRequest) {
					NameRequest request = (NameRequest)object;
					connection.sendTCP(new NameResponse(request.getRequestId(), request.getIndex(), PartyScreen.this.name));
				} else if(object instanceof StartGameRequest) {
					StartGameRequest message = (StartGameRequest)object;
					PartyScreen.this.game.setScreen(new ClientGameScreen(PartyScreen.this.game, message.getPlayer(), PartyScreen.this.name));
					dispose();
				}
			}

			@Override
			public void disconnected(Connection connection) {
				PartyScreen.this.game.setScreen(new JoinScreen(PartyScreen.this.game));
				dispose();
			}
		};
		client.addListener(listener);

		ui = new Stage();
		Skin skin = SkinManager.getSkin();
		
		Table table = new Table();
		table.setFillParent(true);

		table.row().expand().center();
		
		table.add(new Label("Waiting for Host", skin.get(LabelStyle.class))).center().top().colspan(2);
		
		playerTable = new Table();
		table.add(playerTable).colspan(2);
		
		table.row().bottom().center();
		
		TextButton backButton = new TextButton("Back", skin.get(TextButtonStyle.class));
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				client.stop();
			}
		});
		table.add(backButton);

		ui.addActor(table);
	}
	
	/**
	 * Attempts to connect to the server.
	 *
	 * pre;
	 * None.
	 * post:
	 * The client is connected to the server.
	 */
	private void connect() {
		try {
			if(!client.isConnected()) {
				client.connect(1000, address, WifiHelper.TCP_PORT, WifiHelper.UDP_PORT);
			}
			JoinRequest request = new JoinRequest();
			client.sendTCP(request);
		} catch (IOException e) {
			e.printStackTrace();
			game.setScreen(new JoinScreen(game));
			dispose();
		}
	}
	
	/**
	 * Renders the screen.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The screen is rendered.
	 */
	@Override
	public void render(float delta) {
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
		connect();
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

	/**
	 * Called to dispose of the party screen..
	 * 
	 * pre:
	 * The screen is not disposed.
	 * post:
	 * The screen is disposed.
	 */
	@Override
	public void dispose() {
		client.removeListener(listener);
		ui.dispose();
	}

}
