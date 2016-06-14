package com.custommods.ai;

import java.util.List;

import org.lwjgl.Sys;

import com.sun.org.apache.bcel.internal.generic.INEG;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
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
	
	public final static int WOOD = 0;
	public final static int IRON = 2;
	public final static int STONE = 1;
	public final static int DIAMOND = 3;
	public final static int GOLD = 4;
	
	public static final String PICAXE_NAME = Util.PICAXE_NAME;
	public static final String SHOVEL_NAME = Util.SHOVEL_NAME;
	public static final String AXE_NAME = Util.AXE_NAME;
	
	public final int WOODEN_SWORD = 268;
	public final int WOODEN_SHOVEL = 269;
	public final int WOODEN_PICKAXE = 270;
	public final int WOODEN_AXE = 271;
	public final int WOODEN_HOE = 290;
	
	public final int STONE_SWORD = 272;
	public final int STONE_SHOVEL = 273;
	public final int STONE_PICKAXE = 274;
	public final int STONE_AXE = 275;
	public final int STONE_HOE = 291;
	
	public final int IRON_SWORD = 267;
	public final int IRON_SHOVEL = 256;
	public final int IRON_PICKAXE = 257;
	public final int IRON_AXE = 258;
	public final int IRON_HOE = 292;
	
	public final int GOLDEN_SWORD = 283;
	public final int GOLDEN_SHOVEL = 284;
	public final int GOLDEN_PICKAXE = 285;
	public final int GOLDEN_AXE = 286;
	public final int GOLDEN_HOE = 294;
	
	public final int DIAMOND_SWORD = 276;
	public final int DIAMOND_SHOVEL = 277;
	public final int DIAMOND_PICKAXE = 278;
	public final int DIAMOND_AXE = 279;
	public final int DIAMOND_HOE = 293;
	
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

	///Return the number of stack of an item
	public int stackSize(ItemStack item){
		int size = 0;
		int itemID = Item.getIdFromItem(item.getItem());
		for (int i = 0 ; i < inventory.mainInventory.length ; i++){
			if (inventory.mainInventory[i] != null){
				if ( Item.getIdFromItem(inventory.mainInventory[i].getItem()) == itemID){
					size += inventory.mainInventory[i].stackSize;
				}
			}
		}
		return size;
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
	
	///Check if in the inventory have the item by id
	public boolean haveItem(int id){
		return inventory.hasItem(Item.getItemById(id));
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
	public int useTool(int toolID){
		switch (toolID) {
		case PICKAXE:
			return usePickaxe();
		case AXE:
			return useAxe();
		case SHOVEL:
			return useShovel();
		default:
			return NOT_FOUND;
		}
	}
	
	///Remove an item in selected index
	public void removeItem(int index){
		inventory.mainInventory[index] = null;
	}
	
	///Check if its have better tool
	public boolean betterTool(ItemStack item){
		String tool = "";
		int toolLevel, invetoryToolLevel = 0;
		for (String key : item.getItem().getToolClasses(item) ){
			Logger.debug("sssssss: " + key);
			 tool= key;
		}
		toolLevel = item.getItem().getHarvestLevel(item, tool);
		Logger.debug("ssss1ss: " + tool);
		if (tool.contains(PICAXE_NAME)){
			Logger.debug("p");
			invetoryToolLevel = usePickaxe();
		} else if (tool.contains(SHOVEL_NAME)){
			Logger.debug("s");
			invetoryToolLevel= useShovel();
		} else if (tool.contains(AXE_NAME)){
			Logger.debug("a");
			invetoryToolLevel= useAxe();
		} 
		if (getCurrentItem() == null){
			return false;
		}
		Logger.debug("--------Item: " + item.getDisplayName() + " " + toolLevel + " " + invetoryToolLevel, Logger.LOG);
		
		if (toolLevel <= invetoryToolLevel){
			if (getDurability(inventory.currentItem) > item.stackSize){
				return true;
			}
			else{
				ItemStack tempPlace = inventory.getCurrentItem().copy();
				removeItem(inventory.currentItem);
				boolean find = betterTool(item);
				addItem(tempPlace);
				return find;
			}
		}
		
		return false;
	}
	
	///Get the kind of the best pickaxe
	public int getBestPickaxe(){
		if (haveItem(DIAMOND_PICKAXE)){
			return DIAMOND;
		}
		if (haveItem(IRON_PICKAXE)){
			return IRON;
		}
		if (haveItem(STONE_PICKAXE)){
			return STONE;
		}
		if (haveItem(GOLDEN_PICKAXE)){
			return GOLD;
		}
		if (haveItem(WOODEN_PICKAXE)){
			return WOOD;
		}
		return NOT_FOUND;
	}
	
	
	///Use best pickaxe from inventory, return the kind of the pickaxe, if not found return NOT_FOUND
	public int usePickaxe(){
		int pickaxeKind = getBestPickaxe();
		switch (pickaxeKind) {
		case DIAMOND:
			changeCurrentItemTo(DIAMOND_PICKAXE);
			break;
		case IRON:
			changeCurrentItemTo(IRON_PICKAXE);
			break;
		case STONE:
			changeCurrentItemTo(STONE_PICKAXE);
			break;
		case GOLD:
			changeCurrentItemTo(GOLDEN_PICKAXE);
			break;
		case WOOD:
			changeCurrentItemTo(WOODEN_PICKAXE);
			break;
		default:
			break;
		}
		return pickaxeKind;
	}
	
	///Get the kind of the best Shovel
	public int getBestShovel(){
		if (haveItem(DIAMOND_SHOVEL)){
			return DIAMOND;
		}
		if (haveItem(IRON_SHOVEL)){
			return IRON;
		}
		if (haveItem(STONE_SHOVEL)){
			return STONE;
		}
		if (haveItem(GOLDEN_SHOVEL)){
			return GOLD;
		}
		if (haveItem(WOODEN_SHOVEL)){
			return WOOD;
		}
		return NOT_FOUND;
	}
	
	///Use best shovel from inventory, return the kind of the shovel, if not found return NOT_FOUND
	public int useShovel(){
		int kind = getBestShovel();
		switch (kind) {
		case DIAMOND:
			changeCurrentItemTo(DIAMOND_SHOVEL);
			break;
		case IRON:
			changeCurrentItemTo(IRON_SHOVEL);
			break;
		case STONE:
			changeCurrentItemTo(STONE_SHOVEL);
			break;
		case GOLD:
			changeCurrentItemTo(GOLDEN_SHOVEL);
			break;
		case WOOD:
			changeCurrentItemTo(WOODEN_SHOVEL);
			break;
		default:
			break;
		}
		return kind;
	}
	
	
	///Get the kind of the best axe
	public int getBestAxe(){
		if (haveItem(DIAMOND_AXE)){
			return DIAMOND;
		}
		if (haveItem(IRON_AXE)){
			return IRON;
		}
		if (haveItem(STONE_AXE)){
			return STONE;
		}
		if (haveItem(GOLDEN_AXE)){
			return GOLD;
		}
		if (haveItem(WOODEN_AXE)){
			return WOOD;
		}
		return NOT_FOUND;
	}
		
	///Get best axe from inventory, return the kind of the axe, if not found return NOT_FOUND
	public int useAxe(){
		int kind = getBestAxe();
		switch (kind) {
		case DIAMOND:
			changeCurrentItemTo(DIAMOND_AXE);
			break;
		case IRON:
			changeCurrentItemTo(IRON_AXE);
			break;
		case STONE:
			changeCurrentItemTo(STONE_AXE);
			break;
		case GOLD:
			changeCurrentItemTo(GOLDEN_AXE);
			break;
		case WOOD:
			changeCurrentItemTo(WOODEN_AXE);
			break;
		default:
			break;
		}
		return kind;
	}
	
	///Use best hoe from inventory, return the kind of the hoe, if not found return NOT_FOUND
	public int useHoe(){
		if (changeCurrentItemTo(DIAMOND_HOE)){
			return DIAMOND;
		}
		if (changeCurrentItemTo(IRON_HOE)){
			return IRON;
		}
		if (changeCurrentItemTo(STONE_HOE)){
			return STONE;
		}
		if (changeCurrentItemTo(GOLDEN_HOE)){
			return GOLD;
		}
		if (changeCurrentItemTo(WOODEN_HOE)){
			return WOOD;
		}
		
		return NOT_FOUND;
	}
	
	///Use best sword from inventory, return the kind of the sword, if not found return NOT_FOUND
	public int useSword(){
		if (changeCurrentItemTo(DIAMOND_SWORD)){
			return DIAMOND;
		}
		if (changeCurrentItemTo(IRON_SWORD)){
			return IRON;
		}
		if (changeCurrentItemTo(STONE_SWORD)){
			return STONE;
		}
		if (changeCurrentItemTo(GOLDEN_SWORD)){
			return GOLD;
		}
		if (changeCurrentItemTo(WOODEN_SWORD)){
			return WOOD;
		}
		
		return NOT_FOUND;
	}
		
	
	///Swap two item in the inventory
	private void swap(int x, int y){
		if (x != y){
			ItemStack other = inventory.mainInventory[x];
			inventory.mainInventory[x] = inventory.mainInventory[y];
			inventory.mainInventory[y] = other;
		}
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
	
	///Smelt an item with no smelt process
	public boolean smeltItem(ItemStack item){
		if (!Objective.canSmelt(item, this)){
			return false;
		}
		decItem(RecipesList.getSmeltingItem(item));
		return addItem(item);
	}
	
	///Get the current item in use
	public ItemStack getCurrentItem(){
		return inventory.getCurrentItem();
	}
	
	///Get the durability of an item
	public int getDurability(int index){
		ItemStack item = inventory.getStackInSlot(index);
		return item.getMaxDamage() - item.getItemDamage();
	}
	
}