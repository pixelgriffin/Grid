package com.pixelgriffin.grid.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Entity interface
 * 
 * @author Nathan
 *
 */
public interface Entity {
	/**
	 * Update entity information
	 * Should be called before draw
	 * 
	 * @param _dt
	 */
	public void update(float _dt);
	/**
	 * Draws an entity
	 * 
	 * @param _batch
	 */
	public void draw(SpriteBatch _batch);
}
