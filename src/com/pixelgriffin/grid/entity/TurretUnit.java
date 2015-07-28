package com.pixelgriffin.grid.entity;

import com.badlogic.gdx.audio.Sound;
import com.pixelgriffin.grid.android.GridGame;
import com.pixelgriffin.grid.logic.AIOpponent;
import com.pixelgriffin.grid.logic.GameBoard;
import com.pixelgriffin.grid.util.NumberUtility;

/**
 * 
 * @author Nathan
 *
 */
public class TurretUnit extends Unit {

	public static final int VALUE = 50;
	
	private float time;
	
	private int radius;
	private Unit target;
	
	private GameBoard board;
	
	public TurretUnit(boolean _isPlayerPiece) {
		super("unit/turret.png", _isPlayerPiece);
		
		//since the turret needs to be aware
		//of other pieces on the board, we must
		//have a reference to the board
		this.board = GridGame.getGame().getBoard();
		
		//set health
		this.health = 125;
		
		//set check radius
		this.radius = 2;
		
		//set target to none
		this.target = null;
		
		//set timer to 0
		this.time = 0;
	}

	public Unit getTarget() {
		return target;
	}
	
	@Override
	public void update(float _dt) {
		this.time += _dt;
		
		float speed;
		if(this.isPlayerOwned())
			speed = GameBoard.playerSpeedFactor;
		else
			speed = GameBoard.otherSpeedFactor;
		
		if(this.time < (4f / speed))
			return;
		
		//check "circularly" around the turret
		if(GridGame.getGame().getOpponent() instanceof AIOpponent || this.isPlayerOwned()) {
			//non-networking
			
			targetloop:
			for(int x = -radius; x <= radius; x++) {
				for(int y = -radius; y <= radius; y++) {
					if(x == 0 && y == 0)
						continue;
					
					try {
						Unit u = board.getUnit(this.xCell + x, this.yCell + y);
						
						//if the unit exists and we're not on the same teams
						if(u != null) {
							if(u.isPlayerOwned() != this.isPlayerOwned()) {
								this.target = u;
								break targetloop;
							}
						} else {
							this.target = null;
						}
					} catch (Exception e) {
					}
				}
			};
		} else {
			//networking & other player's unit
			//must be reversed because when the other
			//unit is placed it is placed in the reverse
			//position, so it must check in a reverse
			//order to maintain synchronization to some extent
			targetloop:
				for(int x = radius; x >= -radius; x--) {
					for(int y = radius; y >= -radius; y--) {
						if(x == 0 && y == 0)
							continue;
						
						try {
							Unit u = board.getUnit(this.xCell + x, this.yCell + y);
							
							//if the unit exists and we're not on the same teams
							if(u != null) {
								if(u.isPlayerOwned() != this.isPlayerOwned()) {
									this.target = u;
									break targetloop;
								}
							} else {
								this.target = null;
							}
						} catch (Exception e) {
						}
					}
				};
		}

		//fire
		if(target != null) {
			//shoot bullet
			double angle = NumberUtility.getAngle(realX, realY, target.realX, target.realY);
			angle += NumberUtility.nextIntRange(-2, 2);
			
			Bullet bullet = new Bullet(this.isPlayerOwned(), realX, realY, angle);
			
			GridGame.getGame().addBullet(bullet);
			
			//play sound
			Sound snd = GridGame.getAssetManager().get("pew.ogg", Sound.class);
			long id = snd.play();
			snd.setVolume(id, 0.5f);
		}
		
		//reset timer
		this.time = 0f;
	}

	@Override
	public byte getUnitType() {
		// TODO Auto-generated method stub
		return 3;
	}

}
