package com.pixelgriffin.grid.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * 
 * @author Nathan
 *
 */
public class ShaderManagerUnused {
	
	public static ShaderProgram DEFAULT_SHADER = createProgram("default.vsh", "default.fsh");
	//public static ShaderProgram GLOW_SHADER = createProgram("default.vsh", "glow.fsh");
	
	public static void updateDefaultShader(float _val) {
		DEFAULT_SHADER.begin();
		DEFAULT_SHADER.setUniformf("u_fadeout", _val);
		DEFAULT_SHADER.end();
	}
	
	public static void checkShaders() {
		if(!DEFAULT_SHADER.isCompiled())
			System.out.println(DEFAULT_SHADER.getLog());
		//if(!GLOW_SHADER.isCompiled())
		//	System.out.println(GLOW_SHADER.getLog());
	}
	
	public static ShaderProgram createProgram(String _vert, String _frag) {
		String f = Gdx.files.internal("shader/" + _frag).readString();
		String v = Gdx.files.internal("shader/" + _vert).readString();
		
		return new ShaderProgram(v, f);
	}
}
