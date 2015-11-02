package com.custommods.ai;


import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.minecart.MinecartEvent;

public class ArtificialIntelligence{
	
	public static int CANT_HARVEST = -1;
	public static int CANT_BREAK = -2;
	
	
	private EntityPlayer player;
	private AIinventory inventory;
	private AIWorld world;
	
	
	///Constructor
	public ArtificialIntelligence(EntityPlayer player){
		this.player = player;
		this.inventory = new AIinventory(player.inventory);
		this.world = new AIWorld(Minecraft.getMinecraft().theWorld);
	}
	
	///Handle the get command
	public void get(String requset){
		requset = requset.toLowerCase();
		if (requset.equals("pickaxe")){
			inventory.getPickaxe();
		}
		else if (requset.equals("axe")){
			inventory.getAxe();
		}
		else if (requset.equals("shovel")){
			inventory.getShovel();
		}
		else if (requset.equals("hoe")){
			inventory.getHoe();
		}
		else if (requset.equals("sword")){
			inventory.getSword();
		}
	}
	
	///Function for testing only
	public void invtTest(){
		ArrayList<Vec3> blocks = world.getBlockList(player.getPosition(1), 10,"Gold Ore");
		for (int i = 0 ; i < blocks.size() ; i++)
			System.out.println(blocks.get(i));
	}
	
	///Get the time to dig the block
	public double getTimeToDig(Vec3 blockLoc,Block block, World world){
		double blockHardness;
		if (player.canHarvestBlock(block)){
			if ((blockHardness = Util.getBlockHardness(blockLoc, block, world)) != -1){
				double strVsBlock = player.getCurrentPlayerStrVsBlock(block, false);
				return (blockHardness / strVsBlock);
			}
			else{
				return CANT_BREAK;
			}
		}
		else{
			return CANT_HARVEST;
		}
	}
	
	
	
}