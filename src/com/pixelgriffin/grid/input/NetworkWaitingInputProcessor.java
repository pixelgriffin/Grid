package com.pixelgriffin.grid.input;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.google.android.gms.games.Games;
import com.pixelgriffin.grid.android.GridGame;
import com.pixelgriffin.grid.screen.MainMenuScreen;

/**
 * 
 * @author Nathan
 *
 */
public class NetworkWaitingInputProcessor implements InputProcessor {
	
	private GridGame inst;
	
	public NetworkWaitingInputProcessor(GridGame _instance) {
		this.inst = _instance;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Keys.BACK) {
			//exit network room & return to menu
			
			//if we are in a room we need to leave it
			//if we are not don't, we'll get an NPE
			if(inst.getGPGS().getActiveRoomID() != null)
				Games.RealTimeMultiplayer.leave(inst.getGPGS().getAPI(), inst.getGPGS().getInstance(), inst.getGPGS().getActiveRoomID());
			
			inst.toMenu(MainMenuScreen.INDEX_NETWORK, false);
		}
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
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
