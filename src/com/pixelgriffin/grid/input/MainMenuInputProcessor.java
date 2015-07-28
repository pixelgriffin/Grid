package com.pixelgriffin.grid.input;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector3;
import com.pixelgriffin.grid.android.GridGame;
import com.pixelgriffin.grid.screen.MainMenuScreen;
import com.pixelgriffin.grid.screen.game.GameScreen;
import com.pixelgriffin.grid.util.SettingsUtility;

/**
 * 
 * @author Nathan
 *
 */
public class MainMenuInputProcessor implements InputProcessor {
	
	private MainMenuScreen screen;
	private GridGame inst;
	
	public MainMenuInputProcessor(MainMenuScreen _screen, GridGame _instance) {
		this.screen = _screen;
		this.inst = _instance;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(screen.getIndex() == MainMenuScreen.INDEX_MAIN) {
			if(keycode == Keys.BACK) {
				Gdx.app.exit();
			}
		} else if(screen.getIndex() == MainMenuScreen.INDEX_DIFFICULTY) {
			screen.setIndex(0);
		} else if(screen.getIndex() == MainMenuScreen.INDEX_NETWORK) {
			screen.setIndex(0);
		}
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Vector3 touch = screen.getCamera().unproject(new Vector3(screenX, screenY, 0));
		
		System.out.println("poke: " + touch.x + ", " + touch.y);
		
		if(touch.x > 1280 - 64 && touch.x < 1280) {
			if(touch.y > 0 && touch.y < 64) {
				SettingsUtility.musicOn = !SettingsUtility.musicOn;
				
				Music m = GridGame.getAssetManager().get("music.ogg", Music.class);
				if(!SettingsUtility.musicOn) {
					m.stop();
				} else {
					m.setLooping(true);
					m.play();
				}
			}
		}
		
		if(screen.getIndex() == MainMenuScreen.INDEX_MAIN) {
			if(touch.x > (0) && touch.x < (640 + 325)) {
				if(touch.y > (-50 + 360 + 75) && touch.y < (50 + 360 + 75)) {
					//inst.toGame(false);
					screen.setIndex(1);
				}
			}
			
			if(touch.x > 325 && touch.x < 1280) {
				if(touch.y > (360 - 200 + 75) && touch.y < (360 - 100 + 75)) {
					screen.setIndex(MainMenuScreen.INDEX_NETWORK);
				}
			}
			
			if(touch.x > (0) && touch.x < (640 + 325)) {
				if(touch.y > (-350 + 360 + 75) && touch.y < (-250 + 360 + 75)) {
					inst.toTutorial();
				}
			}
		} else if(screen.getIndex() == MainMenuScreen.INDEX_DIFFICULTY) {
			if(touch.x > (0) && touch.x < (640 + 325)) {
				if(touch.y > (-50 + 360 + 75) && touch.y < (50 + 360 + 75)) {
					inst.toGame(false, GameScreen.AI_EASY);
				}
			}
			
			if(touch.x > 325 && touch.x < 1280) {
				if(touch.y > (360 - 200 + 75) && touch.y < (360 - 100 + 75)) {
					inst.toGame(false, GameScreen.AI_MEDIUM);
				}
			}
			
			if(touch.x > (0) && touch.x < (640 + 325)) {
				if(touch.y > (-350 + 360 + 75) && touch.y < (-250 + 360 + 75)) {
					inst.toGame(false, GameScreen.AI_HARD);
				}
			}
		} else if(screen.getIndex() == MainMenuScreen.INDEX_NETWORK) {
			if(touch.x > (0) && touch.x < (640 + 325)) {
				if(touch.y > (-50 + 360 + 75) && touch.y < (50 + 360 + 75)) {
					//open GPGS multiplayer menu
					inst.getGPGS().startQuickGame();
				}
			}
			
			if(touch.x > 325 && touch.x < 1280) {
				if(touch.y > (360 - 200 + 75) && touch.y < (360 - 100 + 75)) {
					inst.getGPGS().startPickGame();
				}
			}
			
			if(touch.x > (0) && touch.x < (640 + 325)) {
				if(touch.y > (-350 + 360 + 75) && touch.y < (-250 + 360 + 75)) {
					inst.getGPGS().openInvitationScreen();
				}
			}
		}
		
		return false;
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
}
