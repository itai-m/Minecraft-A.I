package com.custommods.walkmod;

import net.minecraft.util.Vec3;

class JumpStep extends Step{

	public JumpStep(Step parent, double cost, Vec3 location) {
		super(parent, cost, location);
	}
	
	@Override
	public boolean isCollidingWith(Step otherStep) {
		Vec3 otherStepLocation = otherStep.getLocation();
		if (parent != null){
			//this 
			Vec3 parentLocation = parent.getLocation();
			Vec3 parentUpperLocation = Vec3.createVectorHelper(
					parentLocation.xCoord, 
					parentLocation.yCoord + 1, 
					parentLocation.zCoord);
			if (MinecraftWorldInfo.Vec3Equlas(otherStepLocation, parentUpperLocation))
				return true;
		}
		if (MinecraftWorldInfo.Vec3Equlas(otherStepLocation, location))
			return true;
		return false;
	}
	
}