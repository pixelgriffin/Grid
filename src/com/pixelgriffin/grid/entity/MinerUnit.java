package com.pixelgriffin.grid.entity;

import com.pixelgriffin.grid.android.GridGame;
import com.pixelgriffin.grid.logic.GameBoard;

/**
 * 
 * @author Nathan
 *
 */
public class MinerUnit extends Unit {

	public static final int VALUE = 75;
	
	private float time;
	
	public MinerUnit(boolean _isPlayerPiece) {
		super("unit/miner.png", _isPlayerPiece);
		
		//set health
		this.health = 50;
		
		//set timer
		this.time  = 0f;
		
		//create miner emitter
		this.createEmitter(GridGame.getUnitEffects().getEmitters().get(1));
	}

	@Override
	public void update(float _dt) {
		this.emitter.update(_dt);
		
		time += _dt;
		
		float speed;
		if(this.isPlayerOwned())
			speed = GameBoard.playerSpeedFactor;
		else
			speed = GameBoard.otherSpeedFactor;
		
		if(time >= (5f / speed)) {
			if(isPlayerOwned())
				GameBoard.playerResource += 25;
			else
				GameBoard.otherResource += 25;
			
			time = 0f;
		}
	}

	@Override
	public byte getUnitType() {
		// TODO Auto-generated method stub
		return 1;
	}

}
