package com.custommods.ai;

import java.util.List;

import net.minecraft.item.ItemStack;

public class Objective {

	///Check if can Craft an Item
	public static boolean canCraft(ItemStack item, AIinventory inve){
		List<ItemStack> items = RecipesList.getIngredientList(item);
		if (item == null){
			return false;
		}
		for (ItemStack itemStack : items) {
			if (!inve.haveItem(itemStack)){
				return false;
			}
		}
		return true;
	}
	
}
