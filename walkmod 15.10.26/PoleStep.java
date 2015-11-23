package com.custommods.walkmod;

import net.minecraft.util.Vec3;

class PoleStep extends Step{

	public PoleStep(Step parent, double cost, Vec3 location) {
		super(parent, cost, location);
	}
	

	
	@Override
	public boolean isCollidingWith(Step otherStep) {
		Vec3 otherStepLocation = otherStep.getLocation();
		Vec3 upperLocation = Vec3.createVectorHelper(location.xCoord, location.yCoord + 1, location.zCoord);
		if (MinecraftWorldInfo.Vec3Equlas(otherStepLocation, location) || MinecraftWorldInfo.Vec3Equlas(otherStepLocation, upperLocation))
			return true;
		return false;
	}
	
}