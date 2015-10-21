package com.custommods.ai;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class AIinventory{
	
	InventoryPlayer inventory;
	
	///Constructor
	public AIinventory(InventoryPlayer inventory){
		this.inventory = inventory;
	}
	
	public void test(){
		System.out.println(inventory.getCurrentItem().getDisplayName());
	}
	
}