package com.custommods.walkmod;

import java.util.List;
import net.minecraft.util.Vec3;

interface IWorldInfo {
	public double getMinimalDistance(Vec3 pos, Vec3 goal);
	//public double getMinimalDistance(int[] pos, int[] goal);
	//Itay: In my opinion, getMinimalDistance should be static function of MinecraftWorldInfo
	public List<Step> getNeighbors(Step currStep);
	
	public  boolean isPossiblePlaceToStand(Vec3 pos);
	
}
