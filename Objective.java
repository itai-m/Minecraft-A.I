package com.custommods.ai;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;

public class Objective {

	///Check if can Craft an Item
	public static boolean canCraft(ItemStack item, AIinventory inve){
		List<ItemStack> items = RecipesList.getIngredientList(item);
		if (items == null){
			return false;
		}
		for (ItemStack itemStack : items) {
			if (!inve.haveItem(itemStack)){
				return false;
			}
		}
		return true;
	}
	
	///Check if the player can harvest block
	public static boolean canHarvestBlock(AIPlayer player, Block block){
		return player.getPlayer().canHarvestBlock(block);
	}
	
	///Check if the player have the items to make a portal
	public static boolean canMakePortal(Block block, AIinventory inve){
		//check if the player have "Flint and Steel"
		if (!inve.haveItem(new ItemStack(Item.getItemById(259)))){
			return false;
		}
		ItemStack itemToCheck = new ItemStack(block);
		itemToCheck.stackSize = 10;
		return inve.haveItem(itemToCheck);
	}
	
	///Check if the block is in a reach of the player
	public static boolean nearBlock(AIPlayer player, Vec3 blockLoc){
		double distance = blockLoc.distanceTo(player.getLocation());
		if (distance < Minecraft.getMinecraft().playerController.getBlockReachDistance()){
			return true;
		}
		return false;
	}
}
