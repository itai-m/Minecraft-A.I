package com.custommods.ai;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Util{
	
	
	///Return the block hardness 
	public static double getBlockHardness(Vec3 blockLoc,Block block, World world){
		return block.getBlockHardness(world, (int)blockLoc.xCoord, (int)blockLoc.yCoord, (int)blockLoc.zCoord);
	}
	
	///Get heuristic from distance
	public static double getHeuristic(Vec3 from, Vec3 to){
		return (from.distanceTo(to));
	}
}