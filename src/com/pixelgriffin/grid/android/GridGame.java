package com.pixelgriffin.grid.android;

import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.pixelgriffin.grid.gpgs.GoogleAccountAdapter;
import com.pixelgriffin.grid.screen.AbstractGame;
import com.pixelgriffin.grid.screen.GameFinishedScreen;
import com.pixelgriffin.grid.screen.MainMenuScreen;
import com.pixelgriffin.grid.screen.NetworkWaitingScreen;
import com.pixelgriffin.grid.screen.SplashScreen;
import com.pixelgriffin.grid.screen.game.GameScreen;
import com.pixelgriffin.grid.screen.game.NetworkGameScreen;
import com.pixelgriffin.grid.screen.game.TutorialScreen;
import com.pixelgriffin.grid.util.ColorUtility;
import com.pixelgriffin.grid.util.SettingsUtility;

/**
 * GridGame - extension of LibGDX's Game class
 * 
 * interfaces with Android to render & update
 * as well as abstract some of the game-oriented
 * screen systems.
 * 
 * @author Nathan
 *
 */
public class GridGame extends Game {
	//LibGDX asset manager, handles loading & unloading
	//assets of different specified types.
	private static AssetManager assetMgr;
	
	private static InputMultiplexer multiplexer;
	
	private static SpriteBatch defaultBatch;
	private static BitmapFont tinyFont;
	private static BitmapFont defaultFont;
	private static BitmapFont titleFont;
	private static BitmapFont megaFont;
	
	private static ParticleEffect unitEffects;
	
	private static AbstractGame gameInstance;
	private static GridGame instance;
	
	private GoogleAccountAdapter gpgsAdapter;
	
	/*
	 * Invite draw stuff
	 */
	private boolean inviteUp;
	private float inviteY;
	private int inviteWaitingCount;
	
	private float colAlpha;
	private boolean colAlphaUp;
	private Random alphaRand;
	
	public GridGame(GoogleAccountAdapter _gpgs) {
		this.gpgsAdapter = _gpgs;
	}
	
	@Override
	public void create() {
		//initialize managers
		assetMgr = new AssetManager();//handle assets
		multiplexer = new InputMultiplexer();//handle input processing
		
		//initialize batcher
		defaultBatch = new SpriteBatch();
		
		//create fonts
		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("pdark.ttf"));
		FreeTypeFontParameter defaultParam = new FreeTypeFontParameter();
		defaultParam.size = 36;
		
		defaultFont = gen.generateFont(defaultParam);
		defaultFont.setColor(Color.WHITE);
		
		defaultParam.size = 24;
		tinyFont = gen.generateFont(defaultParam);
		tinyFont.setColor(Color.WHITE);
		
		defaultParam.size = 48;
		titleFont = gen.generateFont(defaultParam);
		defaultFont.setColor(Color.WHITE);
		
		defaultParam.size = 128;
		megaFont = gen.generateFont(defaultParam);
		megaFont.setColor(Color.WHITE);
		
		gen.dispose();
		
		unitEffects = new ParticleEffect();
		unitEffects.load(Gdx.files.internal("unitEffects.p"), Gdx.files.internal(""));
		
		//check shaders
		//ShaderManager.checkShaders();
		
		//load assets
		//FIXME temp: should have a loading screen?
		assetMgr.load("griffin.png", Texture.class);
		assetMgr.load("unit/generator.png", Texture.class);
		assetMgr.load("unit/miner.png", Texture.class);
		assetMgr.load("unit/turret.png", Texture.class);
		assetMgr.load("unit/shield.png", Texture.class);
		assetMgr.load("grid2.png", Texture.class);
		assetMgr.load("select_unit_big.png", Texture.class);
		assetMgr.load("bullet.png", Texture.class);
		assetMgr.load("button.png", Texture.class);
		assetMgr.load("music_button.png", Texture.class);
		
		assetMgr.load("explosion.ogg", Sound.class);
		assetMgr.load("blip.ogg", Sound.class);
		assetMgr.load("construct.ogg", Sound.class);
		assetMgr.load("pew.ogg", Sound.class);
		
		assetMgr.load("music.ogg", Music.class);
		assetMgr.finishLoading();
		//----------
		
		//load settings
		SettingsUtility.load();
		
		//register input for handling
		Gdx.input.setInputProcessor(multiplexer);
		Gdx.input.setCatchBackKey(true);//handle the back key press ourselves
		
		//FIXME temp
		setScreen(new SplashScreen(this));
		//----------
		
