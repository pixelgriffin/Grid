package com.pixelgriffin.grid.screen;

import java.util.HashSet;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pixelgriffin.grid.android.GridGame;
import com.pixelgriffin.grid.entity.Bullet;
import com.pixelgriffin.grid.entity.Explosion;
import com.pixelgriffin.grid.input.MainMenuInputProcessor;
import com.pixelgriffin.grid.util.ColorUtility;
import com.pixelgriffin.grid.util.NumberUtility;
import com.pixelgriffin.grid.util.SettingsUtility;

/**
 * 
 * @author Nathan
 *
 */
public class MainMenuScreen implements Screen {

	public static final int INDEX_MAIN = 0;
	public static final int INDEX_DIFFICULTY = 1;
	public static final int INDEX_NETWORK = 2;
	
	private OrthographicCamera cam;
	private Texture buttonTex, musicTex;
	
	private float titleLength;
	private float playLength, multiLength, tutLength;
	private float easyLength, mediumLength, hardLength;
	private float quickLength, lobbyLength, inboxLength;
	
	private float titleHeight;
	
	//alpha changes
	private float colAlpha, colAlpha2;
	private boolean colAlphaUp, colAlphaUp2;
	private Random alphaRand;
	
	//bullets
	private float time;
	private HashSet<Bullet> bullets;
	private HashSet<Bullet> removeBullets;
	
	private int index;
	
	private MainMenuInputProcessor input;
	
