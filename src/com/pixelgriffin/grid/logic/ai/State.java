package com.pixelgriffin.grid.logic.ai;

/**
 * 
 * @author Nathan
 *
 */
public abstract class State {
	
	public State() {
		reset();
	}
	
	/**
	 * Continues to execute state
	 * @param _dt delta time
	 */
	public abstract void update(float _dt);
	
	/**
	 * Returns true when this state's routine is finished
	 * @return
	 */
	public abstract boolean finished();

	/**
	 * Resets all values, called on creation
	 */
	public abstract void reset();
}
