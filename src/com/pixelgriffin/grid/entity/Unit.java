package com.pixelgriffin.grid.entity;

import java.nio.ByteBuffer;

import box2dLight.PointLight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pixelgriffin.grid.android.GridGame;
import com.pixelgriffin.grid.logic.GameBoard;
import com.pixelgriffin.grid.logic.NetworkOpponent;
import com.pixelgriffin.grid.util.ColorUtility;
import com.pixelgriffin.grid.util.NetworkUtility;
import com.pixelgriffin.grid.util.NumberUtility;

/**
 * Base Unit class
 * 
 * @author Nathan
 *
 */
public abstract class Unit implements Entity {
	
	//LibGDX texture reference
	private Texture tex;
	
	protected int xCell, yCell;
	protected int realX, realY;
	protected int health;
	
	protected ParticleEmitter emitter;
	protected PointLight light;
	
	private boolean playerPiece, deathFlagged;
	
	public Unit(String _texName, boolean _playerPiece) {
		//reference loaded Texture from file name in asset manager
		//LibGDX has a pre-defined Texture loader
		//so we don't need to define one
		this.tex = GridGame.getAssetManager().get(_texName, Texture.class);
		
		//set location to 0 when first created
		this.xCell = this.yCell = 0;
		
		//set health to default 100
		this.health = 100;
		
		//set is player's piece
		playerPiece = _playerPiece;
		
		this.deathFlagged = false;
		
	}
	
	private PointLight createLight() {
		Color thisColor = ColorUtility.playerColor;
		if(!this.playerPiece)
			thisColor = ColorUtility.otherColor;
		
		thisColor.a = 0.85f;
		
		PointLight lt = GridGame.getGame().addPointLight(thisColor, 120f, this.realX, this.realY);
		lt.setStaticLight(true);
		
		return lt;
	}
	
	public Texture getTexture() {
		return this.tex;
	}
	
	public void flagForDeath() {
		this.deathFlagged = true;
		this.health = 0;
	}
	
	public int getHealth() {
		return this.health;
	}
	
	public void setPlayerOwnership(boolean _player) {
		playerPiece = _player;
	}
	
	public boolean isPlayerOwned() {
		return playerPiece;
	}
	
	public int getCellX() {
		return this.xCell;
	}
	
	public int getCellY() {
		return this.yCell;
	}
	
	public float getRealX() {
		return this.realX;
	}
	
	public float getRealY() {
		return this.realY;
	}
	
	public void setCellPosition(int _x, int _y) {
		this.xCell = _x;
		this.yCell = _y;
		
		this.realX = this.xCell + 240 + (GameBoard.CELL_SIZE / 2) - 4;
		this.realY = this.yCell + 120 + (GameBoard.CELL_SIZE / 2) - 4;
		this.realX += this.xCell * GameBoard.CELL_SIZE;
		this.realY += this.yCell * GameBoard.CELL_SIZE;
		
		if(this.light != null)
			this.light.setPosition(this.realX - 2, this.realY - 2);
		else
			this.light = createLight();
		
		if(this.emitter != null) {
			this.emitter.setPosition(this.realX, this.realY);
		}
	}
	
	/**
	 * Check to see if point is in damage circle
	 * 
	 * @param _x world-x of object
	 * @param _y world-y of object 
	 */
	public boolean damageCheck(float _x, float _y) {
		//gather world-location
		
		float xdiff = _x - realX;
		float ydiff = _y - realY;
		
		//radius^2 = 100
		return (((xdiff * xdiff) + (ydiff * ydiff)) < 900);
	}
	
	@Override
	public void draw(SpriteBatch _batch) {
		//set our color based on affiliation
		if(isPlayerOwned())
			_batch.setColor(ColorUtility.playerColor);
		else
			_batch.setColor(ColorUtility.otherColor);
		
		//draw texture at the cell location
		//multiply the cell location by the cell size in order to draw
		//at world coordinates rather than cell coordinates
		_batch.draw(this.tex, 8 + 240 + (this.xCell * GameBoard.CELL_SIZE), 8 + 120 + (this.yCell * GameBoard.CELL_SIZE));
		
		_batch.setColor(Color.WHITE);
		
		if(this.emitter != null) {
			this.emitter.draw(_batch);
		}
	}
	
	public void damage() {
		if(this.health > 0) {
			this.health -= 25;
		}
		
		//no opponent means we are a networked game
		/*if(GridGame.getGame().getOpponent() instanceof NetworkOpponent) {
			if(!this.isPlayerOwned())
				return;
			
			byte[] data = new byte[13];
			
			data[0] = NetworkUtility.PACKET_HEALTH;
			
			byte[] bint = ByteBuffer.allocate(4).putInt(this.getCellX()).array();
			NumberUtility.putBytesInArray(1, data, bint);
			bint = ByteBuffer.allocate(4).putInt(this.getCellY()).array();
			NumberUtility.putBytesInArray(5, data, bint);
			bint = ByteBuffer.allocate(4).putInt(this.health).array();
			NumberUtility.putBytesInArray(9, data, bint);
			
			NetworkUtility.sendMessage(data, GridGame.getInstance());
		}*/
	}
	
	public boolean isDead() {
		//if our health is 0
		if(this.health <= 0) {
			//are we networked?
			if(GridGame.getGame().getOpponent() instanceof NetworkOpponent) {
				//we can only destroy our own shit
				if(this.isPlayerOwned()) {
					//since we are networked, we should inform the other player
					//that one of our units was just destroyed
					byte[] data = new byte[9];
					byte[] bint;
					
					data[0] = NetworkUtility.PACKET_DESTROY;
					
					bint = ByteBuffer.allocate(4).putInt(this.getCellX()).array();
					NumberUtility.putBytesInArray(1, data, bint);
					
					bint = ByteBuffer.allocate(4).putInt(this.getCellY()).array();
					NumberUtility.putBytesInArray(5, data, bint);
					
					NetworkUtility.sendMessageReliable(data, GridGame.getInstance());
				} else {
					//since this is not our unit health will not determine death
					//network packets will, if a network packet has flagged this to die
					//then we return true
					//but until then we return false
					//so we don't blow ourselves up
					
					//boom boom if dead
					if(deathFlagged) {
						GridGame.getGame().addExplosion(new Explosion(this.realX, this.realY));
					}
					
					//die
					return deathFlagged;
				}
			}
			//end networking
			
			//boom boom
			GridGame.getGame().addExplosion(new Explosion(this.realX, this.realY));
			
			//remove light
			GridGame.getGame().removePointLight(this.light);
			
			return true;//mark for removal
		}
		
		return false;
	}
	
	protected void createEmitter(ParticleEmitter _pe) {
		//create emitter
		float[] values = new float[3];
		if(isPlayerOwned()) {
			values[0] = ColorUtility.playerColor.r;
			values[1] = ColorUtility.playerColor.g;
			values[2] = ColorUtility.playerColor.b;
		} else {
			values[0] = ColorUtility.otherColor.r;
			values[1] = ColorUtility.otherColor.g;
			values[2] = ColorUtility.otherColor.b;
		}
		
		this.emitter = new ParticleEmitter(_pe);
		this.emitter.getTint().setColors(values);
		this.emitter.start();
	}
	
	public abstract byte getUnitType();
}
