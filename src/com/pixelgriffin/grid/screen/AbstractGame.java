package com.pixelgriffin.grid.screen;

import java.util.ArrayList;
import java.util.HashSet;

import android.renderscript.Matrix4f;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.pixelgriffin.grid.android.GridGame;
import com.pixelgriffin.grid.entity.Bullet;
import com.pixelgriffin.grid.entity.Entity;
import com.pixelgriffin.grid.entity.Explosion;
import com.pixelgriffin.grid.entity.Unit;
import com.pixelgriffin.grid.logic.AIOpponent;
import com.pixelgriffin.grid.logic.GameBoard;
import com.pixelgriffin.grid.logic.Opponent;
import com.pixelgriffin.grid.logic.ShakeCamera;
import com.pixelgriffin.grid.util.ColorUtility;

/**
 * 
 * @author Nathan
 *
 */
public abstract class AbstractGame implements Screen {
	
	//lighting
	private World box2dWorld;
	private RayHandler lightRays;
	private ArrayList<Light> lights;
	
	//UI display
	protected Unit displayUnit;
	
	//bullets
	private HashSet<Bullet> bullets;
	private HashSet<Bullet> removeBullets;
	
	public AbstractGame() {
		//lighting
		lights = new ArrayList<Light>();
		
		box2dWorld = new World(new Vector2(0, 0), true);
		
		lightRays = new RayHandler(box2dWorld);
		lightRays.setAmbientLight(0.0f, 0.0f, 0.0f, 0.25f);
		lightRays.setBlurNum(3);
		
		//bullets
		bullets = new HashSet<Bullet>();
		removeBullets = new HashSet<Bullet>();
		
		//ads
		GridGame.getInstance().getGPGS().getInstance().loadAd();
	}
	
	
	public abstract ShakeCamera getCamera();	
	public abstract GameBoard getBoard();
	
	public HashSet<Bullet> getBullets() {
		return this.bullets;
	}
	
	public void addBullet(Bullet _b) {
		this.bullets.add(_b);
	}
	
	public abstract void addExplosion(Explosion _e);
	
	public abstract void removeExplosion(Explosion _e);
	
	public abstract void addEntity(Entity _e);
	
	public abstract void removeEntity(Entity _e);
	
	public abstract Opponent getOpponent();
	
	public void displayUnitInfo(Unit _u) {
		this.displayUnit = _u;
	}
	
	protected void renderLights(Matrix4 _combined, float _dt) {
		box2dWorld.step(_dt, 1, 1);
		
		lightRays.setCombinedMatrix(_combined);
		lightRays.update();
		
		lightRays.render();
	}
	
	public PointLight addPointLight(Color _color, float _dist, float _x, float _y) {
		PointLight pl = new PointLight(this.lightRays, 30, _color, _dist, _x, _y);
		this.lights.add(pl);
		
		return pl;
	}
	
	public void removePointLight(Light _light) {
		this.lights.remove(_light);
		_light.remove();
	}
	
	protected void updateBullets(float _dt) {
		//update bullets
		for(Bullet b : removeBullets) {
			bullets.remove(b);
		}
		removeBullets.clear();
		
		for(Bullet b : bullets) {
			if(!b.isDead())
				b.update(_dt);
			else
				removeBullets.add(b);
		}
	}
	
	protected void drawBullets(SpriteBatch _batch) {
		for(Bullet b : bullets) {
			b.draw(_batch);
		}
	}
	
	protected void drawUnitInfo(SpriteBatch _b) {
		if(this.displayUnit == null)
			return;
		
		Texture tex = this.displayUnit.getTexture();
		if(this.displayUnit.isPlayerOwned()) {
			_b.setColor(ColorUtility.playerColor);
			GridGame.getDefaultFont().setColor(ColorUtility.playerColor);
		} else {
			_b.setColor(ColorUtility.otherColor);
			GridGame.getDefaultFont().setColor(ColorUtility.otherColor);
		}
		
		_b.draw(tex, 470, 0);
		GridGame.getDefaultFont().draw(_b, "health . " + this.displayUnit.getHealth(), 590, 40);
		
		GridGame.getDefaultFont().draw(_b, this.displayUnit.getClass().getSimpleName().replace("Unit", ""), 620, 720);
		
		GridGame.getDefaultFont().setColor(Color.WHITE);
		_b.setColor(Color.WHITE);
	}
}
