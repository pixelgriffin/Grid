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
import com.pixelgriffin.grid.logic.GameBoard;
import com.pixelgriffin.grid.screen.MainMenuScreen;
import com.pixelgriffin.grid.screen.game.GameScreen;
import com.pixelgriffin.grid.screen.game.TutorialScreen;

/**
 * 
 * @author Nathan
 *
 */
public class TutorialInputProcessor implements InputProcessor {

	private TutorialScreen game;
	
	private UnitDialogue unitDialogue;
	
	private Sound blip;
	
	public TutorialInputProcessor(TutorialScreen _game) {
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
			game.instance.toMenu(MainMenuScreen.INDEX_MAIN, false);
		}
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(game.messageIndex == 22) {
			game.instance.toMenu(MainMenuScreen.INDEX_MAIN, false);
		}
		
		if(game.messageIndex == 0 || game.messageIndex == 2 || game.messageIndex == 3
			|| game.messageIndex == 5 || game.messageIndex == 6 || game.messageIndex == 7
			|| game.messageIndex == 8 || game.messageIndex == 10 || game.messageIndex == 11
			|| game.messageIndex == 12 || game.messageIndex == 13 || game.messageIndex == 15
			|| game.messageIndex == 16 || game.messageIndex > 17) {
			game.messageIndex++;
			return true;
		}
		
		//convert screen-space coordinates to world-space
		Vector3 touch = this.game.getCamera().unproject(new Vector3(screenX, screenY, 0));
		
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
						//if there is already a unit in this position
						//we do not want to open the unit dialogue
						try {
							GameBoard board = GridGame.getGame().getBoard();
							
							//if there IS  a unit
							if(board.getUnit(cellX, cellY) != null) {
								//stop executing
								return true;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						this.blip.play();
						//let the system know we're selecting
						unitDialogue.openDialogue(touch.x, touch.y, cellX, cellY);
						
						if(game.messageIndex == 1)
							game.messageIndex++;
						
						return true;
					}
				}
			}
		}
		
		Unit u = unitDialogue.unitSelected(touch.x, touch.y, game.getBoard());
		unitDialogue.closeDialogue();
		
		if(u instanceof MinerUnit) {
			if(game.messageIndex == 4) {
				game.messageIndex++;
				return true;
			}
		} else if(u instanceof ShieldUnit) {
			if(game.messageIndex == 9) {
				game.messageIndex++;
				return true;
			}
		} else if(u instanceof TurretUnit) {
			if(game.messageIndex == 14) {
				game.messageIndex++;
				
				try {
					game.getBoard().addUnit(u.getCellX(), u.getCellY() + 1, new TurretUnit(false));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return true;
			}
		} else if(u instanceof GeneratorUnit) {
			if(game.messageIndex == 17) {
				game.messageIndex++;
				return true;
			}
		}
		
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
