package com.pixelgriffin.grid.entity;

import box2dLight.PointLight;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pixelgriffin.grid.android.GridGame;

/**
 * Explosion visualization object
 * Removes itself after emitter's life cycle is done
 * 
 * @author Nathan
 *
 */
public class Explosion {
	//emitter
	private ParticleEmitter emitter;
	//sound
	private Sound boom;
	
	//light
	private PointLight light;
	
	//constructor
	public Explosion(int _x, int _y) {
		//gather sound from asset manager
		this.boom = GridGame.getAssetManager().get("explosion.ogg", Sound.class);
		
		//gather emitter from loaded emitters
		emitter = new ParticleEmitter(GridGame.getUnitEffects().getEmitters().get(3));
		emitter.setPosition(_x + 6, _y + 6);//set position of emitter
		
		emitter.start();//start emitting particles
		
		//add shake to camera
		GridGame.getGame().getCamera().addShakeMagnitude(5);
		
		this.light = GridGame.getGame().addPointLight(Color.ORANGE, 256f, _x, _y);
		
		//play sound
		boom.play();
	}
	
	public void update(float _dt) {
		//if the emitter's done emitting
		if(emitter.isComplete()) {
			//remove ourselves
			GridGame.getGame().removePointLight(this.light);
			GridGame.getGame().removeExplosion(this);
			return;//stop updating
		}
			
		//continue emitting particles
		emitter.update(_dt);
		
		if( this.light.getColor().a > 0) {
			Color c = this.light.getColor();
			c.a -= _dt;
			this.light.setColor(c);
		}
		
		if(this.light.getColor().a < 0) {
			Color c = this.light.getColor();
			c.a = 0;
			this.light.setColor(c);
		}
	}
	
	public void draw(SpriteBatch _b) {
		//do not draw if we are done
		if(emitter.isComplete())
			return;
		
		emitter.draw(_b);
	}
}
