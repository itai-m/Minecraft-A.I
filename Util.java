package com.custommods.ai;

import java.util.Queue;

import com.custommods.walkmod.*;
import com.sun.org.apache.xpath.internal.axes.WalkingIteratorSorted;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Util{
	
	public static int MINE = 1;
	public static int CRAFT = 2;
	public static int CANT_GET = -1;
	public static double Max = Integer.MAX_VALUE;
	
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
		double heur = 1;
		for (Step step : path) {
			if (step instanceof WalkStep){
				heur += 1;
			}
			else if (step instanceof PoleStep){
				heur += 1;
			}
			else if (step instanceof MineStep){
				heur += 1;
			}
			else if (step instanceof JumpStep){
				heur += 1;
			}
			else if (step instanceof BridgeStep){
				heur += 1;
			}
			else{
				
			}
		}
		return heur;
	}
	
	///Check if one item containing the other
	public static boolean isContain(ItemStack item1, ItemStack item2){
		if (Item.getIdFromItem(item1.getItem()) == Item.getIdFromItem(item2.getItem())){
			if (item1.stackSize >= item2.stackSize){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	///Check if one item is id equal to other item id
	public static boolean idItemEqual(ItemStack item1, ItemStack item2){
		return (Item.getIdFromItem(item1.getItem()) == Item.getIdFromItem(item2.getItem()));
	}
	
	///Get ItemStack for string
	public static ItemStack getItemStack(String name){
		Object obj = Item.itemRegistry.getObject(name);
		if (obj instanceof Item){
			return new ItemStack((Item)obj);
		}
		if (obj instanceof ItemBlock){
			return new ItemStack((ItemBlock)obj);
		}
		else{
			Logger.debug("in getItemStack didnt found: " + name);
		}
		return null;
	}
	
	///Wait and tick the game
	public static void waitAndTick(){
		try {
			Thread.sleep(200);
			Minecraft.getMinecraft().getIntegratedServer().tick();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	///Get the min tool to craft
	public static ItemStack getMinToolToCraft(ItemStack item){
		ItemStack woodenAxe = getItemStack("wooden_axe");
		int harverstLevel = item.getItem().getHarvestLevel(item, "wooden");
		//if (item.getItem().canHarvestBlock(Block.getBlockFromItem(item.getItem()), ))
		Logger.debug("" + harverstLevel);
		return null;
	}
}