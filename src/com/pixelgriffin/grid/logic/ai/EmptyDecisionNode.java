package com.pixelgriffin.grid.logic.ai;

/**
 * 
 * @author Nathan
 *
 */
public class EmptyDecisionNode extends DecisionNode {
	@Override
	public boolean isSaciated() {
		return true;
	}
}
