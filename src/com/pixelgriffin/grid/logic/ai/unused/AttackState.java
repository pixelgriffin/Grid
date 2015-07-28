package com.pixelgriffin.grid.logic.ai.unused;

import com.pixelgriffin.grid.android.GridGame;
import com.pixelgriffin.grid.entity.TurretUnit;
import com.pixelgriffin.grid.logic.AIOpponent;
import com.pixelgriffin.grid.logic.GameBoard;
import com.pixelgriffin.grid.logic.ai.State;
import com.pixelgriffin.grid.util.NumberUtility;

/**
 * 
 * @author Nathan
 *
 */
public class AttackState extends State {

	private float time;
	private boolean done;
	
	@Override
	public void update(float _dt) {
		this.time += _dt;
		//1.5 - impossible
		//2.0 - hard
		//2.25 - medium
		//2.65 - easy
		if(this.time < 2.25f)//This greatly affects difficulty, where as time between decisions goes down diffuclty increases
			return;
		
		AIOpponent ai = (AIOpponent) GridGame.getGame().getOpponent();
		GameBoard board = GridGame.getGame().getBoard();
		
		//FIXME temporary
		//just build turrets idk
		if(!board.isBoardFullForAI()) {
			boolean nigga = false;
			
			while(!nigga) {
				try {
					int cellX = NumberUtility.nextIntRange(0, 9);
					int cellY = NumberUtility.nextIntRange(GameBoard.hasGameStarted() ? 2 : 4, 5);
					
					if(board.getUnit(cellX, cellY) == null) {
						ai.addUnit(cellX, cellY, new TurretUnit(false));
						nigga = true;
					} else {
						nigga = false;
					}
				} catch(Exception e) {
					nigga = false;
				}
			}
		}
		
		this.done = true;
	}

	@Override
	public boolean finished() {
		return this.done;
	}

	@Override
	public void reset() {
		this.time = 0f;
		this.done = false;
	}

}
