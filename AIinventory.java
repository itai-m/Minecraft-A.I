package com.custommods.ai;

import java.util.List;

import org.lwjgl.Sys;

import com.sun.org.apache.bcel.internal.generic.INEG;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;

public class AIinventory{
	
	public final static int PICKAXE = 1;
	public final static int AXE = 2;
	public final static int SHOVEL = 3;
	public final static int SWORD = 4;
	public final static int HOE = 5;
	
	public final static int NOT_FOUND = -1;
	
	public final static int WOOD = 101;
	public final static int IRON = 102;
	public final static int STONE = 103;
	public final static int DIAMOND = 104;
	public final static int GOLD = 105;
	

	
	private InventoryPlayer inventory;
	
	///Constructor
	public AIinventory(InventoryPlayer inventory){
		this.inventory = inventory;
	}
	
	///Copy Constructor
	public AIinventory(AIinventory inve){
		this.inventory.copyInventory(inve.getInventory());
	}
	
	///Get inventory
	public InventoryPlayer getInventory() {
		return inventory;
	}

	///Set inventory
	public void setInventory(InventoryPlayer inventory) {
		this.inventory = inventory;
	}

	///Check if in the inventory have the item
	public boolean haveItem(ItemStack item){
		if (!inventory.hasItem(item.getItem())){
			return false;
		}
		int size = 0;
		int itemID = Item.getIdFromItem(item.getItem());
		for (int i = 0 ; i < inventory.mainInventory.length ; i++){
			if (inventory.mainInventory[i] != null){
				if ( Item.getIdFromItem(inventory.mainInventory[i].getItem()) == itemID){
					size += inventory.mainInventory[i].stackSize;
				}
			}
		}
		if (size >= item.stackSize){
			return true;
		}
		else{
			return false;
		}
	}
	
	///Consume one item form the inventory, return true if the item was exist, otherwise false
	public boolean decItem(int itemId){
		return inventory.consumeInventoryItem(new Item().getItemById(itemId));
	}
	
	///Consume one item form the inventory, return true if the item was exist, otherwise false
	public boolean decItem(Item item){
		return inventory.consumeInventoryItem(item);
	}
	
	///Consume one item form the inventory, return true if the item was exist, otherwise false
	public boolean decItem(ItemStack itemStack){
		int stack = itemStack.stackSize;
		int id = Item.getIdFromItem(itemStack.getItem());
		for (int i = 0 ; i < inventory.mainInventory.length ; i++){
			if (inventory.mainInventory[i] != null){
				if (Item.getIdFromItem(inventory.mainInventory[i].getItem()) == id){
					if (inventory.mainInventory[i].stackSize < stack){
						stack -= inventory.mainInventory[i].stackSize;
						inventory.mainInventory[i] = null;
					}
					else{
						if (inventory.mainInventory[i].stackSize == stack){
							inventory.mainInventory[i] = null;
						}
						else{
							inventory.mainInventory[i].stackSize -= stack;
						}
						return true;
					}
				}
			}
		}
		return false;
	}
	
	///Add an stack item to the inventory, return true if can add the item, otherwise false
	public boolean addItem(ItemStack itemStack){
		return (inventory.addItemStackToInventory(itemStack.copy()));
	}

	///Add a block to the inventory, return true if can add the item, otherwise false
	public boolean addItem(Block block){
		return (inventory.addItemStackToInventory(new ItemStack(block)));
	}
	
	///Add an item to the inventory, return true if can add the item, otherwise false
	public boolean addItem(Item item){
		return (inventory.addItemStackToInventory(new ItemStack(item)));
	}
	
	//Return the list of the items in the inventory
	public String toString(){
		String toReturn = "";
		for (int i = 0; i < inventory.mainInventory.length ; i++){
			if (inventory.mainInventory[i] != null)
				toReturn += i + ": " + inventory.mainInventory[i].getDisplayName() + " x " + inventory.mainInventory[i].stackSize + "\n";
		}
		return toReturn;
	}
	
	///Change current item to the item with a giving id, return true if successes otherwise false
	public boolean changeCurrentItemTo(int id){
		int index;
		if ((index = getItemIndex(id)) == NOT_FOUND){
			return false;
		}
		swap(index, inventory.currentItem);
		return true;
	}
	
	///Return the index in the inventory of item with the giving id, if not found return NOT_FOUND
	public int getItemIndex(int id){
		for (int i = 0 ; inventory.mainInventory.length > i ; i++){
			if (inventory.mainInventory[i] != null){
				if (Item.getIdFromItem(inventory.mainInventory[i].getItem()) == id){
					return i;
				}
			}
		}
		return NOT_FOUND;
	}
	
	///Change current item to the item with a giving name, return true if successes otherwise false
	public boolean changeCurrentItemTo(String name){
		int index;
		if ((index = getItemIndex(name)) == NOT_FOUND){
			return false;
		}
		swap(index, inventory.currentItem);
		return true;
	}
	
