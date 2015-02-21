package com.mechwreck;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Manages the game's ui skin.
 */
public class SkinManager {
	
	private static Skin skin = createSkin();
	private static BitmapFont font;
	
	/**
	 * Get's the game's ui skin.
	 * 
	 * pre:
	 * The skin is created.
	 * post:
	 * None.
	 */
	public static Skin getSkin() {
		return skin;
	}
	
	/*
	 * Creates the skin for the libgdx ui.
	 * 
	 * pre:
	 * None.
	 * post:
	 * Skin is created.
	 */
	private static Skin createSkin() {
		skin = new Skin();
		font = new BitmapFont(Gdx.files.internal("fonts/Anita Semi-square.fnt"),
				              			 Gdx.files.internal("fonts/Anita Semi-square.png"),
				              			 false);

		font.scale(Gdx.graphics.getDensity() / 2f);

		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = font;
		labelStyle.fontColor = Color.WHITE;
		
		TextFieldStyle textFieldStyle = new TextFieldStyle();
		textFieldStyle.font = font;
		textFieldStyle.fontColor = Color.WHITE;
		textFieldStyle.focusedFontColor = Color.GREEN;
		
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.font = font;
		textButtonStyle.fontColor = Color.WHITE;
		textButtonStyle.overFontColor = Color.GREEN;
		
		ButtonStyle leftButtonStyle = new ButtonStyle();
		leftButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("textures/arrow_left.png"))));

		ButtonStyle rightButtonStyle = new ButtonStyle();
		rightButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("textures/arrow_right.png"))));

		ButtonStyle jumpButtonStyle = new ButtonStyle();
		jumpButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("textures/jump.png"))));

		ButtonStyle missileButtonStyle = new ButtonStyle();
		missileButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("textures/missileicon.png"))));

		ButtonStyle tripleMissileButtonStyle = new ButtonStyle();
		tripleMissileButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("textures/tripleicon.png"))));

		ButtonStyle cannonButtonStyle = new ButtonStyle();
		cannonButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("textures/cannonicon.png"))));

		ButtonStyle pauseButtonStyle = new ButtonStyle();
		pauseButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("textures/pause.png"))));

		skin.add("default", labelStyle, LabelStyle.class);
		skin.add("default", textFieldStyle, TextFieldStyle.class);
		skin.add("default", textButtonStyle, TextButtonStyle.class);
		skin.add("lb", leftButtonStyle, ButtonStyle.class);
		skin.add("rb", rightButtonStyle, ButtonStyle.class);
		skin.add("jb", jumpButtonStyle, ButtonStyle.class);
		skin.add("mb", missileButtonStyle, ButtonStyle.class);
		skin.add("tmb", tripleMissileButtonStyle, ButtonStyle.class);
		skin.add("cb", cannonButtonStyle, ButtonStyle.class);
		skin.add("pb", pauseButtonStyle, ButtonStyle.class);
		
		return skin;
	}

}
