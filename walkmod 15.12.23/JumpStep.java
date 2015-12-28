package com.custommods.walkmod;

import java.util.LinkedList;

import net.minecraft.util.Vec3;
import scala.Int;

public class JumpStep extends Step{

	public JumpStep(Step parent, double cost, Vec3 location) {
		super(parent, cost, location);
	}
	
	@Override
	protected LinkedList<Vec3> getStepCollidingLocations() {
		LinkedList<Vec3> stepCollidingLocations = new LinkedList<Vec3>();
		for (int i = -1; i < 2; i++){
			stepCollidingLocations.add(Vec3.createVectorHelper(location.xCoord, location.yCoord + i, location.zCoord));
		}
		Vec3 parentLoc = parent.getLocation();
		stepCollidingLocations.add(Vec3.createVectorHelper(parentLoc.xCoord, parentLoc.yCoord + 2, parentLoc.zCoord));
		return stepCollidingLocations;
	}
	
}