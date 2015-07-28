package com.pixelgriffin.grid.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pixelgriffin.grid.android.GridGame;
import com.pixelgriffin.grid.logic.AIOpponent;
import com.pixelgriffin.grid.logic.GameBoard;

/**
 * 
 * @author Nathan
 *
 */
public class UnitDialogue {
	
	private boolean visible;
	private float x, y;
	private int cellX, cellY;
	
	private Texture tex;
	
	public UnitDialogue() {
		tex = GridGame.getAssetManager().get("select_unit_big.png", Texture.class);
		
		this.visible = false;
		this.x = this.y = 0f;
	}
	
	public void openDialogue(float _x, float _y, int _cellX, int _cellY) {
		this.visible = true;
		this.x = _x;
		this.y = _y;
		
		this.cellX = _cellX;
		this.cellY = _cellY;
	}
	
	public Unit unitSelected(float _x, float _y, GameBoard _b) {
		try {
			if(_y > this.y && _y < this.y + 90) {
				if(_x > this.x && _x < this.x + 90) {
					if(GameBoard.playerResource >= TurretUnit.VALUE) {
						TurretUnit u = new TurretUnit(true);
						
						_b.addUnit(this.cellX, this.cellY, u);
						
						GameBoard.playerResource -= TurretUnit.VALUE;
						
						if(GridGame.getGame().getOpponent() instanceof AIOpponent) {
							((AIOpponent)GridGame.getGame().getOpponent()).enemyTurrets.add(u);
						}
						
						return u;
					}
				}
				
				if(_x > this.x + 90 && _x < this.x + 180) {
					if(GameBoard.playerResource >= MinerUnit.VALUE) {
						MinerUnit u = new MinerUnit(true);
						_b.addUnit(this.cellX, this.cellY, u);
						
						GameBoard.playerResource -= MinerUnit.VALUE;
						
						GridGame.getGame().getOpponent().enemyMiners.add(u);
						
						return u;
					}
				}
				
				if(_x > this.x + 180 && _x < this.x + 270) {
					if(GameBoard.playerResource >= ShieldUnit.VALUE) {
						ShieldUnit u = new ShieldUnit(true);
						_b.addUnit(this.cellX, this.cellY, u);
						
						GameBoard.playerResource -= ShieldUnit.VALUE;
						
						if(GridGame.getGame().getOpponent() instanceof AIOpponent) {
							((AIOpponent)GridGame.getGame().getOpponent()).enemyShields.add(u);
						}
						
						return u;
					}
				}
				
				if(_x > this.x + 270 && _x < this.x + 360) {
					if(GameBoard.playerResource >= GeneratorUnit.VALUE) {
						GeneratorUnit u = new GeneratorUnit(true);
						_b.addUnit(this.cellX, this.cellY, u);
						
						GameBoard.playerResource -= GeneratorUnit.VALUE;
						
						if(GridGame.getGame().getOpponent() instanceof AIOpponent) {
							((AIOpponent)GridGame.getGame().getOpponent()).enemyGenerators.add(u);
						}
						
						return u;
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void closeDialogue() {
		this.visible = false;
	}
	
	public boolean isOpen() {
		return this.visible;
	}
	
	public void draw(SpriteBatch _b) {
		if(!this.visible)
			return;
		
		_b.draw(tex, x, y);
	}
}
