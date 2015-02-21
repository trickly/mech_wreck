package com.mechwreck.gameobjects;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Disposable;
import com.mechwreck.GameWorld;
import com.mechwreck.Renderable;
import com.mechwreck.Sendable;
import com.mechwreck.Updatable;

/**
 * General particle class used for weapons.
 */
public class Particle implements Updatable, Renderable, Disposable, Sendable {

	private GameWorld gameWorld;
	private Body body;
	private ShapeRenderer shapeRenderer;

	private boolean dead;

	/**
	 * Creates a new particle.
	 */
	public Particle(GameWorld gameWorld, Vector2 position, Vector2 velocity) {
		this.gameWorld = gameWorld;
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(position);
		bodyDef.linearVelocity.set(velocity);
		bodyDef.type = BodyType.DynamicBody;
		body = gameWorld.getWorld().createBody(bodyDef);
		body.setUserData(this);
		shapeRenderer = new ShapeRenderer();
		
		dead = false;

		gameWorld.getUpdatables().add(this);
		gameWorld.getRenderables().add(this);
		gameWorld.getSendables().add(this);
		gameWorld.getDisposables().add(this);
	}

	/**
	 * Gets the particle's body.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public Body getBody() {
		return body;
	}
	
	/**
	 * Kills the particle:
	 * None.
	 * post:
	 * Particle is killed.
	 */
	public void kill() {
		dead = true;
	}
	
	/**
	 * Returns true if the particle is dead.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public boolean isDead() {
		return dead;
	}

	/**
	 * Updates the particle.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The particle is updated.
	 */
	@Override
	public void update() {
		Vector2 acceleration = gameWorld.calculateNetAcceleration(body.getPosition(), body.getMass());
		body.applyLinearImpulse(acceleration, body.getPosition(), false);
	}

	/**
	 * Renders the particle. Empty.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None
	 */
	@Override
	public void render(Camera camera) {
	}

	/**
	 * Disposes of the particle.
	 * 
	 * pre:
	 * The particle is not disposed.
	 * post:
	 * The particle is disposed.
	 */
	@Override
	public void dispose() {
		gameWorld.getUpdatables().remove(this);
		gameWorld.getRenderables().remove(this);
		gameWorld.getSendables().remove(this);
		gameWorld.getDisposables().remove(this);

		if (body.isActive()) {
			body.setActive(false);
			body.getWorld().destroyBody(body);
			shapeRenderer.dispose();
		}
	}

	/**
	 * Called to get sync data for the particle.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public Object send() {
		return new ParticleSyncData(this);
	}

	/**
	 * Receives sync data for the particle
	 * 
	 * pre:
	 * None.
	 * post:
	 * The particle is synced.
	 */
	@Override
	public void recieve(Object object) {
		ParticleSyncData data = (ParticleSyncData)object;
		data.receive(this);
	}

}