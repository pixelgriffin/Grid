package com.pixelgriffin.grid.screen.game;

import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pixelgriffin.grid.android.GridGame;
import com.pixelgriffin.grid.entity.Bullet;
import com.pixelgriffin.grid.entity.Entity;
import com.pixelgriffin.grid.entity.Explosion;
import com.pixelgriffin.grid.entity.GeneratorUnit;
import com.pixelgriffin.grid.entity.MinerUnit;
import com.pixelgriffin.grid.input.BoardInputProcessor;
import com.pixelgriffin.grid.input.TutorialInputProcessor;
import com.pixelgriffin.grid.logic.AIOpponent;
import com.pixelgriffin.grid.logic.GameBoard;
import com.pixelgriffin.grid.logic.Opponent;
import com.pixelgriffin.grid.logic.ShakeCamera;
import com.pixelgriffin.grid.screen.AbstractGame;
import com.pixelgriffin.grid.util.ColorUtility;

/**
 * 
 * @author Nathan
 *
 */
public class TutorialScreen extends AbstractGame {
	
	//game instance reference
	public GridGame instance;
	
	//game board stuff
	private GameBoard board;
	private TutorialInputProcessor boardProc;
	
	//bullet & explosion pool
	private HashSet<Explosion> explosions;
	private HashSet<Explosion> removeExplosions;
	
	private AIOpponent voidOpponent;
	
	//camera
	private ShakeCamera cam;
	
	//tutorial stuff
	public int messageIndex;
	private String[] messages;
	
	public TutorialScreen(GridGame _instance) {
		this.instance = _instance;
		
		//instantiate board 
		this.board = new GameBoard();
		
		//create tutorial messages
		this.messageIndex = 0;
		this.messages = new String[] {
				"WELCOME TO THE GRID TUTORIAL\nTAP ANYWHERE TO FORWARD THE TUTORIAL",
				"TAP ON A GRID CELL TO BEGIN BULDING",
				"TAPPING ON A GRID CELL WILL BRING UP THE BUILD DIALOGUE",
				"TAPPING ON ONE OF THE UNITS IN THE BUILD DIALOGUE WILL ADD THEM TO THE BOARD",
				"TAP ON A MINER TO CONTINUE",
				"MINERS GENERATE RESOURCES THAT YOU NEED TO BUILD UNITS",
				"KEEP A CLOSE EYE ON YOUR RESOURCES",
				"YOUR RESOURCES ARE DISPLAYED AT THE TOP LEFT OF THE SCREEN",
				"THE ENEMY'S ARE DISPLAYED ON THE TOP RIGHT OF THE SCREEN",
				"NOW TRY BUILDING A SHIELD WHEN YOU REACH 100 RESOURCES",
				"SHIELDS HAVE A HIGH AMOUNT OF HEALTH SO THEY ARE TOUGH TO DESTROY",
				"THEY ALSO COVER A LARGE RANGE OF CELLS",
				"AFTER 30 SECONDS THE MIDDLE OF THE BOARD WILL OPEN FOR BUILDING",
				"EITHER PLAYER CAN BUILD IN THE WHTIE SPACE",
				"TRY BUILDING A TURRET WHEN YOU REACH 50 RESOURCES",
				"TURRETS ARE YOUR OFFENSIVE UNIT",
				"TURRETS WILL TARGET UNITS WITHIN A TWO CELL RADIUS OF THEM IN A CLOCKWISE FASHION",
				"NOW TRY BUILDING A POWER UNIT WHEN YOU REACH 250 RESOURCES",
				"POWER UNITS SUPPLY SPEED BONUSES TO ALL OF YOUR UNITS",
				"POWER UNITS MAKE TURRETS SHOOT FASTER AND MINERS GENERATE RESOURCES FASTER",
				"POWER UNITS ALSO HAVE A HIGH AMOUNT OF HEALTH SO THEY CAN WITHSTAND ATTACKS FOR LONGER PERIODS",
				"YOU NOW KNOW THE BASICS OF PLAYING GRID",
				"GOOD LUCK!"
		};
		
		//add empty opponent
		this.voidOpponent = new AIOpponent();
		
		//add preliminary stuff
		try {
			//add our stuff
			MinerUnit em = new MinerUnit(true);
			this.board.addUnit(5, 0, em);
			GeneratorUnit eg = new GeneratorUnit(true);
			this.board.addUnit(4, 0, eg);
			
			//Do this instead of ai.addUnit(x) because
			//we do not want to take from resources
			MinerUnit am = new MinerUnit(false);
			this.board.addUnit(4, 5, am);
			GeneratorUnit ag = new GeneratorUnit(false);
			this.board.addUnit(5, 5, ag);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		this.boardProc = new TutorialInputProcessor(this);
		
		//instatiate bullet pool
		explosions = new HashSet<Explosion>();
		removeExplosions = new HashSet<Explosion>();
		
		//initialize camera
		this.cam = new ShakeCamera();
		this.cam.setToOrtho(false, 1280, 720);
		
		this.cam.update();
		
		//open input processing
		GridGame.registerInputListener(this.boardProc);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//update
		this.board.update(delta);
		this.cam.update();
		
		if(messageIndex < 12) {
			getBoard().setGameTime(23f);
		}
		
		//update bullets
		updateBullets(delta);
		
		//update explosions
		for(Explosion e: removeExplosions) {
			explosions.remove(e);
		}
		removeExplosions.clear();
		
		for(Explosion e : explosions) {
			e.update(delta);
		}
		
		//draw
		SpriteBatch batch = GridGame.getDefaultBatch();
		BitmapFont font = GridGame.getDefaultFont();
		
		batch.setProjectionMatrix(this.cam.combined);
		batch.begin();
			this.board.render(batch);
			this.boardProc.draw(batch);//draw dialogues
			
			drawBullets(batch);
			for(Explosion e : explosions) {
				e.draw(batch);
			}
			
			//resource scores
			font.setColor(ColorUtility.playerColor);
			font.draw(batch, "" + GameBoard.playerResource, 100, 700);
			font.setColor(ColorUtility.otherColor);
			font.draw(batch, "" + GameBoard.otherResource, 1100, 700);
			font.setColor(Color.WHITE);
			
			font.drawWrapped(batch, this.messages[this.messageIndex], 1000, 600, 280);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
		GridGame.unregisterInputListener(boardProc);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
	
	public ShakeCamera getCamera() {
		return this.cam;
	}
	
	public GameBoard getBoard() {
		return this.board;
	}
	
	public void addExplosion(Explosion _e) {
		this.explosions.add(_e);
	}
	
	public void removeExplosion(Explosion _e) {
		this.removeExplosions.add(_e);
	}

	@Override
	public Opponent getOpponent() {
		return voidOpponent;
	}

	@Override
	public void addEntity(Entity _e) {
	}

	@Override
	public void removeEntity(Entity _e) {
		// TODO Auto-generated method stub
		
	}
}
