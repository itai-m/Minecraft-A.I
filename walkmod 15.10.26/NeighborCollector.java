package com.custommods.walkmod;

import java.util.List;

import com.custommods.walkmod.Step.StepType;
import com.sun.javafx.geom.Vec3d;

import net.minecraft.util.Vec3;

class NeighborCollector implements IWorldInfo {
	private IWorldInfo worldInfo;
	
	public NeighborCollector(IWorldInfo worldInfo){
		this.worldInfo = worldInfo;
	}
	
	public Step[] getNeighbors(){
		return null;
	}

	@Override
	public double getMinimalDistance(Vec3 pos, Vec3 goal) {
		return worldInfo.getMinimalDistance(pos, goal);
	}

	@Override
	public List<Step> getNeighbors(Step currStep) {
		List<Step> neighbors = worldInfo.getNeighbors(currStep);
		Vec3 currLocation = currStep.getLocation();
		Vec3 polePos = Vec3.createVectorHelper(currLocation.xCoord, currLocation.yCoord+1, currLocation.zCoord);
		if (worldInfo.isPossiblePlaceToStand(polePos))
			neighbors.add(new PoleStep(currStep, 1.1, polePos));
		return neighbors;
	}

	@Override
	public boolean isPossiblePlaceToStand(Vec3 pos) {
		return worldInfo.isPossiblePlaceToStand(pos);
	}
}

