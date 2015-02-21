package com.mechwreck;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

/**
 * Class that displays the game's background and can be used by different
 * screens so that the background is consistent.
 */
public class Background implements Disposable {

	private float time;
	private Vector2[][] randomGrid;
	private ShapeRenderer shapeRenderer;

	/**
	 * Creates a new Background.
	 */
	public Background() {
		time = 0;
		Random rand = new Random();
		randomGrid = new Vector2[101][101];
		for (int i = 0; i < randomGrid.length; i++) {
			for (int j = 0; j < randomGrid[0].length; j++) {
				randomGrid[i][j] = new Vector2(rand.nextFloat() - 0.5f, rand.nextFloat() - 0.5f).nor();
			}
		}

		shapeRenderer = new ShapeRenderer();
	}

	/**
	 * Renders the background using the supplied camera.
	 * 
	 * pre: None.
	 * post: Background rendered.
	 */
	public void render(Camera camera) {
		time += Gdx.graphics.getRawDeltaTime();
		time %= 100;

		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(new Color(0.5f, 0.5f, 0.5f, 1f));
		Vector2 v1 = new Vector2();
		Vector2 v2 = new Vector2();
		for (int i = 0; i < 100; i++) {
			v1.set(i * 100 + noise(new Vector2(i / 2f, time / 2f)) * 100f, 0);
			v2.set(i * 100 + noise(new Vector2(i / 2f, time / 2f)) * 100f, 10000);
			shapeRenderer.line(v1, v2);
			v1.set(0, i * 100 + noise(new Vector2(i / 2f, time / 2f)) * 100f);
			v2.set(10000, i * 100 + noise(new Vector2(i / 2f, time / 2f)) * 100f);
			shapeRenderer.line(v1, v2);
		}
		shapeRenderer.end();
	}

	/**
	 * 2D Perlin noise implementation. Returns the noise value at the given
	 * point.
	 * 
	 * pre: randomGrid is created.
	 * post: None.
	 */
	private float noise(Vector2 point) {
		Vector2 bottomLeft = randomGrid[(int) point.x][(int) point.y];
		Vector2 topLeft = randomGrid[(int) point.x][(int) point.y + 1];
		Vector2 bottomRight = randomGrid[(int) point.x + 1][(int) point.y];
		Vector2 topRight = randomGrid[(int) point.x + 1][(int) point.y + 1];

		float bottomLeftValue = bottomLeft.dot(point.cpy().scl(-1).add((int) point.x, (int) point.y));
		float topLeftValue = topLeft.dot(point.cpy().scl(-1).add((int) point.x, (int) point.y + 1));
		float bottomRightValue = bottomRight.dot(point.cpy().scl(-1).add((int) point.x + 1, (int) point.y));
		float topRightValue = topRight.dot(point.cpy().scl(-1).add((int) point.x + 1, (int) point.y + 1));

		float bottomValue = bottomLeftValue * (1 - point.x % 1) + bottomRightValue * (point.x % 1);
		float topValue = topLeftValue * (1 - point.x % 1) + topRightValue * (point.x % 1);
		float value = bottomValue * (1 - point.y % 1) + topValue * (point.y % 1);

		return value;
	}

	/**
	 * Dispose of background.
	 * 
	 * pre: Background not disposed.
	 * post: Background disposed.
	 */
	@Override
	public void dispose() {
		shapeRenderer.dispose();
	}

}
