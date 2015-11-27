package com.custommods.ai;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.ShapedRecipes;

public class RecipesList {
	private static List<ShapedRecipes> list = CraftingManager.getInstance().getRecipeList();;
	
	///get the the shape recipe from the list
	public static ShapedRecipes getIngredientsList(ItemStack item){
		for (ShapedRecipes shapedRecipes : list) {
			if (shapedRecipes.getRecipeOutput().isItemEqual(item)){
				return shapedRecipes;
			}
		}
		return null;
	}
}
