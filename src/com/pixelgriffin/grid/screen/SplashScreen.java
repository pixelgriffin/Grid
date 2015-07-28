package com.pixelgriffin.grid.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pixelgriffin.grid.android.GridGame;
import com.pixelgriffin.grid.util.ColorUtility;

/**
 * 
 * @author Nathan
 *
 */
public class SplashScreen implements Screen {

	private GridGame instance;
	
	private OrthographicCamera cam;
	
	private float nameWidth;
	
	private float white;
	private boolean whiteUp;
	
	public SplashScreen(GridGame _instance) {
		this.instance = _instance;
		
		this.nameWidth = GridGame.getTitleFont().getBounds("PIXELGRIFFIN PRESENTS").width / 2f;
		this.white = 0f;
		this.whiteUp = true;
		
		this.cam = new OrthographicCamera(1280, 720);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(whiteUp) {
			this.white += 0.01f;
			
			if(this.white >= 1f) {
				this.whiteUp = false;
			}
		} else {
			this.white -= 0.01f;
			
			if(this.white <= 0f) {
				instance.toMenu(MainMenuScreen.INDEX_MAIN, true);
			}
		}
		
		this.cam.update();
		//ShaderManager.updateDefaultShader(this.white);
		
		SpriteBatch b = GridGame.getDefaultBatch();
		
		b.setProjectionMatrix(this.cam.combined);
		b.begin();
			//b.draw(griffin, -265, -312);
			GridGame.getTitleFont().draw(b, "PIXELGRIFFIN PRESENTS", -nameWidth, 0);
		b.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	//on first show
	@Override
	public void show() {
		//set the shader to our default
		//GridGame.getDefaultBatch().setShader(ShaderManager.DEFAULT_SHADER);
	}

	@Override
	public void hide() {
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

}
