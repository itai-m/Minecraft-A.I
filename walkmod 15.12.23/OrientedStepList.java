package com.custommods.walkmod;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.Vec3;
import net.minecraft.world.storage.WorldInfo;

class OrientedStepList{
	private static final double HEURISTIC_FACTOR = 1;

	private LinkedList<Step> steps;
	private HashMap<Step, Step> garbage;
	private Vec3 goal;
	private IWorldInfo worldInfo;
	
	public OrientedStepList(Vec3 goal, IWorldInfo worldInfo){
		steps = new LinkedList<Step>();
		this.garbage = new HashMap<Step, Step>();
		this.worldInfo = worldInfo;
		this.goal = goal;
	}
	
	//returns false if there's no point in adding the step
	//because it was already explored (the player already considered it, and got there more efficiently) 
	public boolean add(Step stepToAdd){
		if (garbage.containsKey(stepToAdd)){
			if(true)
				return false;
			Step stepInGarbage = garbage.get(stepToAdd);
			if (stepInGarbage.getTotalCost() < stepToAdd.getTotalCost())
				return false;
			else
				garbage.remove(stepToAdd);
		}
		
		//Add step to the steps list
		for (ListIterator<Step> it = steps.listIterator() ; it.hasNext() ; ){
			int index = it.nextIndex();
			Step currentStep = it.next();

			if (stepToAdd.getTotalCost()
					+ worldInfo.getMinimalDistance(stepToAdd.getLocation(), goal) * HEURISTIC_FACTOR
					< 
					currentStep.getTotalCost()
					+ worldInfo.getMinimalDistance(currentStep.getLocation(), goal) * HEURISTIC_FACTOR){
				steps.add(index, stepToAdd);
				garbage.put(stepToAdd, stepToAdd);
				return true;
			}
		}
		steps.addLast(stepToAdd);
		return true;
	}
	public Step pop(){
		return steps.pollFirst();
	}
}