		//music and sound settings
		Music m = assetMgr.get("music.ogg", Music.class);
		m.setLooping(true);
		
		if(SettingsUtility.musicOn)
			m.play();
		
		instance = this;
		
		//invite
		this.inviteUp = false;
		this.inviteY = 0f;
		
		this.alphaRand = new Random();
		this.colAlpha = 0.5f;
		this.colAlphaUp = true;
		
		this.inviteWaitingCount = 0;
	}
	
	@Override
	public void render() {
		super.render();
		
		if(this.getScreen() instanceof SplashScreen)
			return;
		
		//alpha update
		float nextRand = alphaRand.nextFloat() / 100f;
		
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
		
		//invite update
		if(this.inviteUp) {
			if(this.inviteY < 150) {
				this.inviteY += 2;
			} else {
				this.inviteUp = false;
			}
		} else {
			if(this.inviteY > 0) {
				this.inviteY -= 2;
			} else {
				if(this.inviteWaitingCount > 0) {
					this.inviteWaitingCount--;
					this.inviteUp = true;
				}
			}
		}
		
		float drawY = this.inviteY;
		if(drawY > 100) {
			drawY = 100;
		}
		
		//invite overlay render
		Texture tex = assetMgr.get("button.png", Texture.class);
		
		defaultBatch.begin();
			defaultBatch.setColor(ColorUtility.otherColor.r, ColorUtility.otherColor.g, ColorUtility.otherColor.b, colAlpha);
			//defaultBatch.draw(tex, -640, drawY - 125);
			defaultBatch.draw(tex, -740, drawY - 125, 1280, 100, 0, 0, 1280, 100, true, false);
			defaultFont.draw(defaultBatch, "NEW INVITE", 25, drawY - 60);
			defaultBatch.setColor(Color.WHITE);
		defaultBatch.end();
	}
	
	@Override
	public void dispose() {
		defaultBatch.dispose();
		defaultFont.dispose();
		titleFont.dispose();
		megaFont.dispose();
		
		unitEffects.dispose();
		
		assetMgr.dispose();
		
		SettingsUtility.save();
	}
	
	public void pushInvite() {
		this.inviteWaitingCount++;
	}
	
	public void toTutorial() {
		gameInstance = new TutorialScreen(this);
		setScreen(gameInstance);
	}
	
	public void toFinish(String _last, boolean _net) {
		gameInstance = null;
		setScreen(new GameFinishedScreen(this, _last, _net));
	}
	
	public void toGame(boolean _net, float _diff) {
		if(!_net) {
			gameInstance = new GameScreen(this, _diff);
			setScreen(gameInstance);
		} else {
			gameInstance = new NetworkGameScreen(this);
			setScreen(gameInstance);
		}
	}
	
	public void toMenu(int _index, boolean _showSignIn) {
		gameInstance = null;
		
		MainMenuScreen screen = new MainMenuScreen(this);
		screen.setIndex(_index);
		
		setScreen(screen);
		
		if(_showSignIn) {
			if(!gpgsAdapter.isSignedIn()) {
				gpgsAdapter.signIn();
			}
		}
	}
	
	public void toNetworkWaiting() {
		gameInstance = null;
		setScreen(new NetworkWaitingScreen(this));
	}
	
	public GoogleAccountAdapter getGPGS() {
		return gpgsAdapter;
	}
	
	public boolean isInGame() {
		return (getScreen() instanceof AbstractGame);
	}
	
	//AssetManager getter
	//try to keep assetMgr private but methods public
	public static AssetManager getAssetManager() {
		return assetMgr;
	}
	
	//register listener with multiplexer
	public static void registerInputListener(InputProcessor _listener) {
		multiplexer.addProcessor(_listener);
	}
	
	//unregister listener with multiplexer
	public static void unregisterInputListener(InputProcessor _listener) {
		multiplexer.removeProcessor(_listener);
	}
	
	public static SpriteBatch getDefaultBatch() {
		return defaultBatch;
	}
	
	public static BitmapFont getDefaultFont() {
		return defaultFont;
	}
	
	public static BitmapFont getTinyFont() {
		return tinyFont;
	}
	
	public static BitmapFont getTitleFont() {
		return titleFont;
	}
	
	public static BitmapFont getMegaFont() {
		return megaFont;
	}
	
	public static AbstractGame getGame() {
		return gameInstance;
	}
	
	public static GridGame getInstance() {
		return instance;
	}
	
	public static ParticleEffect getUnitEffects() {
		return unitEffects;
	}
}
