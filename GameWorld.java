package com.mechwreck;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

/**
 * Collection of objects that make up the game world.
 */
public class GameWorld implements Disposable, ContactListener {

	private ArrayList<Updatable> updatables;
	private ArrayList<Renderable> renderables;
	private ArrayList<GravityObject> gravityObjects;
	private ArrayList<Damagable> damagables;
	private ArrayList<Sendable> sendables;
	private ArrayList<Spawner> spawners;
	private ArrayList<Disposable> disposables;
	private ArrayList<Updatable> updatablesCopy;
	private ArrayList<Renderable> renderablesCopy;
	private ArrayList<Damagable> damagablesCopy;
	private ArrayList<Sendable> sendablesCopy;
	private ArrayList<Disposable> disposablesCopy;
	private ArrayList<Runnable> toRun;
	private World world;

	/**
	 * Creates a new Game world.
	 */
	public GameWorld() {
		updatables = new ArrayList<Updatable>();
		renderables = new ArrayList<Renderable>();
		gravityObjects = new ArrayList<GravityObject>();
		damagables = new ArrayList<Damagable>();
		sendables = new ArrayList<Sendable>();
		spawners = new ArrayList<Spawner>();
		disposables = new ArrayList<Disposable>();
		updatablesCopy = new ArrayList<Updatable>();
		renderablesCopy = new ArrayList<Renderable>();
		sendablesCopy = new ArrayList<Sendable>();
		damagablesCopy = new ArrayList<Damagable>();
		disposablesCopy = new ArrayList<Disposable>();
		toRun = new ArrayList<Runnable>();
		world = new World(new Vector2(), true);
		world.setContactListener(this);
	}

	/**
	 * Update the world.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Game world updated.
	 */
	public void update() {
		updatablesCopy.clear();
		updatablesCopy.addAll(updatables);
		for (Updatable updatable : updatablesCopy) {
			updatable.update();
		}
		world.step(Gdx.graphics.getDeltaTime(), 1, 1);
		while (!toRun.isEmpty()) {
			toRun.remove(0).run();
		}
	}

	/**
	 * Render the world.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Game world rendered.
	 */
	public void render(Camera camera) {
		renderablesCopy.clear();
		renderablesCopy.addAll(renderables);
		for (Renderable renderable : renderablesCopy) {
			renderable.render(camera);
		}
	}

	/**
	 * Get a list of the worlds updatable objects.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public ArrayList<Updatable> getUpdatables() {
		return updatables;
	}

	/**
	 * Get a list of the worlds renderable objects.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public ArrayList<Renderable> getRenderables() {
		return renderables;
	}

	/**
	 * Get a list of the worlds objects with gravity.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public ArrayList<GravityObject> getGravityObjects() {
		return gravityObjects;
	}
	
	/**
	 * Get a list of the worlds damagable objects.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public ArrayList<Damagable> getDamagables() {
		return damagables;
	}
	
	/**
	 * Get a list of the worlds sendable objects.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public ArrayList<Sendable> getSendables() {
		return sendables;
	}

	/**
	 * Get a list of the worlds objects that can be disposed.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public ArrayList<Disposable> getDisposables() {
		return disposables;
	}

	/**
	 * Runs a runnable after the current update loop finishes.
	 * 
	 * pre:
	 * None.
	 * post:
	 * runnable will be run.
	 */
	public void runAfter(Runnable runnable) {
		toRun.add(runnable);
	}

	/**
	 * Get the Box2D world.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public World getWorld() {
		return world;
	}
	
	/**
	 * Get data that needs to be synced.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public Object[] getSyncData() {
		Object[] data = new Object[sendables.size()];
		sendablesCopy.clear();
		sendablesCopy.addAll(sendables);
		for(int i = 0; i < sendablesCopy.size(); i++) {
			data[i] = sendablesCopy.get(i).send();
		}
		return data;
	}
	
	/**
	 * Sync world using supplied data.
	 * 
	 * pre:
	 * None.
	 * post:
	 * GameWorld is synced.
	 */
	public void sync(Object[] syncData) {
		sendablesCopy.clear();
		sendablesCopy.addAll(sendables);
		for(int i = 0; i < sendablesCopy.size(); i++) {
			sendablesCopy.get(i).recieve(syncData[i]);
		}
	}

	/**
	 * Gets a list of all the spawners to be spawned.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public Spawner[] getSpawners() {
		Spawner[] spawnersArray = new Spawner[spawners.size()];
		spawners.toArray(spawnersArray);
		spawners.clear();
		return spawnersArray;
	}
	
	/**
	 * Spawn an object using the given spawner.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Spawner will be spawned.
	 */
	public void spawn(Spawner spawner) {
		spawners.add(spawner);
	}
	
