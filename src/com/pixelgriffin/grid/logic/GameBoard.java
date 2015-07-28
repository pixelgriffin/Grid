package com.pixelgriffin.grid.logic;

import java.util.Random;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pixelgriffin.grid.android.GridGame;
import com.pixelgriffin.grid.entity.Bullet;
import com.pixelgriffin.grid.entity.GeneratorUnit;
import com.pixelgriffin.grid.entity.MinerUnit;
import com.pixelgriffin.grid.entity.Unit;
import com.pixelgriffin.grid.screen.game.GameScreen;
import com.pixelgriffin.grid.util.ColorUtility;

/**
 * 
 * @author Nathan
 *
 */
public class GameBoard {
	public static final int BOARD_SIZE_W = 10;
	public static final int BOARD_SIZE_H = 6;
	public static final int CELL_SIZE = 80;//image size 64
	
	//board data (Y, X);
	private Unit[][] board;
	//game start timer
	private float gameTimer;
	private static boolean gameStart;
	
	//player stats
	public static int playerResource, otherResource;
	public static float playerSpeedFactor, otherSpeedFactor;
	
	//board texture
	private Texture gridTex;
	
	//alpha changes
	private float colAlpha, colAlpha2, colAlpha3;
	private boolean colAlphaUp, colAlphaUp2, colAlphaUp3;
	private Random alphaRand;
	
	//constructor
	public GameBoard() {
		//allocate a new 10x10 2D Unit array
		this.board = new Unit[BOARD_SIZE_H][BOARD_SIZE_W];
		
		//stats
		playerResource = otherResource = 100;
		playerSpeedFactor = otherSpeedFactor = 0.75f;
		if(GameScreen.AI_DIFFICULTY == GameScreen.AI_EASY) {
			otherSpeedFactor = 0.75f;
		} else if(GameScreen.AI_DIFFICULTY == GameScreen.AI_MEDIUM) {
			otherSpeedFactor = 0.85f;
		} else if(GameScreen.AI_DIFFICULTY == GameScreen.AI_HARD) {
			otherSpeedFactor = 1f;
		}
		
		//gather grid texture
		gridTex = GridGame.getAssetManager().get("grid2.png", Texture.class);
		
		//set color alpha
		colAlpha = 0.9f;
		colAlpha2 = 0.5f;
		colAlpha3 = 0.6f;
		colAlphaUp = false;//lower colAlpha
		colAlphaUp2 = true;//raise colAlpha2
		colAlphaUp3 = false;//lower colAlpha3
		
		alphaRand = new Random();
		
		//start game timer
		this.gameTimer = 0f;
		gameStart = false;
	}
	
	public boolean isBoardFullForAI() {
		for(int x = 0; x < 10; x++) {
			for(int y = (GameBoard.hasGameStarted() ? 2 : 4); y < 6; y++) {
				if(this.board[y][x] == null)
					return false;
			}
		}
		
		return true;
	}
	
	public void addUnit(int _x, int _y, Unit _unit) {
		//make sure position is within the board
		if(!(_x < BOARD_SIZE_W && _y < BOARD_SIZE_H) || this.board[_y][_x] != null)//TODO possibly split filled or out of bounds into seperate exceptions
			return;
			
		//set position
		_unit.setCellPosition(_x, _y);
		
		//change generator affected values
		if(_unit instanceof GeneratorUnit) {
			if(_unit.isPlayerOwned())
				playerSpeedFactor += 0.25f;
			else
				otherSpeedFactor += 0.25f;
		}
			
		//play construction sound
		Sound snd = GridGame.getAssetManager().get("construct.ogg", Sound.class);
		snd.play();
		
		//set unit in board array
		this.board[_y][_x] = _unit;
	}
	
	public Unit getUnit(int _x, int _y) {
		//position must be inside board
		if(!(_x < BOARD_SIZE_W && _y < BOARD_SIZE_H))
			return null;
		
		return this.board[_y][_x];
	}
	
	public void setGameTime(float _time) {
		this.gameTimer = _time;
	}
	
