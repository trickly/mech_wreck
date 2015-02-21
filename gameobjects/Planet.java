package com.mechwreck.gameobjects;

import java.util.ArrayList;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Disposable;
import com.mechwreck.Damagable;
import com.mechwreck.GameWorld;
import com.mechwreck.GravityObject;
import com.mechwreck.Renderable;
import com.mechwreck.Sendable;
import com.mechwreck.gameobjects.ParticleEffects.ParticleEffectType;

/**
 * The game's planets.
 */
public class Planet implements GravityObject, Renderable, Disposable, Damagable, Sendable {

	public static final int LAYER_COUNT = 20;
	public static final int SLICE_COUNT = 60;
	public static final int INNER_RADIUS = 12;
	public static final float LAYER_SCALE = 4f;
	public static final float MAX_HEALTH = 100f;

	private GameWorld game;
	private Body body;
	private Vector2 position;
	private float mass;
	private PlanetBit[][] planetBits;

	private byte[][] visited;
	private ArrayList<ArrayList<Vector2>> borders;
	private ArrayList<ArrayList<Vector2>> outlines;
	private boolean rebuild;

	private ShapeRenderer shapeRenderer;
	
	private float health;
	private Color color;

	/**
	 * Creates a new planet.
	 */
	public Planet(GameWorld game, Vector2 position, float mass) {
		this.game = game;
		this.position = position.cpy();
		this.mass = mass;
		planetBits = new PlanetBit[LAYER_COUNT][SLICE_COUNT];

		for (int layer = 2; layer < LAYER_COUNT; layer++) {
			for (int slice = 0; slice < SLICE_COUNT; slice++) {
				planetBits[layer][slice] = new PlanetBit();
			}
		}

		for (int layer = 0; layer < 2; layer++) {
			for (int slice = 0; slice < SLICE_COUNT; slice++) {
				planetBits[layer][slice] = new InnerPlanetBit();
			}
		}

		createBody();

		visited = new byte[LAYER_COUNT][SLICE_COUNT];
		borders = new ArrayList<ArrayList<Vector2>>();
		outlines = new ArrayList<ArrayList<Vector2>>();

		rebuild = true;
		shapeRenderer = new ShapeRenderer();

		game.getGravityObjects().add(this);
		game.getRenderables().add(this);
		game.getSendables().add(this);
		game.getDamagables().add(this);
		game.getDisposables().add(this);
		
		health = MAX_HEALTH;
		color = Color.GREEN.cpy();
	}

	/**
	 * Gets the planets position.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public Vector2 getPosition() {
		return position;
	}

	/**
	 * Get's an array of the planets bits.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public PlanetBit[][] getPlanetBits() {
		return planetBits;
	}

	/**
	 * Gets the planets mass.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public float getMass() {
		return mass;
	}

	/**
	 * Sets the planets mass.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The planets mass is set.
	 */
	public void setMass(float mass) {
		this.mass = mass;
	}

	/**
	 * Deals damage to the planet if it is in range.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The planet has taken damage.
	 */
	public void takeDamage(Vector2 point, float radius, float amount) {
		radius /= 3f;
		float sliceRadians = (float) (Math.PI * 2 / SLICE_COUNT);

		if(point.cpy().sub(position).len2() < (INNER_RADIUS * LAYER_SCALE + 20) * (INNER_RADIUS * LAYER_SCALE + 20))  {
			health -= amount;
			color.lerp(Color.RED, amount / (MAX_HEALTH - 50));
			color.r *= 1 / Math.max(color.r, color.g);
			color.g *= 1 / Math.max(color.r, color.g);
		}
		for (int layer = 0; layer < LAYER_COUNT; layer++) {
			for (int slice = 0; slice < SLICE_COUNT; slice++) {
				Vector2 position = new Vector2((float) (Math.sin(slice * sliceRadians) * (INNER_RADIUS + layer) * LAYER_SCALE), (float) (Math.cos(slice * sliceRadians) * (INNER_RADIUS + layer) * LAYER_SCALE)).add(this.position);
				if (point.cpy().sub(position).len2() < radius * radius) {
					planetBits[layer][slice].setFilled(false);
					rebuild = true;
				}
			}
		}
	}

