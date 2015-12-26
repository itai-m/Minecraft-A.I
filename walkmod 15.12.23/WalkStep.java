package com.custommods.walkmod;

import java.util.LinkedList;

import net.minecraft.util.Vec3;

class WalkStep extends Step{

	public WalkStep(Step parent, double cost, Vec3 location) {
		super(parent, cost, location);
	}

	@Override
	protected LinkedList<Vec3> getStepCollidingLocations() {
		LinkedList<Vec3> stepCollidingLocations = new LinkedList<Vec3>();
		for (int i = -1; i < 2; i++){
			stepCollidingLocations.add(Vec3.createVectorHelper(location.xCoord, location.yCoord + i, location.zCoord));
		}
		if(parent != null) {
			int dx = (int)(location.xCoord - parent.getLocation().xCoord);
			int dz = (int)(location.zCoord - parent.getLocation().zCoord);
			/*if(dx != 0 && dz != 0){ //diagonal walk
				for (int i = 1; i < 2; i++){
					stepCollidingLocations.add(Vec3.createVectorHelper(location.xCoord+dx, location.yCoord + i, location.zCoord));
				}
				for (int i = 1; i < 2; i++){
					stepCollidingLocations.add(Vec3.createVectorHelper(location.xCoord, location.yCoord + i, location.zCoord+dz));
				}
			}*/
		}
		return stepCollidingLocations;
	}
}