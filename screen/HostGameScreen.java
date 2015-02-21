package com.mechwreck.screen;

import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mechwreck.gameobjects.Mech;
import com.mechwreck.gameobjects.Planet;
import com.mechwreck.wireless.GameOverMessage;
import com.mechwreck.wireless.InitializeMessage;
import com.mechwreck.wireless.InputMessage;
import com.mechwreck.wireless.SyncMessage;
import com.mechwreck.wireless.WifiHelper;

/**
 * The game screen on the host's device.
 */
public class HostGameScreen extends GameScreen {
	
	private static final float SYNC_INTERVAL = 0.05f;
	
	private Game game;
	
	private String name;
	
	private Server server;
	private Listener listener;
	private ArrayList<Connection> connections;
	private float syncTimer;
	private boolean singlePlayer;
	private float time;

	/*
	 * Creates the host's gamescreen.
	 */
	public HostGameScreen(Game game, ArrayList<Connection> connections, String name) {
		super(game, 0, name);
		this.game = game;
		this.server = WifiHelper.openServer();
		this.name = name;
		listener = new Listener() {
			@Override
			public void received(Connection connection, Object object) {
				if(object instanceof InputMessage) {
					sendMessage(object);
				}
			}

			@Override
			public void disconnected(Connection connection) {
				if(HostGameScreen.this.connections.contains(connection)) {
					HostGameScreen.this.connections.remove(connection);
					sendMessage(new GameOverMessage());
				}
			}
		};
		server.addListener(listener);
		this.connections = connections;

		syncTimer = SYNC_INTERVAL;
		
		singlePlayer = connections.isEmpty();
		
		initialize();
	}
	
	/*
	 * Sets up the game. Creates planets and the mechs.
	 *
	 * pre:
	 * None
	 * post:
	 * Planets and mechs are created.
	 */
	private void initialize() {
		float totalRadius = (Planet.INNER_RADIUS + Planet.LAYER_COUNT) * Planet.LAYER_SCALE;
		
		final float[] possibleAngles = new float[] {
				60, -60,
				100, -100,
				80, -80,
				110, -110,
				70, -70,
				90, -90,
				120, -120
		};
		
		Vector2[] planetPositions = new Vector2[MAX_PLANETS];
		int count = 0;
		while(count < MIN_PLANETS) {
			count = 0;
		
			float prevAngle = 0;
			Vector2 position = new Vector2(GAME_WIDTH / 2, GAME_HEIGHT / 2);
			for(int i = 0; i < MAX_PLANETS; i++) {
				int start = (int)(Math.random() * possibleAngles.length);
				for(int j = start; j < possibleAngles.length + start; j++) {
					float angle = possibleAngles[j % possibleAngles.length];
					Vector2 newPosition = position.cpy().add(new Vector2(0, 2 * totalRadius + PLANET_DISTANCE).rotate(prevAngle + angle));
					boolean fits = newPosition.x > totalRadius + 50 && newPosition.y > totalRadius + 50 &&
							       newPosition.x < GAME_WIDTH - totalRadius - 50 && newPosition.y < GAME_HEIGHT - totalRadius - 50;
					if(fits) {
						for(int c = 0; c < count; c++) {
							if(newPosition.cpy().sub(planetPositions[c]).len2() < (2 * totalRadius + PLANET_DISTANCE) * (2 * totalRadius + PLANET_DISTANCE)) {
								fits = false;
								break;
							}
						}
					}

					if(fits) {
						position = newPosition;
						prevAngle += angle;
						planetPositions[count++] = newPosition;
						break;
					}
				}
			}
		}

		Vector2[] usedPlanetPositions = new Vector2[count];
		for(int i = 0; i < count; i++) {
			usedPlanetPositions[i] = planetPositions[i];
		}
		
		ArrayList<Integer> nums =  new ArrayList<Integer>(count);
		for(int i = 0; i < count; i++) {
			nums.add(i);
		}
		
		Vector2[] mechPositions = new Vector2[connections.size() + 1];
		for(int i = 0; i < mechPositions.length; i++) {
			int planet = nums.remove((int)(Math.random() * nums.size()));
			float angle = (float) (Math.random() * 360);
			mechPositions[i] = new Vector2(0, totalRadius + 5).rotate(angle).add(planetPositions[planet]);
		}
		
		InitializeMessage message = new InitializeMessage(usedPlanetPositions, mechPositions);
		sendMessage(message);
	}
	
	/**
	 * Sends a message to the client devices.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Message sent.
	 */
	private void sendMessage(Object object) {
		receiveMessage(object);
		for(int i = 0; i < connections.size(); i++) {
			connections.get(i).sendUDP(object);
		}
	}
	
	/**
	 * Called when any input happens in GameScreen.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Input message sent to clients.
	 */
	@Override
	public void onInput(InputMessage message) {
		sendMessage(message);
	}
	
	/**
	 * Called when the game is over.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Switches to game over screen.
	 */
	@Override
	public void onGameOver(GameOverMessage message) {

		time += Gdx.graphics.getRawDeltaTime();
		if(time > Math.PI ) {

			game.setScreen(new HostGameOverScreen(game, connections, name,message.getWinner() == 0));
			dispose();		}

	}

	/**
	 * Syncs the client screens with this screen.
	 */
	private void sync() {
		SyncMessage message = new SyncMessage(getGameWorld().getSyncData(), getGameWorld().getSpawners());
		sendMessage(message);
	}

	/**
	 * Renders the host game screen.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The game screen is rendered.
	 */
	@Override
	public void render(float delta) {
		syncTimer -= Gdx.graphics.getDeltaTime();
		if(syncTimer <= 0) {
			sync();
			syncTimer = SYNC_INTERVAL;
		}

		int count = singlePlayer ? -1 : 0;
		Mech[] mechs = getMechs();
		for (int i = 0; i < mechs.length; i++){
			if (mechs[i].getHealth() <= 0){
				count++;
			}
		}
		
		if(count >= mechs.length - 1){
			int winner  = 0;
			for(int i = 0; i < mechs.length; i++) {
				if(mechs[i].getHealth() > 0) {
					winner = i;
					break;
				}
			}
			sendMessage(new GameOverMessage(winner));
		}
		
		super.render(delta);
	}

	/*
	 * Disposes of the host gamescreen.
	 * 
	 * pre:
	 * The screen is not diposed.
	 * post:
	 * The screen is disposed.
	 */
	@Override
	public void dispose() {
		server.removeListener(listener);
		super.dispose();
	}
	
}
