package com.custommods.ai;

import java.util.Queue;

import com.custommods.walkmod.*;
import com.sun.org.apache.xpath.internal.axes.WalkingIteratorSorted;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Util{
	
	public static int MINE = 1;
	public static int CRAFT = 2;
	public static int CANT_GET = -1;
	
	///Return the block hardness 
	public static double getBlockHardness(Vec3 blockLoc,Block block, World world){
		return block.getBlockHardness(world, (int)blockLoc.xCoord, (int)blockLoc.yCoord, (int)blockLoc.zCoord);
	}
	
	///Get heuristic from distance
	public static double getHeuristic(Vec3 from, Vec3 to){
		return (from.distanceTo(to));
	}
	
	///Check if need better to Craft or to mine an item, return true for craft and false for mine
	public static int CraftOrMine(AIPlayer player, AIWorld world, ItemStack item){
		if (RecipesList.getRecipes(item)==null){
			
		}
		return CANT_GET;
	}
	
	///Calculating the Heuristic from the path
	public static double getHeuristic(Queue<Step> path){
		double heur = 0;
		for (Step step : path) {
			if (step instanceof WalkStep){
				
			}
			else if (step instanceof PoleStep){
				
			}
			else if (step instanceof MineStep){
				
			}
			else if (step instanceof JumpStep){
				
			}
			else if (step instanceof BridgeStep){
				
			}
			else{
				
			}
		}
		return heur;
	}
	
	///The tree of chois
	public static double tree(ItemStack item){
		if (RecipesList.getRecipes(item)==null){
			
		}
		return 0;
	}
}