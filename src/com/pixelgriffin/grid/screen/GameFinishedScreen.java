package com.pixelgriffin.grid.screen;

import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.android.gms.games.Games;
import com.pixelgriffin.grid.android.GridGame;
import com.pixelgriffin.grid.entity.Bullet;
import com.pixelgriffin.grid.input.FinishScreenInputProcessor;
import com.pixelgriffin.grid.input.NetworkWaitingInputProcessor;
import com.pixelgriffin.grid.util.ColorUtility;
import com.pixelgriffin.grid.util.NumberUtility;

/**
 * 
 * @author Nathan
 *
 */
public class GameFinishedScreen implements Screen {

	private OrthographicCamera cam;
	
	private float nameWidth;
	private String currentString;
	
	private float time;
	
	private FinishScreenInputProcessor input;
	
	private GridGame inst;
	
	//bullets
	private float bulletTime;
	private HashSet<Bullet> bullets;
	private HashSet<Bullet> removeBullets;
	
	public GameFinishedScreen(GridGame _instance, String _finish, boolean _net) {
		this.currentString = "YOU " + _finish;
		
		this.nameWidth = GridGame.getTitleFont().getBounds(this.currentString).width / 2f;

		this.time = 0f;
		
		this.cam = new OrthographicCamera();
		this.cam.setToOrtho(false, 1280, 720);
		
		this.input = new FinishScreenInputProcessor(_instance, _net);
		GridGame.registerInputListener(this.input);
		
		this.bulletTime = 0f;
		this.bullets = new HashSet<Bullet>();
		this.removeBullets = new HashSet<Bullet>();
		
		this.inst = GridGame.getInstance();
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		/*
		 * Update
		 */
		this.time += delta;
		if(time > 2f) {
			if(inst.getGPGS().getActiveRoomID() != null) {
				Games.RealTimeMultiplayer.leave(inst.getGPGS().getAPI(), inst.getGPGS().getInstance(), inst.getGPGS().getActiveRoomID());
				inst.getGPGS().nullifyRoomID();
			}
		}
		
		//add bullets
		this.bulletTime += delta;
		if(this.bulletTime > 0.25f) {
			this.bulletTime = 0f;
			
			bullets.add(new Bullet(false, NumberUtility.nextFloat(0, 1280), 0, 90));
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
		
		this.cam.update();
		
		/*
		 * Draw
		 */
		SpriteBatch b = GridGame.getDefaultBatch();
		
		b.setProjectionMatrix(this.cam.combined);
		b.begin();
			for(Bullet bul : bullets) {
				bul.draw(b);
			}
		
			GridGame.getTitleFont().draw(b, currentString, 640 - nameWidth, 360);
		b.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	//on first show
	@Override
	public void show() {
		//set the shader to our default
		//GridGame.getDefaultBatch().setShader(ShaderManager.DEFAULT_SHADER);
		inst.getGPGS().getInstance().showAd();
	}

	@Override
	public void hide() {
		//ShaderManager.updateDefaultShader(1f);
		GridGame.unregisterInputListener(this.input);
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

}