	/**
	 * Calculates acceleration due to gravity from this planet.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public Vector2 calculateAcceleration(Vector2 position, float mass) {
		Vector2 acceleration = position.cpy().sub(this.position.cpy());
		float r2 = acceleration.len2() - (INNER_RADIUS) * (INNER_RADIUS);
		return acceleration.scl(-mass * this.mass * GRAVITY_CONSTANT / (float) Math.pow(r2, 2));
	}

	/**
	 * Creates a list of the planets bordering bits.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Border list is created.
	 */
	private void createBorders() {
		for (int layer = 0; layer < LAYER_COUNT; layer++) {
			for (int slice = 0; slice < SLICE_COUNT; slice++) {
				visited[layer][slice] = 0;
			}
		}
		borders.clear();
		for (int layer = 0; layer < LAYER_COUNT; layer++) { // Loop through each bit
			for (int slice = 0; slice < SLICE_COUNT; slice++) {
				if (planetBits[layer][slice].isFilled()) { // If the bit is filled
					for (int direction = 0; direction < 8; direction += 2) { // Check each adjacent direction
						if ((visited[layer][slice] & (1 << direction)) == 0) { // If the direction is not visited
							Vector2 pointer = new Vector2(layer, slice);
							addDirection(pointer, direction); // Determine
																// position from
																// direction
							if (pointer.y == SLICE_COUNT) { // Wrap around the edges (It's a circle)
								pointer.y = 0;
							} else if (pointer.y == -1) {
								pointer.y = SLICE_COUNT - 1;
							}
							if (pointer.x == LAYER_COUNT || // If the tile in the direction is empty or off the sides
							pointer.x == -1 || !planetBits[(int) pointer.x][(int) pointer.y].isFilled()) {
								ArrayList<Vector2> border = new ArrayList<Vector2>();
								// We know a bit with an empty side so we can find a border
								borders.add(border);
								findBorder(layer, slice, direction, border);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Finds a single border. Was recursive but it caused a stack overflow error.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Border has been found.
	 */
	private void findBorder(int layer, int slice, int direction, ArrayList<Vector2> border) {
		boolean done = false;
		Vector2 pointer = new Vector2();
		while (!done) {
			border.add(new Vector2(layer, slice));
			for (int i = 0; i < 8; i++) {
				direction++;
				if (direction == 8) { // Wrap direction back around;
					direction = 0;
				}
				if ((visited[layer][slice] & (1 << direction)) == 0) { // If the
																		// direction
																		// is
																		// not
																		// visited
					visited[layer][slice] |= (1 << direction); // Set the
																// direction as
																// visited;
					pointer.set(layer, slice);
					addDirection(pointer, direction); // Determine position from
														// direction
					if (pointer.y == SLICE_COUNT) { // Wrap around the edges (It's a circle)
						pointer.y = 0;
					} else if (pointer.y == -1) {
						pointer.y = SLICE_COUNT - 1;
					}
					if (pointer.x != LAYER_COUNT && // If the tile in the
													// direction is not empty or
													// off the sides
					pointer.x != -1 && planetBits[(int) pointer.x][(int) pointer.y].isFilled()) {
						direction += 4;
						if (direction >= 8) {
							direction -= 8;
						}
						layer = (int) pointer.x;
						slice = (int) pointer.y;
						break;
					}
				} else {
					done = true;
					break; // If the direction is visited, we're done.
				}
			}
		}
	}

	/**
	 * Creates the planets outlines using the border lists.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Outlines are created.
	 */
	private void createOutlines() {
		outlines.clear();
		for (ArrayList<Vector2> border : borders) {
			if (border.get(0).x != 0) {
				ArrayList<Vector2> outline = new ArrayList<Vector2>();
				outlines.add(outline);
				if (border.size() == 1) { // If there is only one bit, just make
											// a diamond
					outline.add(border.get(0).cpy().add(-0.5f, 0));
					outline.add(border.get(0).cpy().add(0, 0.5f));
					outline.add(border.get(0).cpy().add(0.5f, 0));
					outline.add(border.get(0).cpy().add(0, -0.5f));
				} else {
					for (int i = 0; i < border.size() - 1; i++) {
						Vector2 current = border.get(i).cpy();
						Vector2 next = border.get(i).cpy();
						if (next.y == 0 && current.y > 1) {
							next.y = SLICE_COUNT;
						} else if (next.y == SLICE_COUNT - 1 && current.y == 0) {
							next.y = -1;
						}
						outline.add(current); // Add the current position and
												// the average of the current
												// and next to create an
												// outline.
						outline.add(current.cpy().add(next).scl(0.5f));
					}
				}
				float sliceRadians = (float) (Math.PI * 2 / SLICE_COUNT);
				for (Vector2 point : outline) {
					point.set((float) (Math.sin(point.y * sliceRadians) * (INNER_RADIUS + point.x) * LAYER_SCALE), (float) (Math.cos(point.y * sliceRadians) * (INNER_RADIUS + point.x) * LAYER_SCALE)).add(position);
				}
			}
		}
	}

	/**
	 * Adds a direction to a vector.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The direction has been added to the vector.
	 */
	private void addDirection(Vector2 position, int direction) {
		switch (direction) {
		case 0:
			position.add(0, 1);
			break;
		case 1:
			position.add(1, 1);
			break;
		case 2:
			position.add(1, 0);
			break;
		case 3:
			position.add(1, -1);
			break;
		case 4:
			position.add(0, -1);
			break;
		case 5:
			position.add(-1, -1);
			break;
		case 6:
			position.add(-1, 0);
			break;
		case 7:
			position.add(-1, 1);
			break;
		}
	}

	/**
	 * Creates the planets physics body.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The planet's physics body has been created.
	 */
	private void createBody() {
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(position);
		bodyDef.type = BodyType.StaticBody;

		body = game.getWorld().createBody(bodyDef);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 1;

		PolygonShape polygonShape = new PolygonShape();
		Vector2[] vertices = new Vector2[4];
		for (int i = 0; i < 4; i++) {
			vertices[i] = new Vector2();
		}

		float sliceRadians = (float) (Math.PI * 2 / SLICE_COUNT);

		for (int layer = 0; layer < LAYER_COUNT; layer++) {
			for (int slice = 0; slice < SLICE_COUNT; slice++) {
				vertices[0].x = (float) Math.sin((slice - 0.5f) * sliceRadians) * (INNER_RADIUS + layer + 0.5f) * LAYER_SCALE;
				vertices[0].y = (float) Math.cos((slice - 0.5f) * sliceRadians) * (INNER_RADIUS + layer + 0.5f) * LAYER_SCALE;

				vertices[1].x = (float) Math.sin((slice + 0.5f) * sliceRadians) * (INNER_RADIUS + layer + 0.5f) * LAYER_SCALE;
				vertices[1].y = (float) Math.cos((slice + 0.5f) * sliceRadians) * (INNER_RADIUS + layer + 0.5f) * LAYER_SCALE;

				vertices[2].x = (float) Math.sin((slice + 0.5f) * sliceRadians) * (INNER_RADIUS + layer - 0.5f) * LAYER_SCALE;
				vertices[2].y = (float) Math.cos((slice + 0.5f) * sliceRadians) * (INNER_RADIUS + layer - 0.5f) * LAYER_SCALE;

				vertices[3].x = (float) Math.sin((slice - 0.5f) * sliceRadians) * (INNER_RADIUS + layer - 0.5f) * LAYER_SCALE;
				vertices[3].y = (float) Math.cos((slice - 0.5f) * sliceRadians) * (INNER_RADIUS + layer - 0.5f) * LAYER_SCALE;

				polygonShape.set(vertices);
				fixtureDef.shape = polygonShape;
				Fixture fixture = body.createFixture(fixtureDef);
				planetBits[layer][slice].setFixture(fixture);
			}
		}
		polygonShape.dispose();
	}

	/**
	 * Gets the planet's remaining health.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public float getHealth() {
		return health;
	}
	
	/**
	 * Sets the planet's remaining health.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The planet's health is set.
	 */
	public void setHealth(float health) {
		this.health = health;
	}

	/**
	 * Renders the planet.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public void render(Camera camera) {
		if (rebuild) {
			if (Gdx.app.getType() == ApplicationType.Android) {
				System.out.println("start");
			}
			createBorders();
			createOutlines();
			rebuild = false;
			if (Gdx.app.getType() == ApplicationType.Android) {
				System.out.println("end");
			}
		}
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(color);
		for (ArrayList<Vector2> outline : outlines) {
			for (int i = 0; i < outline.size() - 1; i++) {
				shapeRenderer.line(outline.get(i), outline.get(i + 1));
			}
			shapeRenderer.line(outline.get(outline.size() - 1), outline.get(0));
		}
		shapeRenderer.end();
	}

	/**
	 * Disposes of the planet.
	 * 
	 * pre:
	 * The planet is not disposed.
	 * post:
	 * The planet is disposed.
	 */
	@Override
	public void dispose() {
		game.spawn(new ParticleEffectSpawner(position, ParticleEffectType.RAINBOW));
		game.spawn(new ParticleEffectSpawner(position, ParticleEffectType.RAINBOW));
		for (int layer = 0; layer < LAYER_COUNT; layer++) {
			for (int slice = 0; slice < SLICE_COUNT; slice++) {
				planetBits[layer][slice].setFilled(false);
			}
		}
		game.getGravityObjects().remove(this);
		game.getRenderables().remove(this);
		game.getSendables().remove(this);
		game.getDamagables().remove(this);
		game.getDisposables().remove(this);
		body.getWorld().destroyBody(body);
		shapeRenderer.dispose();
	}

	/**
	 * Called to get the sync data for this planet.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public Object send() {
		return new PlanetSyncData(this);
	}

	/**
	 * Syncs the planet with the given data.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Planet is synced.
	 */
	@Override
	public void recieve(Object object) {
		PlanetSyncData data = (PlanetSyncData)object;
		data.receive(this);
	}

}
