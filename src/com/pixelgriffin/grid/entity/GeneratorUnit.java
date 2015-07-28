package com.pixelgriffin.grid.entity;

import com.pixelgriffin.grid.android.GridGame;
import com.pixelgriffin.grid.logic.GameBoard;

/**
 * 
 * @author Nathan
 *
 */
public class GeneratorUnit extends Unit {

	public static final int VALUE = 250;
	
	public GeneratorUnit(boolean _isPlayerPiece) {
		super("unit/generator.png", _isPlayerPiece);
		
		//set health
		this.health = 250;
		
		//create generator emitter
		this.createEmitter(GridGame.getUnitEffects().getEmitters().get(2));
	}

	@Override
	public void update(float _dt) {
		this.emitter.update(_dt);
	}
	
	@Override
	public boolean isDead() {
		if(this.health <= 0) {
			GridGame.getGame().addExplosion(new Explosion(this.realX, this.realY));
			
			//change speed factors since a side just lost a generator
			if(this.isPlayerOwned())
				GameBoard.playerSpeedFactor -= 0.25f;
			else
				GameBoard.otherSpeedFactor -= 0.25f;
			
			return true;
		}
		
		return false;
	}

	@Override
	public byte getUnitType() {
		return 0;
	}
}
