package com.custommods.ai;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.ShapedRecipes;

public class RecipesList {
	private static List<ShapedRecipes> list = CraftingManager.getInstance().getRecipeList();;
	
	///Get the shape recipe from the list
	public static ShapedRecipes getRecipesList(ItemStack item){
		for (ShapedRecipes shapedRecipes : list) {
			if (shapedRecipes.getRecipeOutput().isItemEqual(item)){
				return shapedRecipes;
			}
		}
		return null;
	}
	
	///Get the list of ingredient of the recipe
	public static List<ItemStack> getIngredientList(ItemStack item){
		List<ItemStack> toReturn = new ArrayList<ItemStack>();
		ShapedRecipes recipe = getRecipesList(item);
		if (recipe == null){
			return null;
		}
		for (int i = 0 ; i < recipe.recipeItems.length ; i++){
			if (toReturn.contains(recipe.recipeItems[i])){
				for (ItemStack itemStack : toReturn) {
					if (itemStack.getItem().equals(item.getItem())){
						itemStack.stackSize++;
					}
					
				}
			}
			else{
				toReturn.add(recipe.recipeItems[i]);
			}
		}
		return toReturn;
	}
}
