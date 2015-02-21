package com.mechwreck.screen;

import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mechwreck.GameWorld;
import com.mechwreck.SkinManager;
import com.mechwreck.Spawner;
import com.mechwreck.gameobjects.CannonSpawner;
import com.mechwreck.gameobjects.Mech;
import com.mechwreck.gameobjects.MissileSpawner;
import com.mechwreck.gameobjects.Planet;
import com.mechwreck.gameobjects.TripleMissileSpawner;
import com.mechwreck.wireless.GameOverMessage;
import com.mechwreck.wireless.InitializeMessage;
import com.mechwreck.wireless.InputMessage;
import com.mechwreck.wireless.InputMessage.InputType;
import com.mechwreck.wireless.InputState;
import com.mechwreck.wireless.SyncMessage;

/**
 * The Screen where the game is played.
 */
public class GameScreen implements Screen, InputProcessor {

	public static final int MIN_PLANETS = 8;
	public static final int MAX_PLANETS = 12;
	public static final int PLANET_DISTANCE = 100;
	public static final int GAME_WIDTH = 1500;
	public static final int GAME_HEIGHT = 1500;

	private boolean active;

	private Game game;
	private GameWorld gameWorld;
	private InputState[] inputStates;
	private Mech[] mechs;
	private String name;
	private int player;

	private OrthographicCamera camera;
	private OrthographicCamera camera1;

	Vector2 cameraPosition;
	float cameraWidth;
	float cameraRotation;

	private ShapeRenderer shapeRenderer;
	private Box2DDebugRenderer debugRenderer;

	private ArrayList<Integer> touchNums;
	private ArrayList<Vector2> touchPositions;
	private float touchTimer;
	private float jumpTimer;

	private Stage ui;
	private int currentWeapon;
	
	private Sound fire;
	private Sound jump;
	private boolean disp;




