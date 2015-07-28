package com.pixelgriffin.grid.entity;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.pixelgriffin.grid.android.GridGame;
import com.pixelgriffin.grid.util.ColorUtility;

/**
 * 
 * @author Nathan
 *
 */
public class ShieldUnit extends Unit {

	public static final int VALUE = 100;
	
	public ShieldUnit(boolean _isPlayerPiece) {
		super("unit/shield.png", _isPlayerPiece);
		
		//set health
		this.health = 300;
		
		//create sheild emitter
		this.createEmitter(GridGame.getUnitEffects().getEmitters().get(0));
	}

	@Override
	public void update(float _dt) {
		this.emitter.update(_dt);
	}
	
	@Override
	public boolean damageCheck(float _x, float _y) {
		float xdiff = _x - realX;
		float ydiff = _y - realY;
				
		return (((xdiff * xdiff) + (ydiff * ydiff)) < 14400);
	}

	@Override
	public byte getUnitType() {
		// TODO Auto-generated method stub
		return 2;
	}
}