	public void update(float _dt) {
		if(!gameStart) {
			//update timer
			this.gameTimer += _dt;
			
			if(this.gameTimer >= 30) {
				gameStart = true;
			}
		}
		
		//change alphas
		float nextRand = alphaRand.nextFloat() / 100f;
		
		//our alpha
		if(colAlphaUp) {
			if(colAlpha < 0.9f)
				colAlpha += nextRand;
			else
				colAlphaUp = false;
		} else {
			if(colAlpha > 0.45f)
				colAlpha -= nextRand;
			else
				colAlphaUp = true;
		}
		
		nextRand = alphaRand.nextFloat() / 100f;
		
		//enemy alpha
		if(colAlphaUp2) {
			if(colAlpha2 < 0.9f)
				colAlpha2 += nextRand;
			else
				colAlphaUp2 = false;
		} else {
			if(colAlpha2 > 0.45f)
				colAlpha2 -= nextRand;
			else
				colAlphaUp2 = true;
		}
		
		nextRand = alphaRand.nextFloat() / 100f;
		
		//no-man's alpha
		if(colAlphaUp3) {
			if(colAlpha3 < 0.9f)
				colAlpha3 += nextRand;
			else
				colAlphaUp3 = false;
		} else {
			if(colAlpha2 > 0.45f)
				colAlpha3 -= nextRand;
			else
				colAlphaUp3 = true;
		}
		
		//update pieces
		for(int y = 0; y < BOARD_SIZE_H; y++) {
			for(int x = 0; x < BOARD_SIZE_W; x++) {
				if(this.board[y][x] != null) {
					for(Bullet b : GridGame.getGame().getBullets()) {
						//avoid getting hit twice or something
						if(b.isDead())
							continue;
						
						//check collision
						if(this.board[y][x].isPlayerOwned() != b.getPlayerOwned()) {
							if(this.board[y][x].damageCheck(b.getX(), b.getY())) {
								this.board[y][x].damage();
								b.die();
							}
						}
					}
					
					if(this.board[y][x].isDead()) {
						if(GridGame.getGame().getOpponent() instanceof AIOpponent) {
							((AIOpponent)GridGame.getGame().getOpponent()).postDeadUnit(this.board[y][x]);
						} else {
							if(this.board[y][x] instanceof MinerUnit) {
								if(this.board[y][x].isPlayerOwned()) {
									GridGame.getGame().getOpponent().enemyMiners.remove(this.board[y][x]);
								} else {
									GridGame.getGame().getOpponent().ourMiners.remove(this.board[y][x]);
								}
							}
						}
						
						this.board[y][x] = null;
						continue;
					}
					
					this.board[y][x].update(_dt);
				}
			}
		}
	}
	
	public void render(SpriteBatch _batch) {
		//draw boards
		_batch.setColor(ColorUtility.playerColor.r, ColorUtility.playerColor.g, ColorUtility.playerColor.b, colAlpha);
		_batch.draw(gridTex, 240, 120);
		_batch.setColor(ColorUtility.otherColor.r, ColorUtility.otherColor.g, ColorUtility.otherColor.b, colAlpha2);
		_batch.draw(gridTex, 240, 600 - 160);
		_batch.setColor(1f, 1f, 1f, colAlpha3);
		if(gameStart) {
			_batch.draw(gridTex, 240, 280);
		}
		_batch.setColor(Color.WHITE);
		
		//draw pieces
		for(int y = 0; y < BOARD_SIZE_H; y++) {
			for(int x = 0; x < BOARD_SIZE_W; x++) {
				if(this.board[y][x] != null)
					this.board[y][x].draw(_batch);
			}
		}
	}
	
	public void drawCountdown(SpriteBatch _batch) {
		//draw countdown
		if(this.gameTimer >= 25 && this.gameTimer < 26) {
			GridGame.getTitleFont().draw(_batch, "5", 640 - 12, 360);
		} else if(this.gameTimer >= 26 && this.gameTimer < 27) {
			GridGame.getTitleFont().draw(_batch, "4", 640 - 12, 360);
		} else if(this.gameTimer >= 27 && this.gameTimer < 28) {
			GridGame.getTitleFont().draw(_batch, "3", 640 - 12, 360);
		} else if(this.gameTimer >= 28 && this.gameTimer < 29) {
			GridGame.getTitleFont().draw(_batch, "2", 640 - 12, 360);
		} else if(this.gameTimer >= 29 && this.gameTimer < 30) {
			GridGame.getTitleFont().draw(_batch, "1", 640 - 12, 360);
		}
	}
	
	public static boolean hasGameStarted() {
			return gameStart;
	}
}
