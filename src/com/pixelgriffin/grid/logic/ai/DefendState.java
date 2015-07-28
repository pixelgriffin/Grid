package com.pixelgriffin.grid.logic.ai;

import com.pixelgriffin.grid.android.GridGame;
import com.pixelgriffin.grid.entity.TurretUnit;
import com.pixelgriffin.grid.logic.AIOpponent;
import com.pixelgriffin.grid.logic.GameBoard;
import com.pixelgriffin.grid.screen.game.GameScreen;

/**
 * 
 * @author Nathan
 *
 */
public class DefendState extends State {
	
	private float time;
	private boolean done;
	
	@Override
	public void update(float _dt) {
		this.time += _dt;
		if(this.time < GameScreen.AI_DIFFICULTY)
			return;
		
		AIOpponent ai = (AIOpponent) GridGame.getGame().getOpponent();
		
		//TODO needed?
		//if(ai.focusX == -1 || ai.focusY == -1)
		//	return;
		
		System.out.println("Defending...");
		
		done = buildDefenseTurret(ai);
	}

	@Override
	public boolean finished() {
		return done;
	}

	@Override
	public void reset() {
		time = 0f;
		done = false;
	}
	
	private boolean buildDefenseTurret(AIOpponent _ai) {
		if(GameBoard.otherResource >= TurretUnit.VALUE) {
			System.out.println("have resources");
			System.out.println("focus @ " + _ai.focusX + ", " + _ai.focusY);
			for(int x = (_ai.focusX - 1); x <= (_ai.focusX + 1); x++) {
				for(int y = (_ai.focusY + 1); y >= (_ai.focusY - 1); y--) {
					if(x == 0 || y == 0)
						continue;
					
					try {
						_ai.addUnit(x, y, new TurretUnit(false));
						System.out.println("Built defense turret");
						return true;
					} catch (Exception e) {
						//e.printStackTrace();
					}
				}
			}
		}
		
		return false;
	}
}
