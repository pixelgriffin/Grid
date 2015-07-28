package com.pixelgriffin.grid.logic.ai;

import com.pixelgriffin.grid.android.GridGame;
import com.pixelgriffin.grid.entity.GeneratorUnit;
import com.pixelgriffin.grid.entity.MinerUnit;
import com.pixelgriffin.grid.entity.TurretUnit;
import com.pixelgriffin.grid.entity.Unit;
import com.pixelgriffin.grid.logic.AIOpponent;

/**
 * 
 * @author Nathan
 *
 */
public class DecisionIsBeingAttacked extends DecisionNode {

	@Override
	public boolean isSaciated() {
		AIOpponent ai = (AIOpponent) GridGame.getGame().getOpponent();
		
		//start by checking for generators, they are the most valuable
		Unit target;
		for(TurretUnit tu : ai.enemyTurrets) {
			target = tu.getTarget();
			
			if(target != null) {
				if(target instanceof GeneratorUnit) {
					ai.focusX = tu.getCellX();
					ai.focusY = tu.getCellY();
					return true;
				}
			}
		}
		
		//next check if our miners are under attack
		for(TurretUnit tu : ai.enemyTurrets) {
			target = tu.getTarget();
			
			if(target != null) {
				if(target instanceof MinerUnit) {
					ai.focusX = tu.getCellX();
					ai.focusY = tu.getCellY();
					
					return true;
				}
			}
		}
		
		//we aren't being attacked
		//so there should be no focus point
		ai.focusX = -1;
		ai.focusY = -1;
		
		return false;
	}

}
