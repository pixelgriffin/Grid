package com.pixelgriffin.grid.logic.ai;

/**
 * 
 * @author Nathan
 *
 */
public abstract class DecisionNode {
	
	public static final DecisionNode EMPTY = new EmptyDecisionNode();
	
	public abstract boolean isSaciated();
}
