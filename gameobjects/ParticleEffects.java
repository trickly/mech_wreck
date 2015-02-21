package com.mechwreck.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.mechwreck.GameWorld;
import com.mechwreck.Renderable;

/**
 * Class responsible for spawning particle effect animations.
 */
public class ParticleEffects implements Renderable, Disposable {

	private Vector2 position;
	private GameWorld gameWorld;
	private SpriteBatch batch; 
	private PooledEffect effect;
	private static ParticleEffectPool cyanPool = createPool("effects/cyanExplosion.p", "effects");
	private static ParticleEffectPool rainbowPool = createPool("effects/rainbowExplosion.p", "effects");
	private static ParticleEffectPool bluePool = createPool("effects/blueExplosion.p", "effects");
	private static ParticleEffectPool pinkPool = createPool("effects/pinkExplosion.p", "effects");
	private static ParticleEffectPool purplePool = createPool("effects/purpleExplosion.p", "effects");
	private static ParticleEffectPool greenPool = createPool("effects/greenExplosion.p", "effects");
	private static ParticleEffectPool orangePool = createPool("effects/orangeExplosion.p", "effects");

	private ParticleEffectType type;

	/*
	 * Creates a new particle effect instance.
	 */
	public ParticleEffects(GameWorld gameWorld, Vector2 position, ParticleEffectType type) {
		this.position = position;
		this.gameWorld = gameWorld;
		this.type = type;
		batch = new SpriteBatch();
		switch(type) {
		case CYAN:
			effect = cyanPool.obtain();
			break;
		case RAINBOW:
			effect = rainbowPool.obtain();
			break;
		case BLUE:
			effect = bluePool.obtain();
			break;
		case PINK:
			effect = pinkPool.obtain();
			break;
		case ORANGE:
			effect = orangePool.obtain();
			break;
		case PURPLE:
			effect = purplePool.obtain();
			break;
		case GREEN:
			effect = greenPool.obtain();
			break;
		}
		effect.start();

		gameWorld.getRenderables().add(this);
		gameWorld.getDisposables().add(this);
	}
	
	/**
	 * Creates a pool of particle effects from a given file.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Pool is created.
	 */
	private static ParticleEffectPool createPool(String file, String folder) {
		ParticleEffect effect = new ParticleEffect();
		effect.load(Gdx.files.internal(file), Gdx.files.internal(folder));
		ParticleEffectPool pool = new ParticleEffectPool(effect, 10, 100);
		return pool;
	}
	
	/**
	 * Empty function used to load the particle effects class.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Particle effects class has loaded.
	 */
	public static void YOLO() {
		
	}

	/**
	 * Renders the particle effects.
	 * 
	 * pre:
	 * None.
	 * post:
	 * The particle effects are rendered.
	 */
	@Override
	public void render(Camera camera) {
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		effect.setPosition(position.x, position.y);
		effect.draw(batch, Gdx.graphics.getDeltaTime());
		batch.end();
		if (effect.isComplete()) {
			dispose();
		}
	}

	/*
	 * Disposes of the particle effects.
	 * 
	 * pre:
	 * Particle effects not disposed.
	 * post:
	 * Particle effects disposed.
	 */
	@Override
	public void dispose() {
		gameWorld.getRenderables().remove(this);
		gameWorld.getDisposables().remove(this);
		switch(type) {
		case CYAN:
			cyanPool.free(effect);
			break;
		case RAINBOW:
			rainbowPool.free(effect);
			break;
		case BLUE:
			bluePool.free(effect);
			break;
		case PINK:
			pinkPool.free(effect);
			break;
		case ORANGE:
			orangePool.free(effect);
			break;
		case PURPLE:
			purplePool.free(effect);
			break;
		case GREEN:
			greenPool.free(effect);
			break;
		}
	}
	
	/*
	 * A selection of colours for the particle effects.
	 */
	public enum ParticleEffectType {
		CYAN,
		RAINBOW,
		BLUE,
		PINK,
		ORANGE,
		PURPLE,
		GREEN
		
	}

}
