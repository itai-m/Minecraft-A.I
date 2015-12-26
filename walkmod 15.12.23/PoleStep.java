package com.custommods.walkmod;

import java.util.LinkedList;

import net.minecraft.util.Vec3;

class PoleStep extends Step{

	public PoleStep(Step parent, double cost, Vec3 location) {
		super(parent, cost, location);
	}
	
	@Override
	protected LinkedList<Vec3> getStepCollidingLocations() {
		LinkedList<Vec3> stepCollidingLocations = new LinkedList<Vec3>();
		for (int i = -1; i < 2; i++){
			stepCollidingLocations.add(Vec3.createVectorHelper(location.xCoord, location.yCoord + i, location.zCoord));
		}
		
		//To avoid "leaves" issue (block above head blocking jump step)
		if (this.getParent() != null){
			Step grandParent = this.getParent().getParent();
			if (grandParent != null){
				Vec3 grandParentLoc = grandParent.getLocation();
				//Block above grandParent head.
				stepCollidingLocations.add(Vec3.createVectorHelper(grandParentLoc.xCoord, grandParentLoc.yCoord + 2, grandParentLoc.zCoord));
			}
		}
		return stepCollidingLocations;
	}
	
	@Override
	public boolean isCollidingWith(Step s){
		if (!(s instanceof PoleStep))
			return false;
		return super.isCollidingWith(s);
	}
	
}