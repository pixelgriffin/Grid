package com.pixelgriffin.grid.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * 
 * @author Nathan
 *
 */
public class ShakeCamera extends OrthographicCamera {
	
	private float offsetX, offsetY;
	private float SHAKE_X, SHAKE_Y;
	
	private float time = 0;
	
	public ShakeCamera() {
		super();
	}
	
	public ShakeCamera(float _w, float _h) {
		super(_w, _h);
		
		
		offsetX = offsetY = 0;
	}
	
	public void addShakeMagnitude(float _mag) {
		SHAKE_X += _mag;
		SHAKE_Y += _mag / 2;
		
		if(SHAKE_X > 5)
			SHAKE_X = 5;
		if(SHAKE_Y > 2)
			SHAKE_Y = 2;
	}
	
	//use offset
	@Override
	public void update() {
		super.update();
		
		time += 0.25f;
		
		this.position.set(640f + (float)Math.sin(time) * SHAKE_X, 360f + (float)Math.sin(time) * SHAKE_Y, 0f);
		
		SHAKE_X = lerp(SHAKE_X, 0, Gdx.graphics.getDeltaTime() * 2);
		SHAKE_Y = lerp(SHAKE_Y, 0, Gdx.graphics.getDeltaTime() * 2);
	}
	
	private float lerp(float start, float finish, float time) {
		return start + time * (finish - start);
	}
}
