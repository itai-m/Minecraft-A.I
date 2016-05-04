package com.custommods.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.Sys;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
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
	private static Map smeltingList = FurnaceRecipes.smelting().getSmeltingList();
	
	///Get the ShapedRecipes from the list
	public static IRecipe getRecipes(ItemStack item){
		for (Object object : list) {
			if (((IRecipe)object).getRecipeOutput()!=null){
				if (Util.isContain(((IRecipe)object).getRecipeOutput(),item)){
					return ((IRecipe)object);
				}
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
					toReturn.add(recipe.recipeItems[i].copy());
				}
			}
		}
		else if (irecipe instanceof ShapedOreRecipe){
			ShapedOreRecipe recipe = (ShapedOreRecipe) irecipe;
			for (int i = 0 ; i < recipe.getInput().length ; i++){
				if (recipe.getInput()[i] == null){
				
				}
				else if (recipe.getInput()[i] instanceof ArrayList){
					ArrayList<ItemStack> itemList = (ArrayList<ItemStack>)recipe.getInput()[i];
					for (ItemStack itemInList : itemList) {
						if (itemInList !=null){
							boolean find = false;
							for (ItemStack itemStack : toReturn) {
								if (itemStack.getItem().equals(itemInList.getItem())){
									find = true;
									itemStack.stackSize++;
								}
							}
							if (!find){
								toReturn.add((ItemStack) itemInList.copy());
							}
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
					toReturn.add((ItemStack)(recipe.getInput()[i]));
				}
			}
		}
		
		return toReturn;
	}
	
	///Get a item for the smelting
	public static ItemStack getSmeltingItem(ItemStack outPut){
		Iterator iterator = smeltingList.entrySet().iterator();
        Entry entry = null;

        do
        {
            if (!iterator.hasNext())
            {
                return null;
            }

            if (entry != null){
            	//Logger.debug("key: " + ((ItemStack)entry.getKey()).getDisplayName());
            	//Logger.debug(((ItemStack)entry.getValue()).getDisplayName());
            	//Logger.debug(outPut.getDisplayName());
            }
            entry = (Entry)iterator.next();
        }
        while (!Util.idItemEqual(outPut, (ItemStack)entry.getValue()));
        //while (Item.getIdFromItem(((ItemStack)entry.getKey()).getItem()) == Item.getIdFromItem(input.getItem()));
        
        return (ItemStack)entry.getKey();
	}
	
}
