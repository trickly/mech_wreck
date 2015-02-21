package com.mechwreck.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GLTexture;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Disposable;
import com.mechwreck.Damagable;
import com.mechwreck.GameWorld;
import com.mechwreck.Renderable;
import com.mechwreck.Sendable;
import com.mechwreck.Updatable;

/**
 * The players mechs.
 */
public class Mech implements Updatable, Renderable, Disposable, ContactListener, Damagable, Sendable {
	
	public static final float MAX_HEALTH = 300;

	private Texture mechTexture;
	private GameWorld gameWorld;
	private Body body;
	private Fixture sensor;
	private float health;
	private int contacts;
	private Vector2 acceleration;
	private SpriteBatch tank;
	private TextureRegion mechRegion;
	private ShapeRenderer shapeRenderer;
	private boolean hit;

	/*
	 * Creates a new mech.
	 */
	public Mech(GameWorld gameWorld, Vector2 position, int player) {
		this.gameWorld = gameWorld;

		tank = new SpriteBatch();
		mechTexture = chooseMechType(player);

		GLTexture.setEnforcePotImages(false);
		mechRegion = new TextureRegion(mechTexture);
		
		shapeRenderer = new ShapeRenderer();

		//Create mech physics body.
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(position);
		bodyDef.linearVelocity.set(20, 0);
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.fixedRotation = true;
		body = gameWorld.getWorld().createBody(bodyDef);
		body.setUserData(this);
		CircleShape shape = new CircleShape();
		shape.setRadius(15);
		FixtureDef bodyfDef = new FixtureDef();
		bodyfDef.shape = shape;
		bodyfDef.density = 850;
		body.createFixture(bodyfDef);

		shape.dispose();

		PolygonShape sensorShape = new PolygonShape();
		sensorShape.set(new Vector2[] { new Vector2(-15, -15), new Vector2(15, -15), new Vector2(15, -18), new Vector2(-15, -18) });

		FixtureDef sensorFixtureDef = new FixtureDef(); // Sensor fixture is used to detect when the mech is on the ground
		sensorFixtureDef.shape = sensorShape;
		sensorFixtureDef.isSensor = true;
		sensor = body.createFixture(sensorFixtureDef);
		sensorShape.dispose();

		gameWorld.getUpdatables().add(this);
		gameWorld.getRenderables().add(this);
		gameWorld.getDamagables().add(this);
		gameWorld.getSendables().add(this);
		gameWorld.getDisposables().add(this);

		contacts = 0;
		health = MAX_HEALTH;
		hit = false;
	}

	/*
	 * Selects the color of the mech.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	private Texture chooseMechType(int num) {
		switch (num) {
		case 0:
			return new Texture(Gdx.files.internal("textures/mech.png"));
		case 1:
			return new Texture(Gdx.files.internal("textures/mechred.png"));
		case 2:
			return new Texture(Gdx.files.internal("textures/mechgreen.png"));
		case 3:
			return new Texture(Gdx.files.internal("textures/mechyellow.png"));
		case 4:
			return new Texture(Gdx.files.internal("textures/mechorange.png"));
		case 5:
			return new Texture(Gdx.files.internal("textures/mechpink.png"));
		default:
			return new Texture(Gdx.files.internal("textures/mech.png"));
		}
	}

	/*
	 * Get the mech's physics body.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public Body getBody() {
		return body;
	}
	
	/*
	 * Get the mech's health.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public float getHealth() {
		return health;
	}
	/*
	 * Get the mech's Max health.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public float getMAXHealth() {
		return MAX_HEALTH;
	}
	
	/*
	 * Set the mech's health.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The mech's health is set.
	 */
	public void setHealth(float health) {
		this.health = health;
	}

	/*
	 * Get the number of contacts the mech has.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public int getContacts() {
		return contacts;
	}
	
	/*
	 * Set the number of contacts the mech has.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The mech's contact count is set.
	 */
	public void setContacts(int contacts) {
		this.contacts = contacts;
	}
	
