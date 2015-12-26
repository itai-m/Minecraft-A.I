package com.custommods.walkmod;

import java.util.LinkedList;

import net.minecraft.util.Vec3;

class UnassignedStep extends Step{

	public UnassignedStep(Step parent, double cost, Vec3 location) {
		super(parent, cost, location);
	}

	@Override
	protected LinkedList<Vec3> getStepCollidingLocations() {
		return new LinkedList<Vec3>();
	}

	@Override
	protected double getCollidingFine(Step s) {
		return 0;
	}

	
}