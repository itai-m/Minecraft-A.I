package com.custommods.walkmod;

import java.util.*;

import com.custommods.walkmod.Step.StepType;

import net.minecraft.util.Vec3;



public class PathFinder {
	private static final int MAX_TOTAL_COST = 300;
	
	private Vec3 initPos;
	private Vec3 goal;
	private IWorldInfo worldInfo;
	
	public PathFinder(Vec3 initPos, Vec3 goal, IWorldInfo worldInfo) {
		this.initPos = initPos;
		this.goal = goal;
		this.worldInfo = worldInfo;
	}
	
	public Queue<Step> findPath() {
		OrientedStepList steps = new OrientedStepList(goal, worldInfo);
		Step step = new WalkStep(null, 0, initPos);
		
		if (!worldInfo.isPossiblePlaceToStand(goal))
			return null;
		
		//System.out.println("*********PATH FINDING***********");
		while(worldInfo.getMinimalDistance(step.getLocation(), goal) > 0){ // H = 0
			//System.out.println("Testing (" + step.getLocation().xCoord + ", " + step.getLocation().yCoord + ", "+ step.getLocation().zCoord + ")");
			if(step.getTotalCost() < MAX_TOTAL_COST) {
				List<Step> neighbors = worldInfo.getNeighbors(step);
				for(Step neighbor : neighbors){
					if (null != neighbor && !(neighbor instanceof UnassignedStep))
					{
						steps.add(neighbor);
						//System.out.println("Neighbor added to steps");
					}
				}
			}
			step = steps.pop();
			if(step == null)
			{
				System.out.println("Step equals to null");
				return null;
			}
		}
		//END
		LinkedList<Step> returnStepsList = new LinkedList<Step>();
		while(null != step.getParent()){
			returnStepsList.addFirst(step);
			step = step.getParent();
		}
		returnStepsList.addFirst(new WalkStep(null, 0, initPos));
		return returnStepsList;
	}
	

	
}
