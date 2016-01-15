package com.custommods.ai;

import com.custommods.walkmod.IWorldInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.Sys;

import com.custommods.walkmod.IWorldInfo;
import com.custommods.walkmod.MinecraftWorldInfo;
import com.custommods.walkmod.NeighborCollector;
import com.custommods.walkmod.WalkMod;
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
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraftforge.client.MinecraftForgeClient;
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
	public boolean get(String requset){
		requset = requset.toLowerCase();
		ItemStack item = new ItemStack(Block.getBlockFromName(requset)); 
		Logger.debug(item.getClass().getName());
		if (item.getItem() == null){
			return false;
		}
		return getItem(item);
		
		/*if (requset.equals("pickaxe")){
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
		}*/
	}
	
	///Handle the craft command
	public void craft(String toCraft){
		toCraft = toCraft.toLowerCase();
		if (player.craftItem(inventory, new ItemStack(Block.getBlockFromName(toCraft)), world)){
			player.sendMessage("Successfully Craft " + toCraft);
		}
		else{
			player.sendMessage("Could not craft " + toCraft);
		}
	}
	
	///Handle the smelt command
	public void smelt(String toSmelt){
		toSmelt = toSmelt.toLowerCase();
		if (player.smeltItem(inventory, new ItemStack(Block.getBlockFromName(toSmelt)), world)){
			player.sendMessage("Successfully Smelt " + toSmelt);
		}
		else{
			player.sendMessage("Could not smelt " + toSmelt);
		}
	}
	
	///Handle the if the command not exist
	public void commandNotExist(){
		player.sendMessage("Command not exist");
	}
	
	///Handle the if the command not exist
	public void goToPoint(String strX, String strY, String strZ){
		if (strX == null || strY == null || strZ == null){
			player.sendMessage("Command not enter currctly");
		}
		else{
			Vec3 dest = Vec3.createVectorHelper(Integer.parseInt(strX), Integer.parseInt(strY), Integer.parseInt(strZ));
			if (player.moveToPoint(dest, world)){
				player.sendMessage("Successfully got to " + dest);
			}
			else{
				player.sendMessage("Could not found a path to the destion");
			}
		}
	}
		
	///Function for testing only
	public void test(){
		Logger.debug("Start Test" + WalkMod.pathNavigator.run);
		Logger.debug("get: " + player.getItem(new ItemStack(Item.getItemById(4)), world, inventory));
		Logger.debug("End Test" + WalkMod.pathNavigator.run);
	}
	
	///Get an item
	private boolean getItem(ItemStack item){
		return player.getItem(item, world, inventory);
	}
	
}