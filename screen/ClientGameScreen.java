package com.mechwreck.screen;

import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mechwreck.wireless.GameOverMessage;
import com.mechwreck.wireless.InputMessage;
import com.mechwreck.wireless.WifiHelper;

/**
 * Client implementation of the game screen.
 */
public class ClientGameScreen extends GameScreen {
	
	private Game game;
	
	private String name;
	
	private Client client;
	private Listener listener;
	private ArrayList<Object> messageQueue;
	private int player;
	private float time;

	
	/**
	 * Creates a client game screen.
	 */
	public ClientGameScreen(Game game, int player, String name) {
		super(game, player, name);
		this.game = game;
		this.name = name;
		this.player = player;

		
		messageQueue = new ArrayList<Object>();
		
		client = WifiHelper.openClient();
		listener = new Listener() {
			@Override
			public void received(Connection connection, Object object) {
				synchronized (messageQueue) {
					messageQueue.add(object);
				}
			}
		};
		client.addListener(listener);
		
	}
	
	/**
	 * Called when input is received.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Input is sent to host.
	 */
	@Override
	public void onInput(InputMessage message) {
		client.sendUDP(message);
	}
	
	/**
	 * Called when the game is over.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Game over screen is shown.
	 */
	@Override
	public void onGameOver(GameOverMessage message) {

		time += Gdx.graphics.getRawDeltaTime();
		if(time > Math.PI ) {

		game.setScreen(new ClientGameOverScreen(game, name, message.getWinner() == player));
		dispose();}
	}
	
	/*
	 * Renders the client device's game screen
	 * 
	 * pre:
	 * None.
	 * post:
	 * The game screen is rendered.
	 */
	@Override
	public void render(float delta) {
		synchronized (messageQueue) {
			for(Object message : messageQueue) {
				receiveMessage(message);
			}
			messageQueue.clear();
		}
		super.render(delta);
	}
	
	/*
	 * Disposes the client device's game screen
	 * 
	 * pre:
	 * Game screen is not disposed.
	 * post:
	 * Game screen is disposed.
	 */
	@Override
	public void dispose() {
		super.dispose();
		client.removeListener(listener);
	}
	
}
