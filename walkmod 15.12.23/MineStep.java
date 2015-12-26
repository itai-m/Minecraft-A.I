package com.custommods.walkmod;

import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.util.Vec3;
import scala.Int;

class MineStep extends Step{

	private LinkedList<MineSpot> mineSpots;
	public String mineType;
	public MineStep(Step parent, double cost, Vec3 location, LinkedList<MineSpot> mineSpots, String mineType) {
		super(parent, cost, location);
		
		this.mineSpots = mineSpots;
		this.mineType = mineType;
	}

	public LinkedList<MineSpot> getStepsToMine(){
		return this.mineSpots;
	}

	@Override
	protected LinkedList<Vec3> getStepCollidingLocations() {
		LinkedList<Vec3> stepCollidingLocations = new LinkedList<Vec3>();
		//for now, we have an issue that location could appear twice - once in minespots 
		//and then in step location. looking forward for effient way to fix it.
		
		/*
		for (int i = -1; i < 2; i++){
			stepCollidingLocations.add(Vec3.createVectorHelper(location.xCoord, location.yCoord + i, location.zCoord));
		}
		Vec3 parentLoc = parent.getLocation();
		if (location.yCoord > parentLoc.yCoord)
			//jump mine step
			stepCollidingLocations.add(Vec3.createVectorHelper(parentLoc.xCoord, parentLoc.yCoord + 2, parentLoc.zCoord));
		else if (location.yCoord < parentLoc.yCoord){
			// cliff mine step
			for (int i = (int)location.yCoord + 2; i <= (int)parentLoc.yCoord; i++)
				stepCollidingLocations.add(Vec3.createVectorHelper(location.xCoord, i, location.zCoord));
		}*/
		return stepCollidingLocations;
	}

	@Override
	protected double getCollidingFine(Step s) {
		if (this.isCollidingWith(s))
			//return 1000;
			return 0;
		return 0;
	}
	
}