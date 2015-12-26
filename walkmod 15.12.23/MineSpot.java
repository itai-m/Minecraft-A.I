package com.custommods.walkmod;

import net.minecraft.util.Vec3;

class MineSpot{
	private Vec3 location;
	private double mineCost;
	public MineSpot(Vec3 location, double mineCost){
		this.location = location;
		this.mineCost = mineCost;
		
	}
	public Vec3 getLocation() {
		return location;
	}
	public double getMineCost() {
		return mineCost;
	}
}