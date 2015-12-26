package com.custommods.walkmod;

import java.util.LinkedList;

import net.minecraft.util.Vec3;

public abstract class Step {
	public static enum StepType{Walk, Jump, Mine, Pole, Bridge, Unassigned};
	//private static final int STEP_COLLIDING_FINE = 0;
	private static final int STEP_COLLIDING_FINE = 10;
	
	protected final Step parent;
	
	
	//****** TO BE REMOVED
	//private final StepType type;
	protected final Vec3 location;
	protected final double cost;
	private double totalCost = -1;
	protected final LinkedList<Vec3> collidingLocations;
	
	public boolean isCollidingWith(Step s){
		
		LinkedList<Vec3> currentStepCollidingLocations = this.collidingLocations;
		LinkedList<Vec3> otherStepCollidingLocations = s.collidingLocations;
		

		for (Vec3 currentStepCollidingLocation : currentStepCollidingLocations) {
			for (Vec3 otherStepCollidingLocation : otherStepCollidingLocations) {
				if (MinecraftWorldInfo.vec3Equlas(currentStepCollidingLocation, otherStepCollidingLocation))
					return true;
			}
		}
		return false;
	}
	
	protected abstract LinkedList<Vec3> getStepCollidingLocations();
	
	protected double getCollidingFine(Step s){
		if(this.isCollidingWith(s))
			return STEP_COLLIDING_FINE;
		return 0;
	}
	
	public Step getParent() {
		return parent;
	}
	//****** TO BE REMOVED
	/*
	public StepType getType() {
		return type;
	}*/

	public Vec3 getLocation() {
		return location;
	}
	
	public double getCost() {
		return cost;
	}
	public double getTotalCost() {
		if(-1 == totalCost)
			totalCost = calculateTotalCost();
		return totalCost;
	}
	//****** TO BE REMOVED
	/*public Step(Step parent, StepType type, double cost, Vec3 location){
		this.parent = parent;
		this.type = type;
		this.cost = cost;
		this.location = location;
		
		if (null != parent)
			this.totalCost = cost + parent.totalCost;
		else
			this.totalCost = cost;
	}*/
	
	public Step(Step parent, double cost, Vec3 location){
		this.parent = parent;
		this.cost = cost;
		this.location = location;
		this.collidingLocations = getStepCollidingLocations();
	}
	
	private double calculateTotalCost(){
		double totalCost = 0;
		if (null != parent){
			totalCost = cost + parent.totalCost;
			Step currentStep = parent;
			
			while (currentStep != null){
				totalCost +=  this.getCollidingFine(currentStep);
				currentStep = currentStep.getParent();
			}
		}else
			totalCost = cost;
		
		return totalCost;
	}
	
	@Override
	public int hashCode() {
		if (location == null)
			return 0;
		return (int)Math.floor((location.xCoord - location.yCoord) * location.zCoord);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		Step other = (Step) obj;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (location.xCoord != other.location.xCoord || location.yCoord != other.location.yCoord || location.zCoord != other.location.zCoord)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return location.toString();
	}
}
