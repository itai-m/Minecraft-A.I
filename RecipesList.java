package com.custommods.ai;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.Sys;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeBookCloning;
import net.minecraft.item.crafting.RecipesArmorDyes;
import net.minecraft.item.crafting.RecipesMapExtending;
import net.minecraft.item.crafting.RecipesTools;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipesList {
	private static List list = CraftingManager.getInstance().getRecipeList();
	
	///Get the ShapedRecipes from the list
	public static IRecipe getRecipes(ItemStack item){
		for (Object object : list) {
			if (((IRecipe)object).getRecipeOutput().isItemEqual(item)){
				return ((IRecipe)object);
			}
		}
		return null;
	}
	
	///Get the list of ingredient of the recipe
	public static List<ItemStack> getIngredientList(ItemStack item){
		List<ItemStack> toReturn = new ArrayList<ItemStack>();
		IRecipe irecipe = getRecipes(item);
		if (irecipe == null){
			return null;
		}
		
		if (irecipe instanceof ShapedRecipes){
			ShapedRecipes recipe = (ShapedRecipes) irecipe;
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
		}
		else if (irecipe instanceof ShapedOreRecipe){
			ShapedOreRecipe recipe = (ShapedOreRecipe) irecipe;
			for (int i = 0 ; i < recipe.getInput().length ; i++){
				if (recipe.getInput()[i] instanceof ArrayList){
					ArrayList<ItemStack> itemList = (ArrayList<ItemStack>)recipe.getInput()[i];
					for (ItemStack itemInList : itemList) {
						boolean find = false;
						for (ItemStack itemStack : toReturn) {
							if (itemStack.getItem().equals(itemInList.getItem())){
								find = true;
								itemStack.stackSize++;
							}
						}
						if (!find){
							toReturn.add((ItemStack) itemInList);
						}
					}
						
				}
				else if (toReturn.contains(recipe.getInput()[i])){
					for (ItemStack itemStack : toReturn) {
						if (itemStack.getItem().equals(item.getItem())){
							itemStack.stackSize++;
						}
						
					}
				}
				else{
					toReturn.add((ItemStack) recipe.getInput()[i]);
				}
			}
		}
		
		return toReturn;
	}
}
