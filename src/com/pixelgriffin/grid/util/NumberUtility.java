package com.pixelgriffin.grid.util;

import java.util.Random;

/**
 * 
 * @author Nathan
 *
 */
public class NumberUtility {
	
	private static Random rand = new Random();
	
	/**
	 * Inclusive min / max
	 * @param _min
	 * @param _max
	 * @return
	 */
	public static int nextIntRange(int _min, int _max) {
		return (rand.nextInt((_max - _min) + 1) + _min);
	}
	
	/**
	 * Inclusive min / max
	 * @param _min
	 * @param _max
	 * @return
	 */
	public static float nextFloat(float _min, float _max) {
		return _min + (rand.nextFloat() * ((1 + _max) - _min));
	}
	
	public static double getAngle(float _x, float _y, float _x2, float _y2) {
		double angle = Math.toDegrees(Math.atan2(_y2 - _y, _x2 - _x));
		
		if(angle < 0) {
			angle += 360;
		}
		
		return angle;
	}
	
	public static byte[] putBytesInArray(int _pos, byte[] _dest, byte[] _other) {
		for(int i = 0; i < _other.length; i++) {
			_dest[_pos + i] = _other[i];
		}
		
		return _dest;
	}
}
