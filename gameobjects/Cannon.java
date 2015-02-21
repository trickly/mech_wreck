package com.mechwreck.gameobjects;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mechwreck.GameWorld;
import com.mechwreck.gameobjects.ParticleEffects.ParticleEffectType;

/**
 * Cannon weapon type.
 */
public class Cannon extends Particle implements ContactListener {

	private GameWorld gameWorld;
	private boolean collided;
	private ShapeRenderer shapeRenderer;

	/**
	 * Creates a new cannon projectile.
	 */
	public Cannon(GameWorld gameWorld, Vector2 position, Vector2 velocity) {
		super(gameWorld, position, velocity);
		this.gameWorld = gameWorld;
		collided = false;
		CircleShape shape = new CircleShape();
		shape.setRadius(10f);
		getBody().createFixture(shape, 0.0000000000001f);
		shape.dispose();
		shapeRenderer = new ShapeRenderer();
	}

	/**
	 * Renders the cannon projectile.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The cannon is renedered.
	 */
	@Override
	public void render(Camera camera) {

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.circle(getBody().getPosition().x, getBody().getPosition().y, 10);
		shapeRenderer.end();
	}

	/**
	 * Called when a contact begins.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public void beginContact(Contact contact) {
	}

	/**
	 * Called when a contact ends.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public void endContact(Contact contact) {
	}

	/**
	 * Called when a contact solve begins.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	/**
	 * Called when a contact solve ends.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Missile is destroyed and particles are spawned.
	 */
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		if (!collided) {
			collided = true;
			gameWorld.runAfter(new Runnable() {
				@Override
				public void run() {
					gameWorld.takeDamage(getBody().getPosition().cpy(), 80, 25);
					gameWorld.spawn(new ParticleEffectSpawner(getBody().getPosition().cpy(), ParticleEffectType.GREEN));
					kill();
				}
			});
		}
	}

}