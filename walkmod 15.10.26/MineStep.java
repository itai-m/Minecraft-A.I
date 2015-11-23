package com.custommods.walkmod;

import net.minecraft.util.Vec3;

class MineStep extends Step{

	public MineStep(Step parent, double cost, Vec3 location) {
		super(parent, cost + 0.5, location);
		
	}

	@Override
	public boolean isCollidingWith(Step otherStep) {
		// TODO Auto-generated method stub
		return false;
	}
	
}