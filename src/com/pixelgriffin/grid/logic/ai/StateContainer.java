package com.pixelgriffin.grid.logic.ai;

/**
 * 
 */
import java.util.LinkedList;

/**
 * A container that holds a state loop
 * 
 * @author Nathan
 *
 */
public class StateContainer {
	private State currentState;
	private LinkedList<State> states;
	private LinkedList<DecisionNode> nodes;
	private int currentStateIndex;
	
	//constantly checked during every update
	private DecisionNode constantCheck;
	//if the decision node above returns true
	//then we enter  the below container until the above is false
	private StateContainer constantContainer;
	
	public StateContainer() {
		this.states = new LinkedList<State>();
		this.nodes = new LinkedList<DecisionNode>();
		
		this.currentStateIndex = 0;
		this.currentState = null;
		
		this.constantCheck = null;
		this.constantContainer = null;
	}
	
	/**
	 * Add a state to the end of the current routine
	 * @param _s the state to be added
	 * @param _n the decision to be made in order to continue on to the next state in the sequence
	 */
	public void addState(State _s, DecisionNode _n) {
		this.states.add(_s);
		
		if(_n == null) {
			//null node means empty node, so we just need to return true for it for the most part
			this.nodes.add(DecisionNode.EMPTY);
		} else {
			this.nodes.add(_n);
		}
	}
	
	public void setConstant(StateContainer _sc, DecisionNode _n) {
		this.constantCheck = _n;
		this.constantContainer = _sc;
	}
	
	public void update(float _dt) {
		if(this.states.isEmpty())
			return;
		
		//check decision constant
		//do we have a constant check in this container?
		if(this.constantCheck != null) {
			//if the constant check is true
			if(this.constantCheck.isSaciated()) {
				//then we enter the constant container
				this.constantContainer.update(_dt);
				
				return;//we do not update the regular routines during the constant updating
			}
		}
		
		//gather next if null
		if(this.currentState == null) {
			this.currentState = this.states.get(0);
		} else if(this.currentState.finished()) {//or if the current state routine is done
			if(this.nodes.get(this.currentStateIndex).isSaciated()) {//if the decision node for this state is saciated
				//update current state index to gather next state
				this.currentStateIndex++;
				if((this.states.size() - 1) < this.currentStateIndex) {
					this.currentStateIndex = 0;
				}
				
				//gather state from next index
				this.currentState = this.states.get(this.currentStateIndex);
				this.currentState.reset();//make sure our new state is reset before updating
				
				//FIXME DEBUG
				System.out.println("Switched states to: " + this.currentState.getClass().getSimpleName());
			} else {//it aint sacitaed
				//continue executing the same state
				this.currentState.reset();
			}
		}
		
		//execute current state
		this.currentState.update(_dt);
	}
}
