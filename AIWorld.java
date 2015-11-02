package com.custommods.ai;

import java.util.ArrayList;

import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class AIWorld{
	
	private World world;
	
	///Constructor
	public AIWorld(World world){
		this.world = world;
	}
	
	///Return the nearest block
	public Vec3 findNearestBlock(){
		return null;
	}

	///Get the world
	public World getWorld() {
		return world;
	}

	///Set the world
	public void setWorld(World world) {
		this.world = world;
	}
	
	///Get list of block of some kind
	public ArrayList<Vec3> getBlockList(Vec3 StartPos, double distance, String name){
		ArrayList<Vec3> toReturn = new ArrayList<Vec3>();
		for (int i = (int)(StartPos.xCoord - distance) ; i < StartPos.xCoord + distance ; i++){
			for (int j = (int)(StartPos.zCoord - distance) ; j < StartPos.zCoord + distance ; j++){
				int startY = (int) StartPos.yCoord;
				for (int k = (int)(startY - distance) ; k < startY + distance ; k++){
					if (world.getBlock(i, k, j).getLocalizedName().equals(name))
						toReturn.add(Vec3.createVectorHelper(i, k, j));				
				}
			}
		}
		return toReturn;
	}
}