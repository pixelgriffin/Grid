package com.pixelgriffin.grid.logic.ai.unused;

import com.pixelgriffin.grid.android.GridGame;
import com.pixelgriffin.grid.entity.MinerUnit;
import com.pixelgriffin.grid.logic.AIOpponent;
import com.pixelgriffin.grid.logic.GameBoard;
import com.pixelgriffin.grid.logic.ai.DecisionNode;

/**
 * 
 * @author Nathan
 *
 */
public class DecisionIsProtected extends DecisionNode {

	@Override
	public boolean isSaciated() {
		//if the game has not yet started we are naturally safe
		if(!GameBoard.hasGameStarted())
			return true;
		
		//the game has started, check the board
		AIOpponent ai = (AIOpponent) GridGame.getGame().getOpponent();
		
		if(ai.ourMiners.size() >= (200 - (GameBoard.otherResource / MinerUnit.VALUE))) {
			if(ai.ourTurrets.size() >= 2) {
				return true;
			}
		}
		
		return false;
	}
	
}
