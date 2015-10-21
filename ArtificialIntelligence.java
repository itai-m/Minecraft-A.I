package com.custommods.ai;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class ArtificialIntelligence{
	EntityPlayer player;
	AIinventory inventory;
	
	///Constructor
	public ArtificialIntelligence(EntityPlayer player){
		this.player = player;
		this.inventory = new AIinventory(player.inventory);
	}
	
	public void invtTest(){
		inventory.test();
	}
	
	
}