package com.mechwreck.wireless;

import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import com.mechwreck.Spawner;
import com.mechwreck.gameobjects.CannonSpawner;
import com.mechwreck.gameobjects.MechSyncData;
import com.mechwreck.gameobjects.MissileSpawner;
import com.mechwreck.gameobjects.ParticleEffectSpawner;
import com.mechwreck.gameobjects.PlanetSyncData;
import com.mechwreck.gameobjects.ParticleEffects.ParticleEffectType;
import com.mechwreck.gameobjects.ParticleSyncData;
import com.mechwreck.gameobjects.TripleMissileSpawner;
import com.mechwreck.wireless.InputMessage.InputType;

/**
 * Class that helps with wifi connections.
 */
public class WifiHelper {

	public static final int UDP_PORT = 43291;
	public static final int TCP_PORT = 43292;
	
	private static Server server;
	private static boolean serverOpen = false;
	
	private static Client client;
	private static boolean clientOpen = false;
	
	/**
	 * Creates the server. If it is already created, it returns a copy.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The server is created.
	 */
	public static Server openServer() {
		if(server == null) {
			server = new Server();
			registerClasses(server.getKryo());
		}
		if(!serverOpen) {
			server.start();
			try {
				server.bind(TCP_PORT, UDP_PORT);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		serverOpen = true;
		return server;
	}
	
	/**
	 * Closes the server if it is open.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The server is closed.
	 */
	public static Server closeServer() {
		if(server == null) {
			server = new Server();
		}
		if(serverOpen) {
			server.stop();
		}
		serverOpen = false;
		return server;
	}
	
	/**
	 * Creates the client. If it is already created, it returns a copy.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The client is created.
	 */
	public static Client openClient() {
		if(client == null) {
			client = new Client();
			registerClasses(client.getKryo());
		}
		if(!clientOpen) {
			client.start();
		}
		clientOpen = true;
		return client;
	}
	
	/**
	 * Closes the client if it is open.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The client is closed.
	 */
	public static Client closeClient() {
		if(client == null) {
			client = new Client();
		}
		if(clientOpen) {
			client.stop();
		}
		clientOpen = false;
		return client;
	}
	
	/**
	 * Registers classes that need to be sent across the network.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Classes are registered.
	 */
	public static void registerClasses(Kryo kryo) {
		kryo.register(NameRequest.class);
		kryo.register(NameResponse.class);
		kryo.register(JoinRequest.class);
		kryo.register(StartGameRequest.class);
		
		kryo.register(InitializeMessage.class);
		kryo.register(GameOverMessage.class);
		kryo.register(InputMessage.class);
		kryo.register(InputType.class);
		kryo.register(SyncMessage.class);
		kryo.register(Spawner.class);
		kryo.register(Spawner[].class);

		kryo.register(Object.class);
		kryo.register(Object[].class);
		kryo.register(Vector2.class);
		kryo.register(Vector2[].class);
		kryo.register(ArrayList.class);
		
		kryo.register(MechSyncData.class);
		kryo.register(ParticleSyncData.class);
		kryo.register(MissileSpawner.class);
		kryo.register(CannonSpawner.class);
		kryo.register(TripleMissileSpawner.class);
		kryo.register(ParticleEffectSpawner.class);
		kryo.register(ParticleEffectType.class);
		kryo.register(PlanetSyncData.class);
	}

}
