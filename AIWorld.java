package com.custommods.ai;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class AIWorld{
	
	private World world;
	
	///Constructor
	public AIWorld(World world){
		this.world = world;
	}
	
	///Return the nearest block by name
	public Vec3 findNearestBlock(Vec3 startLoc, String Blockname, int max){
		return findNearestBlock(startLoc, Block.getIdFromBlock(Block.getBlockFromName(Blockname)), max);
	}
	
	///Return the nearest block by block
	public Vec3 findNearestBlock(Vec3 startLoc, Block block, int max){
		return findNearestBlock(startLoc, Block.getIdFromBlock(block), max);
	}
	
	///Return the nearest block by ID
	public Vec3 findNearestBlock(Vec3 startLoc, int blockId, int max){
		int x = (int) startLoc.xCoord;
		int y = (int) startLoc.yCoord;
		int z = (int) startLoc.zCoord;
		for (int i = 0 ; i < max ; i++){
			for (int j = -i ; j < i ; j++){
				for (int k = -i ; k < i ; k++){
					if (Block.getIdFromBlock(world.getBlock(x + i, y + k, z + j)) == blockId){
						return Vec3.createVectorHelper(x + i, y + k, z + j);
					}
					if (Block.getIdFromBlock(world.getBlock(x - i, y + k, z + j)) == blockId){
						return Vec3.createVectorHelper(x - i, y + k, z + j);
					}
					if (Block.getIdFromBlock(world.getBlock(x + k, y + i, z + j)) == blockId){
						return Vec3.createVectorHelper(x + k, y + i, z + j);
					}
					if (Block.getIdFromBlock(world.getBlock(x + k, y - i, z + j)) == blockId){
						return Vec3.createVectorHelper(x + k, y - i, z + j);
					}
					if (Block.getIdFromBlock(world.getBlock(x + k, y + j, z + i)) == blockId){
						return Vec3.createVectorHelper(x + k, y + j, z + i);
					}
					if (Block.getIdFromBlock(world.getBlock(x + k, y + j, z - i)) == blockId){
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
	
	///Check if the block is air
	public boolean isBlockAir(Vec3 loc){
		return world.isAirBlock((int)loc.xCoord, (int)loc.yCoord, (int)loc.zCoord);
	}
	
	///Get the id of the block by vector
	public int blockId(Vec3 loc){
		return Block.getIdFromBlock(world.getBlock((int)loc.xCoord, (int)loc.yCoord, (int)loc.zCoord));
	}
	
	///Get the id of the block by integers
	public int blockId(int x, int y, int z){
		return Block.getIdFromBlock(world.getBlock(x, y, z));
	}
	
	///Get the block by vector
	public Block getBlock(Vec3 loc){
		return world.getBlock((int)loc.xCoord, (int)loc.yCoord, (int)loc.zCoord);
	}
	
	///Get the block by integers
	public Block getBlock(int x, int y, int z){
		return world.getBlock(x, y, z);
	}
	
	///Get the TileEntityFurnace form the world
	public TileEntityFurnace getFurnaceEntity(Vec3 loc){
		return (TileEntityFurnace)world.getTileEntity((int)loc.xCoord, (int)loc.yCoord, (int)loc.zCoord);
	}
	
}