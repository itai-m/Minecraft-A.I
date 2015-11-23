package com.custommods.walkmod;

import net.minecraft.util.Vec3;

class BridgeStep extends Step{

	public BridgeStep(Step parent, double cost, Vec3 location) {
		super(parent, cost, location);
	}

	@Override
	public boolean isCollidingWith(Step otherStep) {
		// TODO Auto-generated method stub
		return false;
	}
	
}