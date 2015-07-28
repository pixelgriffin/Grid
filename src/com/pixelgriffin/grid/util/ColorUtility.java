package com.pixelgriffin.grid.util;

import com.badlogic.gdx.graphics.Color;

/**
 * 
 * @author Nathan
 *
 */
public class ColorUtility {
	
	public static Color playerColor = mixColors(Color.CYAN, new Color(1, 1, 1, 0.5f));
	public static Color otherColor = mixColors(Color.MAGENTA, new Color(1, 1, 1, 0.5f));
	
	public static Color mixColors(Color start, Color mix) {
		float totalA = start.a + mix.a;
		float weight0 = start.a / totalA;
		float weight1 = mix.a / totalA;
		
		float r = weight0 * start.r + weight1 * mix.r;
		float g = weight0 * start.g + weight1 * mix.g;
		float b = weight0 * start.b + weight1 * mix.b;
		float a = Math.max(start.a, mix.a);
		
		return new Color(r, g, b, a);
	}
}
