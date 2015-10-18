package com.custommods.walkmod;

import net.minecraft.util.Vec3;

class Step {
	public static enum StepType{Walk, Jump, Mine, Pole, Bridge, Unassinged};
	
	private final Step parent;
	private final StepType type;
	private final Vec3 location;
	private final double cost;
	private final double totalCost;
	
	public Step getParent() {
		return parent;
	}

	public StepType getType() {
		return type;
	}

	public Vec3 getLocation() {
		return location;
	}
	
	public double getCost() {
		return cost;
	}
	public double getTotalCost() {
		return totalCost;
	}
	
	public Step(Step parent, StepType type, double cost, Vec3 location){
		this.parent = parent;
		this.type = type;
		this.cost = cost;
		this.location = location;
		
		if (null != parent)
			this.totalCost = cost + parent.totalCost;
		else
			this.totalCost = cost;
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
