package com.custommods.ai;

import net.minecraft.block.Block;
import net.minecraft.item.ItemTool;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Util{
	
	public static double getTimeForDig(ItemTool tool, Vec3 blockLoc,Block block, World world){
		return block.getBlockHardness(world, (int)blockLoc.xCoord, (int)blockLoc.yCoord, (int)blockLoc.zCoord);
	}
}