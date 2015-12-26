package com.custommods.walkmod;

import java.util.LinkedList;

import net.minecraft.util.Vec3;

class BridgeStep extends Step{

	public BridgeStep(Step parent, double cost, Vec3 location) {
		super(parent, cost, location);
	}

	@Override
	protected LinkedList<Vec3> getStepCollidingLocations() {
		LinkedList<Vec3> stepCollidingLocations = new LinkedList<Vec3>();
		for (int i = -1; i < 2; i++){
			stepCollidingLocations.add(Vec3.createVectorHelper(location.xCoord, location.yCoord + i, location.zCoord));
		}
		return stepCollidingLocations;
	}
}