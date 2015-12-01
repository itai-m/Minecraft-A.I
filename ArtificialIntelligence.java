package com.custommods.ai;


import java.util.ArrayList;
import java.util.List;

import org.lwjgl.Sys;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameData.GameDataSnapshot;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.minecart.MinecartEvent;
import tv.twitch.broadcast.GameInfo;
import tv.twitch.broadcast.GameInfoList;

public class ArtificialIntelligence{
	
	
	
	
	private AIPlayer player;
	private AIinventory inventory;
	private AIWorld world;
	
	
	///Constructor
	public ArtificialIntelligence(EntityPlayer player){
		this.player = new AIPlayer(player);
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
	public void test(){
		System.out.println(Objective.canCraft(new ItemStack(Item.getItemById(268)), inventory));		
	}
	
	
	///Harvest a Block by the player
	private boolean harvestBlock(String blockName){
		Vec3 des = world.findNearestBlock(player.getPlayer().getPosition(0), blockName, UserSetting.BLOCK_SEARCH_SIZE);
		if (des == null){
			return false;
		}
		else{
			return true;
		}
	}
	
	
}