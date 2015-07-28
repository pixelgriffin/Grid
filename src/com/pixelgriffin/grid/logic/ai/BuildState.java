package com.pixelgriffin.grid.logic.ai;

import com.pixelgriffin.grid.android.GridGame;
import com.pixelgriffin.grid.entity.GeneratorUnit;
import com.pixelgriffin.grid.entity.MinerUnit;
import com.pixelgriffin.grid.entity.ShieldUnit;
import com.pixelgriffin.grid.entity.TurretUnit;
import com.pixelgriffin.grid.entity.Unit;
import com.pixelgriffin.grid.logic.AIOpponent;
import com.pixelgriffin.grid.logic.GameBoard;
import com.pixelgriffin.grid.screen.game.GameScreen;
import com.pixelgriffin.grid.util.NumberUtility;
/**
 * 
 * @author Nathan
 *
 */
public class BuildState extends State {

	private boolean done;
	private float time;
	
	@Override
	public void update(float _dt) {
		this.time += _dt;
		//1.5 - impossible
		//2.0 - hard
		//2.25 - medium
		//2.5 - easy
		if(this.time < (1.0f + GameScreen.AI_DIFFICULTY))//This greatly affects difficulty, where as time between decisions goes down difficulty increases
			return;
		
		AIOpponent ai = (AIOpponent) GridGame.getGame().getOpponent();
		GameBoard board = GridGame.getGame().getBoard();
		
		//immediate attention required if we do not have a generator
		//however, it may be beneficial if we continue
		//building elsewhere so we should not stop here if
		//we do not have the resources.
		if(ai.ourGenerators.isEmpty()) {
			if(GameBoard.otherResource >= GeneratorUnit.VALUE) {
				makeGenerator(ai, board);
				this.done = true;
				return;
			}
		}
		
		//low resources
		if(GameBoard.otherResource < 150) {
			//we are in a state of emergency, we don't have nearly enough miners to continue!
			if(ai.ourMiners.size() < 4) {
				//if we have enough to make some
				if(GameBoard.otherResource >= MinerUnit.VALUE) {
					//try to make some!
					if(makeMinerProtective(ai, board)) {
						this.done = true;
						return;
					}
				}
			}
		} else if(GameBoard.otherResource >= 150) {

			//make miners if we have the resources
			if(ai.ourMiners.size() < ((GameBoard.otherResource / MinerUnit.VALUE) + 4)) {
				if(makeMinerProtective(ai, board)) {
					this.done = true;
					return;
				}
			}
			
			//build shields if we are not well protected
			//build turrets near our units, inside shields if possible
			//or at least prefer shields.
			for(GeneratorUnit gu : ai.ourGenerators) {
				int gx = gu.getCellX();
				int gy = gu.getCellY();
				
				boolean generatorProtected = false;
				
				{
					Unit u;
					
					checkLoop:
					for(int x = gx - 1; x < gx + 1; x++) {
						for(int y = gy - 1; y < gy + 1; y++) {
							if(x == 0 && y == 0)
								continue;
							
							u = board.getUnit(x, y);
							
							if(u != null) {
								if(!u.isPlayerOwned()) {
									if(u instanceof ShieldUnit) {
										System.out.println("Decided shield not necessary @ " + x + ", " + y);
										generatorProtected = true;
										break checkLoop;
									}
								}
							}
						}
					}
				}
				
				//we have no shields around our generator
				if(!generatorProtected) {
					//build a shield
					if(makeShieldGeneratorDefense(gx, gy, ai, board)) {
						this.done = true;
						return;
					}
				}
			}
			
			//This was just a quick hack to test out adding
			//turrets, but it works really well to simulate attacking
			//and strategy, so instead of messing with more
			//states (attack state) I left this in. It does its job,
			//why worry?
			if(!board.isBoardFullForAI()) {
				boolean search = false;
				
				while(!search) {
					int cellX = NumberUtility.nextIntRange(0, 9);
					int cellY = NumberUtility.nextIntRange(GameBoard.hasGameStarted() ? 2 : 4, 5);
					
					if(board.getUnit(cellX, cellY) == null) {
						ai.addUnit(cellX, cellY, new TurretUnit(false));
						search = true;
					} else {
						search = false;
					}
				}
			}
		}
		
		this.done = true;
	}

	private boolean makeShieldGeneratorDefense(int xcenter, int ycenter, AIOpponent ai, GameBoard board) {
		for(int x = xcenter - 1; x < xcenter + 1; x++) {
			for(int y = ycenter - 1; y < ycenter + 1; y++) {
				if(board.getUnit(x, y) == null) {
					ai.addUnit(x, y, new ShieldUnit(false));
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	//FIXME bottom two methods could be one
	private boolean makeMinerProtective(AIOpponent ai, GameBoard board) {
		//try to make some!
		for(int y = 5; y > 3; y--) {
			for(int x = 0; x < 5; x++) {
				if(board.getUnit(x, y) == null) {
					ai.addUnit(x, y, new MinerUnit(false));
					return true;
				}
				
				if(board.getUnit(9 - x, y) == null) {
					ai.addUnit(9 - x, y, new MinerUnit(false));
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean makeGenerator(AIOpponent ai, GameBoard board) {
		//try to make some!
		for(int y = 5; y > 3; y--) {
			for(int x = 0; x < 5; x++) {
				if(board.getUnit(x, y) == null) {
					ai.addUnit(x, y, new GeneratorUnit(false));
					return true;
				}
				
				if(board.getUnit(9 - x, y) == null) {
					ai.addUnit(9 - x, y, new GeneratorUnit(false));
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public boolean finished() {
		return done;
	}

	@Override
	public void reset() {
		done = false;
		time = 0f;
	}
}
