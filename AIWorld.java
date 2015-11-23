package com.custommods.ai;

import java.util.ArrayList;

import net.minecraft.stats.StatBase;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class AIWorld{
	
	private World world;
	
	///Constructor
	public AIWorld(World world){
		this.world = world;
	}
	
	///Return the nearest block
	public Vec3 findNearestBlock(Vec3 startLoc, String Blockname, int max){
		int x = (int) startLoc.xCoord;
		int y = (int) startLoc.yCoord;
		int z = (int) startLoc.zCoord;
		for (int i = 0 ; i < max ; i++){
			for (int j = -i ; j < i ; j++){
				for (int k = -i ; k < i ; k++){
					if (world.getBlock(x + i, y + k, z + j).getLocalizedName().equals(Blockname)){
						return Vec3.createVectorHelper(x + i, y + k, z + j);
					}
					if (world.getBlock(x - i, y + k, z + j).getLocalizedName().equals(Blockname)){
						return Vec3.createVectorHelper(x - i, y + k, z + j);
					}
					if (world.getBlock(x + k, y + i, z + j).getLocalizedName().equals(Blockname)){
						return Vec3.createVectorHelper(x + k, y + i, z + j);
					}
					if (world.getBlock(x + k, y - i, z + j).getLocalizedName().equals(Blockname)){
						return Vec3.createVectorHelper(x + k, y - i, z + j);
					}
					if (world.getBlock(x + k, y + j, z + i).getLocalizedName().equals(Blockname)){
						return Vec3.createVectorHelper(x + k, y + j, z + i);
					}
					if (world.getBlock(x + k, y + j, z - i).getLocalizedName().equals(Blockname)){
						return Vec3.createVectorHelper(x + k, y + j, z - i);
					}
				}
			}
		}
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