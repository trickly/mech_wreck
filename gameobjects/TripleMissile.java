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
 * Weapon that fires three missiles.
 */
public class TripleMissile extends Particle implements ContactListener {

	private GameWorld gameWorld;
	private boolean collided;
	private ShapeRenderer shapeRenderer;

	/**
	 * Creates a new triple missile and two other missiles.
	 */
	public TripleMissile(GameWorld gameWorld, Vector2 position, Vector2 velocity) {
		super(gameWorld, position, velocity);
		this.gameWorld = gameWorld;
		collided = false;
		new Missile (gameWorld, position.cpy().add(new Vector2(-20,5).rotate(velocity.angle()-90)), velocity.cpy().rotate(50).rotate(0));
		new Missile (gameWorld, position.cpy().add(new Vector2 (20, 5).rotate (velocity.angle()-90)), velocity.cpy().rotate(-50).rotate(0));

		CircleShape shape = new CircleShape();
		shape.setRadius(2);
		getBody().createFixture(shape, 0.000000000000000001f);
		shape.dispose();
		shapeRenderer = new ShapeRenderer();

	}
	
	/**
	 * Renders the missile.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The missile is rendered.
	 */
	@Override
	public void render(Camera camera){

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.circle(getBody().getPosition().x, getBody().getPosition().y, 2);
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
	 * The missile is destroyed and particle effects are created.
	 */
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		if(!collided) {
			collided = true;
			gameWorld.runAfter(new Runnable() {
				@Override
				public void run() {
					gameWorld.takeDamage(getBody().getPosition().cpy(), 30, 10);
					gameWorld.spawn(new ParticleEffectSpawner(getBody().getPosition().cpy(), ParticleEffectType.CYAN));
					kill();
				}
			});
		}
	}
	
}