package com.mechwreck.gameobjects;

import com.badlogic.gdx.math.Vector2;
import com.mechwreck.GameWorld;
import com.mechwreck.Spawner;
import com.mechwreck.gameobjects.ParticleEffects.ParticleEffectType;

/**
 * Spawner class for particle effects.
 */
public class ParticleEffectSpawner implements Spawner {
	
	private Vector2 position;
	private ParticleEffectType type;
	
	/*
	 * Creates a new particle effect spawner.
	 */
	public ParticleEffectSpawner() {
	}

	/*
	 * Creates a new particle effect spawner from a given position and effect type.
	 */
	public ParticleEffectSpawner(Vector2 position, ParticleEffectType type) {
		this.position = position;
		this.type = type;
	}

	/*
	 * Spawns a particle effects instance.
	 */
	@Override
	public void spawn(GameWorld gameWorld) {
		new ParticleEffects(gameWorld, position, type);
	}

}
