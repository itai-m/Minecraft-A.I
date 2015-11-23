package com.custommods.walkmod;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import net.minecraft.util.Vec3;
import net.minecraft.world.storage.WorldInfo;

class OrientedStepList{
	private LinkedList<Step> steps;
	private HashSet<Step> garbage;
	private Vec3 goal;
	private IWorldInfo worldInfo;
	
	
	public OrientedStepList(Vec3 goal, IWorldInfo worldInfo){
		steps = new LinkedList<Step>();
		this.garbage = new HashSet<Step>();
		this.worldInfo = worldInfo;
		this.goal = goal;
	}
	
	//returns false if there's no point in adding the step
	//because it was already explored (the player already considered it, and got there more efficiently) 
	public boolean add(Step stepToAdd){
		if (garbage.contains(stepToAdd))
				return false;
		
		//Add step to the steps list
		for (ListIterator<Step> it = steps.listIterator() ; it.hasNext() ; ){
			int index = it.nextIndex();
			Step currentStep = it.next();

			if (stepToAdd.getTotalCost()
					+ worldInfo.getMinimalDistance(stepToAdd.getLocation(), goal) * 1.5
					< 
					currentStep.getTotalCost()
					+ worldInfo.getMinimalDistance(currentStep.getLocation(), goal) * 1.5){
				steps.add(index, stepToAdd);
				garbage.add(stepToAdd);
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
