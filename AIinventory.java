package com.custommods.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AIinventory{
	
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
	
	///Function for Debug
	public void test(Block block, Item item){
		System.out.println(addItem(block));
		System.out.println(addItem(item));
		
	}
	
}