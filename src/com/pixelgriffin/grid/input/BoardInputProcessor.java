package com.pixelgriffin.grid.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.pixelgriffin.grid.android.GridGame;
import com.pixelgriffin.grid.entity.GeneratorUnit;
import com.pixelgriffin.grid.entity.MinerUnit;
import com.pixelgriffin.grid.entity.ShieldUnit;
import com.pixelgriffin.grid.entity.TurretUnit;
import com.pixelgriffin.grid.entity.Unit;
import com.pixelgriffin.grid.entity.UnitDialogue;
import com.pixelgriffin.grid.entity.VisualizeMoney;
import com.pixelgriffin.grid.logic.GameBoard;
import com.pixelgriffin.grid.screen.MainMenuScreen;
import com.pixelgriffin.grid.screen.game.GameScreen;

/**
 * 
 * @author Nathan
 *
 */
public class BoardInputProcessor implements InputProcessor {

	private GameScreen game;
	
	private UnitDialogue unitDialogue;
	
	private Sound blip;
	
	public BoardInputProcessor(GameScreen _game) {
		this.game = _game;
		
		this.unitDialogue = new UnitDialogue();
		
		this.blip = GridGame.getAssetManager().get("blip.ogg", Sound.class);
	}
	
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		//concede defeat
		if(keycode == Keys.BACK) {
			//game.instance.toMenu(MainMenuScreen.INDEX_DIFFICULTY);
			game.instance.toFinish("LOST", false);
		}
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		//convert screen-space coordinates to world-space
		Vector3 touch = this.game.getCamera().unproject(new Vector3(screenX, screenY, 0));
		
		GridGame.getGame().displayUnitInfo(null);
		
		//touch is inside grid bounds
		if(touch.x > 240 && touch.x < 1040) {
			if(touch.y > 120 && touch.y < Gdx.graphics.getHeight() - 120) {
				//then handle the touch
				//start selecting unit
				
				//convert world-space to cell-space
				int cellX = (int)touch.x - 240;
				int cellY = (int)touch.y - 120;
				
				//divide to cell sizes
				cellX = cellX / GameBoard.CELL_SIZE;
				cellY = cellY / GameBoard.CELL_SIZE;
				
				//if there is already a unit in this position
				//we do not want to open the unit dialogue
				if(!unitDialogue.isOpen()) {
					try {
						GameBoard board = GridGame.getGame().getBoard();
						
						//if there IS  a unit
						Unit u = board.getUnit(cellX, cellY);
						if(u != null) {
							//display unit info
							GridGame.getGame().displayUnitInfo(u);
							
							//stop executing
							return true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				//stop no man's land building until game has started
				int topCell = 1;
				if(GameBoard.hasGameStarted())
					topCell = 3;
				
				//do not allow any pieces to be placed on the opponent's side
				//or the center of the board "no man's land"
				if(cellY <= topCell) {
					//if we are not currently in the unit selection
					//dialogue box
					if(!unitDialogue.isOpen()) {
						this.blip.play();
						//let the system know we're selecting
						unitDialogue.openDialogue(touch.x, touch.y, cellX, cellY);
						return true;
					}
				}
			}
		}
		
		Unit u = unitDialogue.unitSelected(touch.x, touch.y, game.getBoard());
		if(u != null) {
			int val = 0;
			
			System.out.println("swag");
			
			if(u instanceof TurretUnit) {
				val = TurretUnit.VALUE;
			} else if(u instanceof MinerUnit) {
				val = MinerUnit.VALUE;
			} else if(u instanceof ShieldUnit) {
				val = ShieldUnit.VALUE;
			} else if(u instanceof GeneratorUnit) {
				val = GeneratorUnit.VALUE;
			}
			
			this.game.addEntity(new VisualizeMoney(u.getRealX() + 40, u.getRealY() + 40, val));
		}
		
		unitDialogue.closeDialogue();
		
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
	
	public void draw(SpriteBatch _b) {
		unitDialogue.draw(_b);
	}
}
