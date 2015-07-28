package com.pixelgriffin.grid.logic;

import java.util.HashSet;

import com.pixelgriffin.grid.entity.MinerUnit;

/**
 * 
 * @author Nathan
 *
 */
public abstract class Opponent {
	public HashSet<MinerUnit> ourMiners;
	public HashSet<MinerUnit> enemyMiners;
	
	public Opponent() {
		ourMiners = new HashSet<MinerUnit>();
		enemyMiners = new HashSet<MinerUnit>();
	}
}
