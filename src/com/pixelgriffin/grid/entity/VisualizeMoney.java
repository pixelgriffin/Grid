package com.pixelgriffin.grid.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pixelgriffin.grid.android.GridGame;

/**
 * 
 * @author Nathan
 *
 */
public class VisualizeMoney implements Entity {

	private int val;
	private float x, y, alpha;
	
	public VisualizeMoney(float _x, float _y, int _val) {
		this.val = _val;
		
		this.x = _x;
		this.y = _y;
		this.alpha = 1f;
	}
	
	@Override
	public void update(float _dt) {
		if(this.alpha <= 0) {
			//delete self
			GridGame.getGame().removeEntity(this);
			return;
		}
		
		this.alpha -= 0.01f;
		
		this.y += (_dt * 30);
	}

	@Override
	public void draw(SpriteBatch _batch) {
		if(this.alpha < 0)
			return;
		
		GridGame.getTinyFont().setColor(Color.RED.r, Color.RED.g, Color.RED.b, alpha);
		
		GridGame.getTinyFont().draw(_batch, "" + val, x - 64, y);
		
		GridGame.getTinyFont().setColor(Color.WHITE);
	}

}