	public MainMenuScreen(GridGame _instance) {
		this.index = 0;
		
		this.alphaRand = new Random();
		this.colAlpha = 0.6f;
		this.colAlpha2 = 0.8f;
		this.colAlphaUp = true;
		this.colAlphaUp2 = false;
		
		bullets = new HashSet<Bullet>();
		removeBullets = new HashSet<Bullet>();
		this.time = 0f;
		
		this.buttonTex = GridGame.getAssetManager().get("button.png", Texture.class);
		this.musicTex = GridGame.getAssetManager().get("music_button.png", Texture.class);
		
		this.cam = new OrthographicCamera();
		this.cam.setToOrtho(false, 1280, 720);
		
		this.titleLength = GridGame.getMegaFont().getBounds("GRID").width / 2;
		
		this.playLength = GridGame.getTitleFont().getBounds("SINGLE . PLAY").width / 2;
		this.multiLength = GridGame.getTitleFont().getBounds("MULTI . PLAY").width / 2;
		this.tutLength = GridGame.getTitleFont().getBounds("HOW . TO . PLAY").width / 2;
		
		this.easyLength = GridGame.getTitleFont().getBounds("EASY").width / 2;
		this.mediumLength = GridGame.getTitleFont().getBounds("MEDIUM").width / 2;
		this.hardLength = GridGame.getTitleFont().getBounds("HARD").width / 2;
		
		this.quickLength = GridGame.getTitleFont().getBounds("QUICK . MATCH").width / 2;
		this.lobbyLength = GridGame.getTitleFont().getBounds("LOBBY . MATCH").width / 2;
		this.inboxLength = GridGame.getTitleFont().getBounds("INVITE . INBOX").width / 2;
		
		
		this.titleHeight = GridGame.getTitleFont().getBounds("SINGLE . PLAY").height / 2;
		
		input = new MainMenuInputProcessor(this, _instance);
		GridGame.registerInputListener(input);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		/*
		 * Update
		 */
		this.cam.update();
		
		float nextRand = alphaRand.nextFloat() / 100f;
		
		if(colAlphaUp) {
			if(colAlpha < 0.9f)
				colAlpha += nextRand;
			else
				colAlphaUp = false;
		} else {
			if(colAlpha > 0.45f)
				colAlpha -= nextRand;
			else
				colAlphaUp = true;
		}
		
		nextRand = alphaRand.nextFloat() / 100f;
		
		if(colAlphaUp2) {
			if(colAlpha2 < 0.9f)
				colAlpha2 += nextRand;
			else
				colAlphaUp2 = false;
		} else {
			if(colAlpha2 > 0.45f)
				colAlpha2 -= nextRand;
			else
				colAlphaUp2 = true;
		}
		
		//ShaderManager.updateDefaultShader(1f);
		
		//add bullets
		this.time += delta;
		if(this.time > 0.25f) {
			this.time = 0f;
			
			bullets.add(new Bullet(true, NumberUtility.nextFloat(0, 1280), 720, -90));
		}
		
		//update bullets
		for(Bullet b : removeBullets) {
			bullets.remove(b);
		}
		removeBullets.clear();
		
		for(Bullet b : bullets) {
			if(!b.isDead())
				b.update(delta);
			else
				removeBullets.add(b);
		}
		
		
		/*
		 * Draw
		 */
		SpriteBatch b = GridGame.getDefaultBatch();
		b.setProjectionMatrix(this.cam.combined);
		b.begin();
			for(Bullet bul : bullets) {
				bul.draw(b);
			}
			//title & music toggle
			GridGame.getMegaFont().setColor(ColorUtility.playerColor);
			GridGame.getMegaFont().draw(b, "GRID", 640 - titleLength, 660);
			GridGame.getMegaFont().setColor(Color.WHITE);
			
			float musicAlpha = 1f;
			if(!SettingsUtility.musicOn) {
				musicAlpha = 0.5f;
			}
			
			b.setColor(ColorUtility.playerColor.r, ColorUtility.playerColor.g, ColorUtility.playerColor.b, musicAlpha);
				b.draw(this.musicTex, 1280 - 64, 0);
			b.setColor(Color.WHITE);
			
			//play button
			if(this.index == INDEX_MAIN) {
				b.setColor(ColorUtility.playerColor.r, ColorUtility.playerColor.g, ColorUtility.playerColor.b, colAlpha);
				b.draw(buttonTex, 640 -980, 360 -50 + 75, 1280, 100, 0, 0, 1280, 100, true, false);
				b.setColor(ColorUtility.playerColor.r, ColorUtility.playerColor.g, ColorUtility.playerColor.b, colAlpha2);
				b.draw(buttonTex, 640 -300, 360 -200 + 75, 1280, 100, 0, 0, 1280, 100, false, false);
				b.setColor(ColorUtility.playerColor.r, ColorUtility.playerColor.g, ColorUtility.playerColor.b, colAlpha);
				b.draw(buttonTex, 640 -980, 360 -350 + 75, 1280, 100, 0, 0, 1280, 100, true, false);
				b.setColor(Color.WHITE);
				
				GridGame.getTitleFont().setColor(ColorUtility.playerColor);
				GridGame.getTitleFont().draw(b, "SINGLE . PLAYER", 640 -playLength, 360 + titleHeight + 75);
				GridGame.getTitleFont().draw(b, "MULTI . PLAYER", 640 -multiLength, 360 + titleHeight - 150 + 75);
				GridGame.getTitleFont().draw(b, "HOW . TO . PLAY", 640 -tutLength, 360 + titleHeight - 300 + 75);
				GridGame.getTitleFont().setColor(Color.WHITE);
			} else if(this.index == INDEX_DIFFICULTY) {
				b.setColor(ColorUtility.playerColor.r, ColorUtility.playerColor.g, ColorUtility.playerColor.b, colAlpha);
				b.draw(buttonTex, 640 -980, 360 -50 + 75, 1280, 100, 0, 0, 1280, 100, true, false);
				b.setColor(ColorUtility.playerColor.r, ColorUtility.playerColor.g, ColorUtility.playerColor.b, colAlpha2);
				b.draw(buttonTex, 640 -300, 360 -200 + 75, 1280, 100, 0, 0, 1280, 100, false, false);
				b.setColor(ColorUtility.playerColor.r, ColorUtility.playerColor.g, ColorUtility.playerColor.b, colAlpha);
				b.draw(buttonTex, 640 -980, 360 -350 + 75, 1280, 100, 0, 0, 1280, 100, true, false);
				b.setColor(Color.WHITE);
				
				GridGame.getTitleFont().setColor(ColorUtility.playerColor);
				GridGame.getTitleFont().draw(b, "EASY", 640 -easyLength, 360 + titleHeight + 75);
				GridGame.getTitleFont().draw(b, "MEDIUM", 640 -mediumLength, 360 + titleHeight - 150 + 75);
				GridGame.getTitleFont().draw(b, "HARD", 640 -hardLength, 360 + titleHeight - 300 + 75);
				GridGame.getTitleFont().setColor(Color.WHITE);
			} else if(this.index == INDEX_NETWORK) {
				b.setColor(ColorUtility.playerColor.r, ColorUtility.playerColor.g, ColorUtility.playerColor.b, colAlpha);
				b.draw(buttonTex, 640 -980, 360 -50 + 75, 1280, 100, 0, 0, 1280, 100, true, false);
				b.setColor(ColorUtility.playerColor.r, ColorUtility.playerColor.g, ColorUtility.playerColor.b, colAlpha2);
				b.draw(buttonTex, 640 -300, 360 -200 + 75, 1280, 100, 0, 0, 1280, 100, false, false);
				b.setColor(ColorUtility.playerColor.r, ColorUtility.playerColor.g, ColorUtility.playerColor.b, colAlpha);
				b.draw(buttonTex, 640 -980, 360 -350 + 75, 1280, 100, 0, 0, 1280, 100, true, false);
				b.setColor(Color.WHITE);
				
				GridGame.getTitleFont().setColor(ColorUtility.playerColor);
				GridGame.getTitleFont().draw(b, "QUICK . MATCH", 640 -quickLength, 360 + titleHeight + 75);
				GridGame.getTitleFont().draw(b, "LOBBY . MATCH", 640 -lobbyLength, 360 + titleHeight - 150 + 75);
				GridGame.getTitleFont().draw(b, "INVITE . INBOX", 640 -inboxLength, 360 + titleHeight - 300 + 75);
				GridGame.getTitleFont().setColor(Color.WHITE);
			}
		b.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
		GridGame.unregisterInputListener(input);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
	
	public OrthographicCamera getCamera() {
		return this.cam;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public void setIndex(int _i) {
		this.index = _i;
	}
}
