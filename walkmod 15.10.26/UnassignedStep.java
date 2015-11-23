package com.custommods.walkmod;

import net.minecraft.util.Vec3;

class UnassignedStep extends Step{

	public UnassignedStep(Step parent, double cost, Vec3 location) {
		super(parent, cost, location);
	}

	@Override
	public boolean isCollidingWith(Step s) {
		return false;
	}

	
}