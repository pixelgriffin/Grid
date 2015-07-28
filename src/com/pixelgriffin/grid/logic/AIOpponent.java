package com.pixelgriffin.grid.logic;

import java.util.HashSet;

import com.pixelgriffin.grid.android.GridGame;
import com.pixelgriffin.grid.entity.GeneratorUnit;
import com.pixelgriffin.grid.entity.MinerUnit;
import com.pixelgriffin.grid.entity.ShieldUnit;
import com.pixelgriffin.grid.entity.TurretUnit;
import com.pixelgriffin.grid.entity.Unit;
import com.pixelgriffin.grid.logic.ai.BuildState;
import com.pixelgriffin.grid.logic.ai.DecisionIsBeingAttacked;
import com.pixelgriffin.grid.logic.ai.DecisionNode;
import com.pixelgriffin.grid.logic.ai.DefendState;
import com.pixelgriffin.grid.logic.ai.StateContainer;

/**
 * 
 * @author Nathan
 *
 */
public class AIOpponent extends Opponent {
	public HashSet<TurretUnit> ourTurrets;
	public HashSet<GeneratorUnit> ourGenerators;
	public HashSet<ShieldUnit> ourShields;
	
	public HashSet<TurretUnit> enemyTurrets;
	public HashSet<GeneratorUnit> enemyGenerators;
	public HashSet<ShieldUnit> enemyShields;
	
	private StateContainer stateMachine;
	
	public int focusX, focusY;//focus points of defensive building
	
	public AIOpponent() {
		super();
		
		//keeping track of the board
		ourTurrets = new HashSet<TurretUnit>();
		ourGenerators = new HashSet<GeneratorUnit>();
		ourMiners = new HashSet<MinerUnit>();
		ourShields = new HashSet<ShieldUnit>();
		
		enemyTurrets = new HashSet<TurretUnit>();
		enemyGenerators = new HashSet<GeneratorUnit>();
		enemyShields = new HashSet<ShieldUnit>();
		
		//state machine
		stateMachine = new StateContainer();
		stateMachine.addState(new BuildState(), DecisionNode.EMPTY);
		//stateMachine.addState(new AttackState(), DecisionNode.EMPTY);
		
		StateContainer defendContainer = new StateContainer();
		//since this is a constant container state, 
		//the entire container is subject to a decision node,
		//which in this case checks if we're being attacked
		//so we do not need a decision node for this state
		defendContainer.addState(new DefendState(), DecisionNode.EMPTY);
		
		//add constant container
		//if we are being attacked at any point during the state executions
		//we will switch to the defendContainer
		stateMachine.setConstant(defendContainer, new DecisionIsBeingAttacked());
		
		//foci
		this.focusX = this.focusY = -1;
	}
	
	public void postDeadUnit(Unit _u) {
		if(_u instanceof TurretUnit) {
			TurretUnit tu = (TurretUnit)_u;
			if(tu.isPlayerOwned())
				this.enemyTurrets.remove(tu);
			else
				this.ourTurrets.remove(tu);
		} else if(_u instanceof GeneratorUnit) {
			GeneratorUnit gu = (GeneratorUnit)_u;
			
			if(gu.isPlayerOwned())
				this.enemyGenerators.remove(gu);
			else
				this.ourGenerators.remove(gu);
		} else if(_u instanceof MinerUnit) {
			MinerUnit mu = (MinerUnit)_u;
			
			if(mu.isPlayerOwned())
				this.enemyMiners.remove(mu);
			else
				this.ourMiners.remove(mu);
		} else if(_u instanceof ShieldUnit) {
			ShieldUnit su = (ShieldUnit)_u;
			
			if(su.isPlayerOwned())
				this.enemyShields.remove(su);
			else
				this.ourShields.remove(su);
		}
	}
	
	public void addUnit(int _x, int _y, Unit _u) {
		if(_y < 2)
			return;
		
		GridGame.getGame().getBoard().addUnit(_x, _y, _u);
		
		int cost = 0;
		
		if(_u instanceof MinerUnit) {
			this.ourMiners.add((MinerUnit) _u);
			
			cost = MinerUnit.VALUE;
		} else if(_u instanceof GeneratorUnit) {
			this.ourGenerators.add((GeneratorUnit) _u);
			
			cost = GeneratorUnit.VALUE;
		} else if(_u instanceof ShieldUnit) {
			this.ourShields.add((ShieldUnit) _u);
			
			cost = ShieldUnit.VALUE;
		} else if(_u instanceof TurretUnit) {
			this.ourTurrets.add((TurretUnit) _u);
			
			cost = TurretUnit.VALUE;
		}
		
		GameBoard.otherResource -= cost;
	}
	
	public void update(float _dt) {
		stateMachine.update(_dt);
	}
}
