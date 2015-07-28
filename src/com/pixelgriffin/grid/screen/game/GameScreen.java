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
import com.pixelgriffin.grid.logic.AIOpponent;
import com.pixelgriffin.grid.logic.GameBoard;
import com.pixelgriffin.grid.logic.ShakeCamera;
import com.pixelgriffin.grid.screen.AbstractGame;
import com.pixelgriffin.grid.util.ColorUtility;

/**
 * 
 * @author Nathan
 *
 */
public class GameScreen extends AbstractGame {
	public static final float AI_EASY = 1.5f;
	public static final float AI_MEDIUM = 1.0f;
	public static final float AI_HARD = 0.25f;
	
	public static float AI_DIFFICULTY = 0f;
	
	//game instance reference
	public GridGame instance;
	
	//game board stuff
	private GameBoard board;
	private BoardInputProcessor boardProc;
	
	//bullet & explosion pool
	private HashSet<Entity> entities;
	private HashSet<Entity> removeEntity;
	
	private HashSet<Explosion> explosions;
	private HashSet<Explosion> removeExplosions;
	
	//AI
	private AIOpponent ai;
	
	//camera
	private ShakeCamera cam;
	
	private boolean hasCreatedPrelim = false;
	
	public GameScreen(GridGame _instance, float _difficulty) {
		super();
		
		this.instance = _instance;
		AI_DIFFICULTY = _difficulty;
		
		//instantiate board 
		this.board = new GameBoard();
		
		//initialize AI
		ai = new AIOpponent();
		
		this.boardProc = new BoardInputProcessor(this);
		
		//instantiate bullet pool and entities
		entities = new HashSet<Entity>();
		removeEntity = new HashSet<Entity>();
		
		explosions = new HashSet<Explosion>();
		removeExplosions = new HashSet<Explosion>();
		
		//initialize camera
		this.cam = new ShakeCamera();
		this.cam.setToOrtho(false, 1280, 720);
		
		this.cam.update();
		
		//open input processing
		GridGame.registerInputListener(this.boardProc);
	}
	
	private void createPreliminaryUnits() {
		//add preliminary stuff
		try {
			//add our stuff
			MinerUnit em = new MinerUnit(true);
			this.board.addUnit(5, 0, em);
			this.ai.enemyMiners.add(em);
			GeneratorUnit eg = new GeneratorUnit(true);
			this.board.addUnit(4, 0, eg);
			this.ai.enemyGenerators.add(eg);
			
			//Do this instead of ai.addUnit(x) because
			//we do not want to take from resources
			MinerUnit am = new MinerUnit(false);
			this.board.addUnit(4, 5, am);
			this.ai.ourMiners.add(am);
			GeneratorUnit ag = new GeneratorUnit(false);
			this.board.addUnit(5, 5, ag);
			this.ai.ourGenerators.add(ag);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		hasCreatedPrelim = true;
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(!hasCreatedPrelim)
			createPreliminaryUnits();
		
		//update
		//check for a winner
		if(GameBoard.playerResource < MinerUnit.VALUE && ai.enemyMiners.isEmpty()) {
			//ai won
			instance.toFinish("LOST", false);
			return;
		} else if(GameBoard.otherResource < MinerUnit.VALUE && ai.ourMiners.isEmpty()) {
			//player won
			instance.toFinish("WON", false);
			return;
		}
		
		//update AI and board entities
		this.ai.update(delta);
		this.board.update(delta);
		this.cam.update();
		
		//update bullets
		updateBullets(delta);
		
		//update visualizers
		for(Entity e : this.removeEntity) {
			this.entities.remove(e);
		}
		this.removeEntity.clear();
		
		for(Entity e : this.entities) {
			e.update(delta);
		}
		
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
			
			for(Entity e : this.entities) {
				e.draw(batch);
			}
			
			renderLights(this.cam.combined, delta);
		batch.end();
			
			//renderLights(this.cam.combined, delta);
		
		batch.begin();	
			drawBullets(batch);
			for(Explosion e : explosions) {
				e.draw(batch);
			}
		
			this.board.drawCountdown(batch);
			this.boardProc.draw(batch);//draw dialogues
		
			//resource scores
			font.setColor(ColorUtility.playerColor);
			font.draw(batch, "" + GameBoard.playerResource, 100, 700);
			font.setColor(ColorUtility.otherColor);
			font.draw(batch, "" + GameBoard.otherResource, 1100, 700);
			font.setColor(Color.WHITE);
			
			//unit info
			drawUnitInfo(batch);
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
	
	public AIOpponent getOpponent() {
		return this.ai;
	}

	@Override
	public void addEntity(Entity _e) {
		this.entities.add(_e);
	}

	@Override
	public void removeEntity(Entity _e) {
		this.removeEntity.add(_e);
	}
}
