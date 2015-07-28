package com.pixelgriffin.grid.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pixelgriffin.grid.android.GridGame;
import com.pixelgriffin.grid.util.ColorUtility;

/**
 * 
 * @author Nathan
 *
 */
public class Bullet implements Entity {
	private boolean isPlayerOwned;
	
	private Texture tex;
	private float x, y;
	
	private boolean dead;
	
	private double direction;
	
	private ParticleEmitter emitter;
	
	public Bullet(boolean _playerOwned, float _x, float _y, double _dir) {
		this.isPlayerOwned = _playerOwned;
		
		tex = GridGame.getAssetManager().get("bullet.png", Texture.class);
		
		x = _x;
		y = _y;
		direction = Math.toRadians(_dir);
		
		emitter = new ParticleEmitter(GridGame.getUnitEffects().getEmitters().get(4));
		emitter.setPosition(this.x + 6, this.y + 6);
		
		float[] values = new float[3];
		if(isPlayerOwned) {
			values[0] = ColorUtility.playerColor.r;
			values[1] = ColorUtility.playerColor.g;
			values[2] = ColorUtility.playerColor.b;
		} else {
			values[0] = ColorUtility.otherColor.r;
			values[1] = ColorUtility.otherColor.g;
			values[2] = ColorUtility.otherColor.b;
		}
		
		emitter.getTint().setColors(values);
		emitter.start();
				
		//not dead
		this.dead = false;
	}

	@Override
	public void update(float _dt) {
		x += Math.cos(direction) * 2;
		y += Math.sin(direction) * 2;
		
		emitter.setPosition(this.x + 6, this.y + 6);
		emitter.update(_dt);
		
		//destroy ourselves if we have left the area
		if(x < -50 || x > 1280 + 50 || y < -50 || y > 720 + 50) {
			//GridGame.getGame().removeBullet(this);
			die();
		}
	}

	@Override
	public void draw(SpriteBatch _batch) {
		if(isPlayerOwned)
			_batch.setColor(ColorUtility.playerColor);
		else
			_batch.setColor(ColorUtility.otherColor);
		
		emitter.draw(_batch);
		
		_batch.draw(tex, x, y);
		
		_batch.setColor(Color.WHITE);
	}
	
	public boolean getPlayerOwned() {
		return isPlayerOwned;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public void die() {
		this.dead = true;
	}
}
