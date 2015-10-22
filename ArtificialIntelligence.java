package com.custommods.ai;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.minecart.MinecartEvent;

public class ArtificialIntelligence{
	private EntityPlayer player;
	private AIinventory inventory;
	
	
	///Constructor
	public ArtificialIntelligence(EntityPlayer player){
		this.player = player;
		this.inventory = new AIinventory(player.inventory);
	}
	
	public void invtTest(){
		MovingObjectPosition goalMovObj = player.rayTrace(100, 1);
		Vec3 lookVec = player.getLookVec();
		System.out.println(lookVec);
		//MovingObjectPosition mop =  Minecraft.getMinecraft().theWorld.rayTraceBlocks(posVec, lookVec);
		Block block = Minecraft.getMinecraft().theWorld.getBlock(goalMovObj.blockX, goalMovObj.blockY, goalMovObj.blockZ);
		int id = Block.getIdFromBlock(block);
		Item  item = block.getItem(Minecraft.getMinecraft().theWorld, goalMovObj.blockX, goalMovObj.blockY, goalMovObj.blockZ); 
		inventory.test(block,item);
	}
	
	
}