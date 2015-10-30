package com.custommods.ai;

import org.lwjgl.Sys;

import com.sun.org.apache.bcel.internal.generic.INEG;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AIinventory{
	
	public final static int PICKAXE = 1;
	public final static int AXE = 2;
	public final static int SHOVEL = 3;
	public final static int NOT_FOUND = -1;
	
	private InventoryPlayer inventory;
	
	///Constructor
	public AIinventory(InventoryPlayer inventory){
		this.inventory = inventory;
	}
	
	///Consume one item form the inventory, return true if the item was exist, otherwise false
	public boolean decItem(int itemId){
		return inventory.consumeInventoryItem(new Item().getItemById(itemId));
	}
	
	///Consume one item form the inventory, return true if the item was exist, otherwise false
	public boolean decItem(Item item){
		return inventory.consumeInventoryItem(item);
	}
	
	///Add an stack item to the inventory, return true if can add the item, otherwise false
	public boolean addItem(ItemStack itemStack){
		return (inventory.addItemStackToInventory(itemStack));
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
				toReturn += i + ": " + inventory.mainInventory[i].getDisplayName() + "x " + inventory.mainInventory[i].stackSize + "\n";
		}
		return toReturn;
	}
	
	///Change current item to the item with a giving id, return true if successes otherwise false
	private boolean changeCurrentItemTo(int id){
		int index;
		if ((index = getItemIndex(id)) == NOT_FOUND){
			return false;
		}
		ItemStack other = inventory.mainInventory[index];
		inventory.mainInventory[index] = inventory.mainInventory[inventory.currentItem];
		inventory.mainInventory[inventory.currentItem] = other;
		return true;
	}
	
	///Return the index in the inventory of item with the giving id, return -1 if the item dont found
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
	
	///Function for Debug
	public void test(int j){
		System.out.println(this.toString());
		System.out.println(changeCurrentItemTo(3));
	}
	
}