	///Return the index in the inventory of item with the giving name, if not found return NOT_FOUND
	public int getItemIndex(String name){
		for (int i = 0 ; inventory.mainInventory.length > i ; i++){
			if (inventory.mainInventory[i] != null){
				if (inventory.mainInventory[i].getDisplayName().equals(name)){
					return i;
				}
			}
		}
		return NOT_FOUND;
	}
		
	///Use the best tool
	public void useTool(int toolID){
		switch (toolID) {
		case PICKAXE:
		
			break;
		case AXE:
			
			break;
		case SHOVEL:
			
			break;

		default:
			break;
		}
	}
	
	///Get best pickaxe from inventory, return the kind of the pickaxe, if not found return NOT_FOUND
	public int getPickaxe(){
		if (changeCurrentItemTo("Diamond Pickaxe")){
			return DIAMOND;
		}
		if (changeCurrentItemTo("Iron Pickaxe")){
			return IRON;
		}
		if (changeCurrentItemTo("Stone Pickaxe")){
			return STONE;
		}
		if (changeCurrentItemTo("Golden Pickaxe")){
			return GOLD;
		}
		if (changeCurrentItemTo("Wooden Pickaxe")){
			return WOOD;
		}
		
		return NOT_FOUND;
	}
	
	///Get best shovel from inventory, return the kind of the shovel, if not found return NOT_FOUND
	public int getShovel(){
		if (changeCurrentItemTo("Diamond Shovel")){
			return DIAMOND;
		}
		if (changeCurrentItemTo("Iron Shovel")){
			return IRON;
		}
		if (changeCurrentItemTo("Stone Shovel")){
			return STONE;
		}
		if (changeCurrentItemTo("Golden Shovel")){
			return GOLD;
		}
		if (changeCurrentItemTo("Wooden Shovel")){
			return WOOD;
		}
		
		return NOT_FOUND;
	}
	
	///Get best shovel from inventory, return the kind of the shovel, if not found return NOT_FOUND
	public int getAxe(){
		if (changeCurrentItemTo("Diamond Axe")){
			return DIAMOND;
		}
		if (changeCurrentItemTo("Iron Axe")){
			return IRON;
		}
		if (changeCurrentItemTo("Stone Axe")){
			return STONE;
		}
		if (changeCurrentItemTo("Golden Axe")){
			return GOLD;
		}
		if (changeCurrentItemTo("Wooden Axe")){
			return WOOD;
		}
		
		return NOT_FOUND;
	}
	
	///Get best hoe from inventory, return the kind of the hoe, if not found return NOT_FOUND
	public int getHoe(){
		if (changeCurrentItemTo("Diamond Hoe")){
			return DIAMOND;
		}
		if (changeCurrentItemTo("Iron Hoe")){
			return IRON;
		}
		if (changeCurrentItemTo("Stone Hoe")){
			return STONE;
		}
		if (changeCurrentItemTo("Golden Hoe")){
			return GOLD;
		}
		if (changeCurrentItemTo("Wooden Hoe")){
			return WOOD;
		}
		
		return NOT_FOUND;
	}
	
	///Get best sword from inventory, return the kind of the sword, if not found return NOT_FOUND
	public int getSword(){
		if (changeCurrentItemTo("Diamond Sword")){
			return DIAMOND;
		}
		if (changeCurrentItemTo("Iron Sword")){
			return IRON;
		}
		if (changeCurrentItemTo("Stone Sword")){
			return STONE;
		}
		if (changeCurrentItemTo("Golden Sword")){
			return GOLD;
		}
		if (changeCurrentItemTo("Wooden Sword")){
			return WOOD;
		}
		
		return NOT_FOUND;
	}
		
	///Function for Debug
	public void test(int j){
		System.out.println(this.toString());
		System.out.println(getPickaxe());
	}
	
	///Swap two item in the inventory
	private void swap(int x, int y){
		ItemStack other = inventory.mainInventory[x];
		inventory.mainInventory[x] = inventory.mainInventory[y];
		inventory.mainInventory[y] = other;
	}
	
	///Craft an item in the inventory
	public boolean craftItem(ItemStack item){
		List<ItemStack> ingr = RecipesList.getIngredientList(item);
		if (ingr == null){
			return false;
		}
		if (!Objective.canCraft(item, this)){
			return false;
		}
		for (ItemStack itemStack : ingr) {
			if (haveItem(itemStack)){
				if (!decItem(itemStack)){
					return false;
				}
			}
			else if (Objective.canCraft(itemStack, this)){
				if (!craftItem(itemStack)){
					return false;
				}
			}
			else{
				
			}
		}
		return addItem(RecipesList.getRecipes(item).getRecipeOutput());
	}
	
}