	/**
	 * Calculates the net acceleration on an object due to gravity.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public Vector2 calculateNetAcceleration(Vector2 position, float mass) {
		Vector2 acceleration = new Vector2();
		for (GravityObject gravityObject : gravityObjects) {
			acceleration.add(gravityObject.calculateAcceleration(position, mass));
		}
		return acceleration;
	}
	
	/**
	 * Deal damage to all damagables.
	 * 
	 * pre:
	 * None.
	 * post:
	 * damagables have taken damage.
	 */
	public void takeDamage(Vector2 position, float radius, float amount) {
		damagablesCopy.clear();
		damagablesCopy.addAll(damagables);
		for(Damagable damagable : damagablesCopy) {
			damagable.takeDamage(position, radius, amount);
		}
	}

	/**
	 * Disposes the game world.
	 * 
	 * pre:
	 * The game world is not disposed.
	 * post:
	 * The game world is disposed.
	 */
	@Override
	public void dispose() {
		disposablesCopy.clear();
		disposablesCopy.addAll(disposables);
		for (Disposable disposable : disposablesCopy) {
			disposable.dispose();
		}
		world.dispose();
	}

	/**
	 * Called at the start of a contact. Distributes the message to 
	 * objects that can recieve.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		if (fixtureA != null) {
			Body bodyA = fixtureA.getBody();
			Object userDataA = bodyA.getUserData();
			if (userDataA != null && userDataA instanceof ContactListener) {
				ContactListener contactListenerA = (ContactListener) userDataA;
				contactListenerA.beginContact(contact);
			}
		}
		Fixture fixtureB = contact.getFixtureB();
		if (fixtureB != null) {
			Body bodyB = fixtureB.getBody();
			Object userDataB = bodyB.getUserData();
			if (userDataB != null && userDataB instanceof ContactListener) {
				ContactListener contactListenerB = (ContactListener) userDataB;
				contactListenerB.beginContact(contact);
			}
		}
	}

	/**
	 * Called at the end of a contact. Distributes the message to 
	 * objects that can recieve.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public void endContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		if (fixtureA != null) {
			Body bodyA = fixtureA.getBody();
			Object userDataA = bodyA.getUserData();
			if (userDataA != null && userDataA instanceof ContactListener) {
				ContactListener contactListenerA = (ContactListener) userDataA;
				contactListenerA.endContact(contact);
			}
		}
		Fixture fixtureB = contact.getFixtureB();
		if (fixtureB != null) {
			Body bodyB = fixtureB.getBody();
			Object userDataB = bodyB.getUserData();
			if (userDataB != null && userDataB instanceof ContactListener) {
				ContactListener contactListenerB = (ContactListener) userDataB;
				contactListenerB.endContact(contact);
			}
		}
	}

	/**
	 * Called at the start of collision solving Distributes the message to 
	 * objects that can recieve.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		Fixture fixtureA = contact.getFixtureA();
		if (fixtureA != null) {
			Body bodyA = fixtureA.getBody();
			Object userDataA = bodyA.getUserData();
			if (userDataA != null && userDataA instanceof ContactListener) {
				ContactListener contactListenerA = (ContactListener) userDataA;
				contactListenerA.preSolve(contact, oldManifold);
			}
		}
		Fixture fixtureB = contact.getFixtureB();
		if (fixtureB != null) {
			Body bodyB = fixtureB.getBody();
			Object userDataB = bodyB.getUserData();
			if (userDataB != null && userDataB instanceof ContactListener) {
				ContactListener contactListenerB = (ContactListener) userDataB;
				contactListenerB.preSolve(contact, oldManifold);
			}
		}
	}

	/**
	 * Called after collisions are solved. Distributes the message to 
	 * objects that can recieve.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		Fixture fixtureA = contact.getFixtureA();
		if (fixtureA != null) {
			Body bodyA = fixtureA.getBody();
			Object userDataA = bodyA.getUserData();
			if (userDataA != null && userDataA instanceof ContactListener) {
				ContactListener contactListenerA = (ContactListener) userDataA;
				contactListenerA.postSolve(contact, impulse);
			}
		}
		Fixture fixtureB = contact.getFixtureB();
		if (fixtureB != null) {
			Body bodyB = fixtureB.getBody();
			Object userDataB = bodyB.getUserData();
			if (userDataB != null && userDataB instanceof ContactListener) {
				ContactListener contactListenerB = (ContactListener) userDataB;
				contactListenerB.postSolve(contact, impulse);
			}
		}
	}

}
