package com.custommods.walkmod;

import net.minecraft.util.Vec3;

class WalkStep extends Step{

	public WalkStep(Step parent, double cost, Vec3 location) {
		super(parent, cost, location);
	}

	@Override
	public boolean isCollidingWith(Step otherStep) {
		Vec3 otherStepLocation = otherStep.getLocation();
		
		if (MinecraftWorldInfo.Vec3Equlas(otherStepLocation, this.getLocation()))
			return true;
		return false;
	}
	
}