	/*
	 * Gets the mech's sensor fixture.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public Fixture getSensor() {
		return sensor;
	}

	/*
	 * Gets the mech's acceleration.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public Vector2 getAcceleration() {
		return acceleration;
	}
	
	/*
	 * Takes health from the mech if it is within the given radius.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The mech has taken damage if needed.
	 */
	public void takeDamage(Vector2 position, float radius, float amount) {
		if (position.cpy().sub(body.getPosition()).len2() < radius * radius) {
			health -= amount;
			if (health<0)
			{
				health = 0;
			}
			hit = true;
		}
	}
	
	/*
	 * Updates the mech.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The mech is updated.
	 */
	@Override
	public void update() {
		acceleration = gameWorld.calculateNetAcceleration(body.getPosition(), body.getMass());
		body.applyForceToCenter(acceleration.scl(80), false);
		acceleration.nor();
		body.setTransform(body.getPosition(), acceleration.angle() / 180 * (float) Math.PI + (float) Math.PI / 2);
	}
	
	/*
	 * Renders the mech.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The mech is rendered.
	 */
	@Override
	public void render(Camera camera) {
		
		tank.setProjectionMatrix(camera.combined);

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Filled);
		
		shapeRenderer.setColor(Color.DARK_GRAY);
		shapeRenderer.rect(body.getPosition().x - MAX_HEALTH / 18, body.getPosition().y - 23, MAX_HEALTH / 7, 7, MAX_HEALTH / 18, 23, body.getAngle() * 180 / (float) Math.PI);
		shapeRenderer.setColor(Color.GREEN);
		shapeRenderer.rect(body.getPosition().x - MAX_HEALTH / 18, body.getPosition().y - 23, health / 7, 7, MAX_HEALTH / 18, 23, body.getAngle() * 180 / (float) Math.PI);
		

		
	
		shapeRenderer.end();
		tank.disableBlending();

		tank.begin();
		if (hit == true)
		{
			tank.setColor(Color.CLEAR);
			tank.draw(mechRegion, body.getPosition().x - 15, body.getPosition().y - 15, 15, 15, 30, 30, 1, 1, body.getAngle() * 180 / (float) Math.PI);
			hit = false;
			
		}else {
			tank.setColor(Color.WHITE);
			}
		tank.draw(mechRegion, body.getPosition().x - 15, body.getPosition().y - 15, 15, 15, 30, 30, 1, 1, body.getAngle() * 180 / (float) Math.PI);


		tank.end();
	}

	/**
	 * Disposes the mech.
	 * 
	 * pre:
	 * The mech is not disposed.
	 * post:
	 * The mech is disposed.
	 */
	@Override
	public void dispose() {
		gameWorld.getUpdatables().remove(this);
		gameWorld.getRenderables().remove(this);
		gameWorld.getDamagables().remove(this);
		gameWorld.getSendables().remove(this);
		gameWorld.getDisposables().remove(this);
		body.getWorld().destroyBody(body);
		tank.dispose();
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
		if (contact.getFixtureA() == sensor && !(contact.getFixtureB().getBody().getUserData() instanceof Particle)) {
			contacts++;
		}
		if (contact.getFixtureB() == sensor && !(contact.getFixtureA().getBody().getUserData() instanceof Particle)) {
			contacts++;
		}
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
		if (contact.getFixtureA() == sensor) {
			Fixture fixture = contact.getFixtureB();
			if (fixture != null && !(fixture.getBody().getUserData() instanceof Particle)) {
				contacts--;
			}
		}
		if (contact.getFixtureB() == sensor) {
			Fixture fixture = contact.getFixtureA();
			if (fixture != null && !(fixture.getBody().getUserData() instanceof Particle)) {
				contacts--;
			}
		}
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
	 * None.
	 */
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}
	
	/*
	 * Gets the sync data for this mech.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public Object send() {
		return new MechSyncData(this);
	}

	/*
	 * Receives the sync data for the mech.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The mech is synced.
	 */
	@Override
	public void recieve(Object object) {
		MechSyncData mechSyncData = (MechSyncData) object;
		mechSyncData.receive(this);
	}
}