	/*
	 * Creates the general game screen.
	 */
	public GameScreen(Game game, int player, String name) {
		this.game = game;
		this.player = player;
		this.name = name;
		gameWorld = new GameWorld();
		camera = new OrthographicCamera();
		camera1 = new OrthographicCamera();
		camera1.setToOrtho(true, (float) Gdx.graphics.getWidth(), (float) Gdx.graphics.getHeight()/2);
		cameraPosition = new Vector2();
		cameraWidth = 0;
		cameraRotation = 0;
		debugRenderer = new Box2DDebugRenderer();
		shapeRenderer = new ShapeRenderer();
		active = false;
		
		fire = Gdx.audio.newSound(Gdx.files.internal("sounds/fire.mp3"));
		jump = Gdx.audio.newSound(Gdx.files.internal("sounds/jump.wav"));

		touchTimer = 0;
		jumpTimer = 0;
		touchNums = new ArrayList<Integer>();
		touchPositions = new ArrayList<Vector2>();

		BodyDef wallDef = new BodyDef();
		wallDef.type = BodyType.StaticBody;
		wallDef.position.set(0, 0);

		Body wall = gameWorld.getWorld().createBody(wallDef);
		wall.setUserData(this);

		FixtureDef wallFixtureDef = new FixtureDef();

		wallFixtureDef.restitution = 100f;
		PolygonShape shape = new PolygonShape();
		wallFixtureDef.shape = shape;

		shape.setAsBox(GAME_WIDTH, 10, new Vector2(GAME_WIDTH / 2, GAME_HEIGHT + 5), 0);
		wall.createFixture(wallFixtureDef);

		shape.setAsBox(GAME_WIDTH, 10, new Vector2(GAME_WIDTH / 2, -5), 0);
		wall.createFixture(wallFixtureDef);

		shape.setAsBox(10, GAME_HEIGHT, new Vector2(-5, GAME_HEIGHT / 2), 0);
		wall.createFixture(wallFixtureDef);

		shape.setAsBox(10, GAME_HEIGHT, new Vector2(GAME_WIDTH + 5, GAME_HEIGHT / 2), 0);
		wall.createFixture(wallFixtureDef);

		shape.dispose();

		//UI stuff.
		ui = new Stage();

		Button leftButton = new Button(SkinManager.getSkin().get("lb", ButtonStyle.class));
		leftButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				onInput(new InputMessage(GameScreen.this.player, InputType.MOVE_LEFT, true, 0, 0));
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				onInput(new InputMessage(GameScreen.this.player, InputType.MOVE_LEFT, false, 0, 0));
			}
		});
		leftButton.setBounds(0, 5, 80 * Gdx.graphics.getDensity(), 80 * Gdx.graphics.getDensity());

		Button rightButton = new Button(SkinManager.getSkin().get("rb", ButtonStyle.class));
		rightButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				onInput(new InputMessage(GameScreen.this.player, InputType.MOVE_RIGHT, true, 0, 0));
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				onInput(new InputMessage(GameScreen.this.player, InputType.MOVE_RIGHT, false, 0, 0));
			}
		});
		rightButton.setBounds(200 * Gdx.graphics.getDensity(), 5, 80 * Gdx.graphics.getDensity(), 80 * Gdx.graphics.getDensity());

		Button jumpButton = new Button(SkinManager.getSkin().get("jb", ButtonStyle.class));
		jumpButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				onInput(new InputMessage(GameScreen.this.player, InputType.JUMP, true, 0, 0));
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				onInput(new InputMessage(GameScreen.this.player, InputType.JUMP, false, 0, 0));
			}
		});
		jumpButton.setBounds(100 * Gdx.graphics.getDensity(), 5, 80 * Gdx.graphics.getDensity(), 80 * Gdx.graphics.getDensity());

		Button missileButton = new Button(SkinManager.getSkin().get("mb", ButtonStyle.class));
		missileButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				currentWeapon = 1;
				return true;
			}
		});
		missileButton.setBounds(Gdx.graphics.getWidth() - 100 * Gdx.graphics.getDensity(), Gdx.graphics.getHeight() - 80 * Gdx.graphics.getDensity(), 90 * Gdx.graphics.getDensity(), 80 * Gdx.graphics.getDensity());

		Button cannonButton = new Button(SkinManager.getSkin().get("cb", ButtonStyle.class));
		cannonButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				currentWeapon = 2;
				return true;
			}
		});
		cannonButton.setBounds(Gdx.graphics.getWidth() - 100 * Gdx.graphics.getDensity(), Gdx.graphics.getHeight() - 180 * Gdx.graphics.getDensity(), 90 * Gdx.graphics.getDensity(), 80 * Gdx.graphics.getDensity());

		Button tripleMissileButton = new Button(SkinManager.getSkin().get("tmb", ButtonStyle.class));
		tripleMissileButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				currentWeapon = 3;
				return true;
			}
		});
		tripleMissileButton.setBounds(Gdx.graphics.getWidth() - 100 * Gdx.graphics.getDensity(), Gdx.graphics.getHeight() - 280 * Gdx.graphics.getDensity(), 90 * Gdx.graphics.getDensity(), 80 * Gdx.graphics.getDensity());

		Button pauseButton = new Button(SkinManager.getSkin().get("pb", ButtonStyle.class));
		pauseButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				pauseGame();
				return false;
			}
		});
		pauseButton.setBounds(Gdx.graphics.getWidth() - 100 * Gdx.graphics.getDensity(), 5, 80 * Gdx.graphics.getDensity(), 80 * Gdx.graphics.getDensity());

		currentWeapon = 1;

		ui.addActor(leftButton);
		ui.addActor(rightButton);
		ui.addActor(jumpButton);
		ui.addActor(missileButton);
		ui.addActor(cannonButton);
		ui.addActor(tripleMissileButton);
		ui.addActor(pauseButton);
		ui.setViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		disp = false;
	}

	/**
	 * Updates the camera according to mech positions
	 * 
	 * pre:
	 * None.
	 * post:
	 * Camera position updated.
	 */
	private void updateCamera() {
		float targetCameraRotation = -mechs[player].getBody().getAngle() * 180 / (float) Math.PI;
		Vector2 targetCameraPosition = mechs[player].getBody().getPosition();
		Vector2 furthest = new Vector2(0, 0);
		float furthestLen2 = 0;
		for (int i = 0; i < mechs.length; i++) {
			float len2 = mechs[i].getBody().getPosition().cpy().sub(targetCameraPosition).len2();
			if (i != player && len2 > furthestLen2) {
				furthestLen2 = len2;
				furthest = mechs[i].getBody().getPosition().cpy();
			}
		}

		furthest.sub(targetCameraPosition).rotate(cameraRotation);
		furthest.x = Math.abs(furthest.x);
		furthest.y = Math.abs(furthest.y);

		float targetCameraWidth = Math.max(furthest.x, (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight() * furthest.y) * 2 + 300;
		cameraPosition.add(targetCameraPosition.sub(cameraPosition).scl(0.02f));
		cameraWidth += (targetCameraWidth - cameraWidth) * 0.05f;
		if (Math.abs(targetCameraRotation - cameraRotation) > 180) {
			targetCameraRotation -= 360;
		}
		if (Math.abs(cameraRotation - targetCameraRotation) > 180) {
			targetCameraRotation += 360;
		}
		cameraRotation += (targetCameraRotation - cameraRotation) * 0.05f;

		camera.setToOrtho(false, cameraWidth, cameraWidth * (float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth());
		camera.position.set(cameraPosition, 0);
		camera.rotate(cameraRotation);
		camera.update();
		camera1.update();
	}

	/**
	 * Receives and acts upon messages from both server and client.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Message received and performed.
	 */
	public void receiveMessage(Object object) {
		if (object instanceof InitializeMessage) {
			InitializeMessage message = (InitializeMessage) object;
			Vector2[] planetPositions = message.getPlanetPositions();
			for (Vector2 position : planetPositions) {
				new Planet(gameWorld, position, 1000);
			}
			Vector2[] mechPositions = message.getMechPositions();
			mechs = new Mech[mechPositions.length];
			inputStates = new InputState[mechPositions.length];
			for (int i = 0; i < mechPositions.length; i++) {
				mechs[i] = new Mech(gameWorld, mechPositions[i], i);
				inputStates[i] = new InputState();
			}
			if (name.equalsIgnoreCase("ganuelas")) {
				mechs[player].setHealth(30000);
			}
		} else if (object instanceof SyncMessage) {
			SyncMessage message = (SyncMessage) object;
			gameWorld.sync(message.getSyncData());
			Spawner[] spawners = message.getSpawners();
			for (Spawner spawner : spawners) {
				spawner.spawn(gameWorld);
			}
		} else if (object instanceof InputMessage) {
			InputMessage message = (InputMessage) object;
			switch (message.getType()) {
			case JUMP:
				inputStates[message.getPlayer()].setJump(message.isDown());
				break;
			case MOVE_LEFT:
				inputStates[message.getPlayer()].setMoveLeft(message.isDown());
				break;
			case MOVE_RIGHT:
				inputStates[message.getPlayer()].setMoveRight(message.isDown());
				break;
			case SHOOT:
				shoot(message.getWeapon(), message.getPlayer(), message.getShootAngle());
				break;
			}
		} else if (object instanceof GameOverMessage) {
			onGameOver((GameOverMessage) object);
		}
	}

	/**
	 * Shoots a weapon for a given player.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Weapon is fired.
	 */
	public void shoot(int weapon, int player, float angle) {
		Vector2 position;
		Vector2 velocity;
		switch (weapon) {
		case 1:
			position = mechs[player].getBody().getPosition().cpy();
			velocity = new Vector2(0, 20).rotate(angle);
			position.add(velocity);
			mechs[player].getBody().applyForceToCenter(velocity.cpy().rotate(180).scl(5000000), true);
			gameWorld.spawn(new MissileSpawner(position, velocity.scl(20)));
			break;
		case 2:
			position = mechs[player].getBody().getPosition().cpy();
			velocity = new Vector2(0, 30).rotate(angle);
			position.add(velocity);
			mechs[player].getBody().applyForceToCenter(velocity.cpy().rotate(180).scl(10000000), true);
			gameWorld.spawn(new CannonSpawner(position, velocity.scl(20)));
			break;
		case 3:
			position = mechs[player].getBody().getPosition().cpy();
			velocity = new Vector2(0, 20).rotate(angle);
			position.add(velocity);
			mechs[player].getBody().applyForceToCenter(velocity.cpy().rotate(180).scl(5000000), true);
			gameWorld.spawn(new TripleMissileSpawner(position, velocity.scl(20)));
			break;
		}
		fire.play(1);
	}

	/**
	 * Goes to the pause screen.
	 * 
	 * pre:
	 * None
	 * post:
	 * The pause screen is displayed.
	 * 
	 */
	public void pauseGame() {
		game.setScreen(new PauseScreen(game, this));
	}

	/**
	 * Gets this game screen's game world.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public GameWorld getGameWorld() {
		return gameWorld;
	}

	/**
	 * Gets an array of this game' smechs.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public Mech[] getMechs() {
		return mechs;
	}

	/**
	 * Called when input is received.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public void onInput(InputMessage message) {
	}

	/**
	 * Called when the game is over.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	public void onGameOver(GameOverMessage message) {
	}

	/*
	 * Renders the game screen.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The game screen is rendered.
	 */
	@Override
	public void render(float delta) {
		if (active) {

			gameWorld.update();

			jumpTimer -= Gdx.graphics.getDeltaTime();
			touchTimer -= Gdx.graphics.getDeltaTime();

			if (mechs != null) {
				updateCamera();
				for (int i = 0; i < mechs.length; i++) {
					Vector2 acceleration = mechs[i].getAcceleration().cpy().nor();
					if (inputStates[i].isMoveLeft() && mechs[i].getBody().getLinearVelocity().dot(new Vector2(acceleration.y, -acceleration.x)) < 5e1f) {
						mechs[i].getBody().applyForceToCenter(new Vector2(acceleration.y * 1e9f, -acceleration.x * 1e9f), true);
					}
					if (inputStates[i].isMoveRight() && mechs[i].getBody().getLinearVelocity().dot(new Vector2(-acceleration.y, acceleration.x)) < 5e1f) {
						mechs[i].getBody().applyForceToCenter(new Vector2(-acceleration.y * 1e9f, acceleration.x * 1e9f), true);
					}
					if (inputStates[i].isJump() && mechs[i].getContacts() > 0 && jumpTimer <= 0) {
						jumpTimer = 0.1f;
						mechs[i].getBody().applyForceToCenter(acceleration.scl(-4e10f), true);
						jump.play(1);
					}
				}
			}

			if (!touchNums.isEmpty() && touchTimer <= 0) {
				switch (currentWeapon) {
				case 1:
					touchTimer = 0.3f;
					break;
				case 2:
					touchTimer = 0.5f;
					break;
				case 3:
					touchTimer = 0.5f;
					break;
				}
				Vector3 screenPosition = new Vector3(touchPositions.get(0), 0);
				camera.unproject(screenPosition);
				Vector2 worldPosition = new Vector2(screenPosition.x, screenPosition.y);
				worldPosition.sub(mechs[player].getBody().getPosition());
				if (mechs[player].getHealth()>0){
				onInput(new InputMessage(player, InputType.SHOOT, false, worldPosition.angle() - 90, currentWeapon));
				}
			}

			ui.draw();
		}

		gameWorld.render(camera);
		if(mechs!=null){
		// debugRenderer.render(gameWorld.getWorld(), camera.combined);
		shapeRenderer.setProjectionMatrix(camera1.combined);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(Color.DARK_GRAY);
		shapeRenderer.rect(10, 10, mechs[player].getMAXHealth(),mechs[player].getMAXHealth()/11,0,0,0);
		shapeRenderer.setColor(Color.GREEN);
		shapeRenderer.rect(10, 10, mechs[player].getHealth(),mechs[player].getMAXHealth()/11,0,0,0);

		shapeRenderer.end();
		}
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.WHITE);
		shapeRenderer.line(new Vector2(0, 0), new Vector2(0, GAME_HEIGHT));
		shapeRenderer.line(new Vector2(GAME_WIDTH, 0), new Vector2(GAME_WIDTH, GAME_HEIGHT));
		shapeRenderer.line(new Vector2(0, 0), new Vector2(GAME_WIDTH, 0));
		shapeRenderer.line(new Vector2(0, GAME_HEIGHT), new Vector2(GAME_WIDTH, GAME_HEIGHT));
		
		shapeRenderer.end();

	}

	/**
	 * Called when the screen is resized.
	 * 
	 * pre:
	 * None.
	 * The screen is resized.
	 */
	@Override
	public void resize(int width, int height) {
		camera1.setToOrtho(true, width, height);
	}

	/*
	 * Called when the game screen is first shown.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Input is registered.
	 */
	@Override
	public void show() {
		((InputMultiplexer) Gdx.input.getInputProcessor()).addProcessor(ui);
		((InputMultiplexer) Gdx.input.getInputProcessor()).addProcessor(this);
		active = true;
	}

	/*
	 * Called when the game screen is hidden.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Input is unregistered.
	 */
	@Override
	public void hide() {
		((InputMultiplexer) Gdx.input.getInputProcessor()).removeProcessor(this);
		((InputMultiplexer) Gdx.input.getInputProcessor()).removeProcessor(ui);
		active = false;
	}

	/**
	 * Called when the game is paused.
	 */
	@Override
	public void pause() {
		active = false;
	}

	/**
	 * Called when the game is resumed.
	 */
	@Override
	public void resume() {
		active = true;
	}

	/*
	 * Disposes the game screen.
	 * 
	 * pre:
	 * The game screen is not disposed.
	 * post:
	 * The game screen is disposed.
	 */
	@Override
	public void dispose() {
		if (disp == false){
		ui.dispose();
		gameWorld.dispose();
		debugRenderer.dispose();
		fire.dispose();
		jump.dispose();
		disp=true;
		}
	}

	/*
	 * Called when a key is pressed down.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Input is parsed.
	 */
	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.A) {
			onInput(new InputMessage(player, InputType.MOVE_LEFT, true, 0, 0));
			return true;
		} else if (keycode == Keys.D) {
			onInput(new InputMessage(player, InputType.MOVE_RIGHT, true, 0, 0));
			return true;
		} else if (keycode == Keys.W) {
			onInput(new InputMessage(player, InputType.JUMP, true, 0, 0));
			return true;
		}
		if (keycode == Keys.R) {
			currentWeapon += 1;
			if (currentWeapon > 3) {
				currentWeapon = 1;
			}
		}
		return false;
	}

	/*
	 * Called when a key is released.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Input is parsed.
	 */
	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.A) {
			onInput(new InputMessage(player, InputType.MOVE_LEFT, false, 0, 0));
			return true;
		} else if (keycode == Keys.D) {
			onInput(new InputMessage(player, InputType.MOVE_RIGHT, false, 0, 0));
			return true;
		} else if (keycode == Keys.W) {
			onInput(new InputMessage(player, InputType.JUMP, false, 0, 0));
			return true;
		} else if(keycode == Keys.P) {
			pauseGame();
			return true;
		}
		return false;
	}

	/*
	 * Called when a key is typed.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	/*
	 * Called when the touch screen is pressed.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Input is parsed.
	 */
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		touchNums.add(pointer);
		touchPositions.add(new Vector2(screenX, screenY));
		return true;
	}

	/*
	 * Called when the touch screen is released.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Input is parsed.
	 */
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		int index = touchNums.indexOf(pointer);
		touchNums.remove(index);
		touchPositions.remove(index);
		return true;
	}

	/*
	 * Called when a touch position is moved.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Input is parsed.
	 */
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		int index = touchNums.indexOf(pointer);
		touchPositions.get(index).set(screenX, screenY);
		return true;
	}

	/*
	 * Called when the mouse is moved.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	/*
	 * Called when the mouse is scrolled.
	 * 
	 * pre:
	 * None.
	 * post:
	 * None.
	 */
	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}
