package com.custommods.ai;

import java.util.Queue;

import com.custommods.walkmod.*;
import com.sun.org.apache.xpath.internal.axes.WalkingIteratorSorted;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class Util{
	
	public static int MINE = 1;
	public static int CRAFT = 2;
	public static int CANT_GET = -1;
	public static double Max = Integer.MAX_VALUE;
	public static int EMPTY_ID = UserSetting.AirBlockId;
	
	public static final String PICAXE_NAME = "pickaxe";
	public static final String SHOVEL_NAME = "shovel";
	public static final String AXE_NAME = "axe";
	
	
	///Return the block hardness 
	public static double getBlockHardness(Vec3 blockLoc,Block block, World world){
		return block.getBlockHardness(world, (int)blockLoc.xCoord, (int)blockLoc.yCoord, (int)blockLoc.zCoord);
	}
	
	///Get heuristic from distance
	public static double getHeuristic(Vec3 from, Vec3 to, AIWorld world){
		return world.getMinimalDistance(from, to);
	}
	
	///Check if need better to Craft or to mine an item, return true for craft and false for mine
	public static int CraftOrMine(AIPlayer player, AIWorld world, ItemStack item){
		if (RecipesList.getRecipes(item)==null){
			
		}
		return CANT_GET;
	}
	
	///Calculating the Heuristic from the path
	public static double getHeuristic(Queue<Step> path, AIinventory inv){
		double heur = 1;
		for (Step step : path) {
			if (step instanceof WalkStep){
				heur += 2;
			}
			else if (step instanceof PoleStep){
				heur += 5;
			}
			else if (step instanceof MineStep){
				heur += mineHeuristic((MineStep)step, inv);
			}
			else if (step instanceof JumpStep){
				heur += 5;
			}
			else if (step instanceof BridgeStep){
				heur += 10;
			}
			else{
				
			}
		}
		return heur;
	}
	
	///Calculating the Heuristic of one mine step
	private static double mineHeuristic(MineStep step, AIinventory inv){
		
		return 1;
	}
	
	///Check if one item containing the other
	public static boolean isContain(ItemStack item1, ItemStack item2){
		if (Item.getIdFromItem(item1.getItem()) == Item.getIdFromItem(item2.getItem())){
			return item1.stackSize >= item2.stackSize;
		}
		else{
			return false;
		}
	}
	
	///Check if one item is id equal to other item id
	public static boolean idItemEqual(ItemStack item1, ItemStack item2){
		return (Item.getIdFromItem(item1.getItem()) == Item.getIdFromItem(item2.getItem()));
	}
	
	///Get ItemStack form string (id or name)
	public static ItemStack getItemStack(String name){
		try{
			int id = Integer.parseInt(name);
			return new ItemStack(Item.getItemById(id));
		}
		catch(NumberFormatException e){
			name.toLowerCase();
			Object obj = Item.itemRegistry.getObject(name);
			if (obj instanceof Item){
				return new ItemStack((Item)obj);
			}
			if (obj instanceof ItemBlock){
				return new ItemStack((ItemBlock)obj);
			}
			else{
				Logger.debug("in getItemStack didnt found: " + name);
			}
			return null;
		}
	}
	
	///Get id form string (id or name)
	public static int getIdByName(String name){
		name.toLowerCase();
		Object obj = Item.itemRegistry.getObject(name);
		if (obj instanceof Item){
			return Item.getIdFromItem((Item)obj);
		}
		if (obj instanceof ItemBlock){
			return Item.getIdFromItem(((ItemBlock)obj));
		}
		else{
			Logger.debug("in getIdByName didnt found: " + name);
		}
		return CANT_GET;
	}
	
	///Get ItemStack form id
	public static ItemStack getItemStack(int id){
		return new ItemStack(Item.getItemById(id));
	}
	
	///Wait and tick the game
	public static void waitAndTick(){
		try {
			Thread.sleep(200);
			Minecraft.getMinecraft().getIntegratedServer().tick();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	///Get the min tool to craft
	public static ItemStack getMinToolToCraft(ItemStack item){
		return getMinToolToCraft(Block.getBlockFromItem(item.getItem()));
	}
	
	///Get the min tool to craft
	public static ItemStack getMinToolToCraft(Block blockItem){
		String toolname = "";
		if (blockItem.getMaterial().isToolNotRequired()){
			return new ItemStack(Item.getItemById(EMPTY_ID));
		}
		int toolLevel = blockItem.getHarvestLevel(0);
		switch(toolLevel){
		case(0):
			toolname = "wooden";
			break;
		case(1):
			toolname = "stone";
			break;
		case (2):
			toolname = "iron";
			break;
		case (3):
			toolname = "diamond";
			break;
		default:
			return null;	
		}
		toolname += "_" + blockItem.getHarvestTool(0);
		return getItemStack(toolname);
	}
	
	///Get the tool for the block
	public static int toolForItem(ItemStack item){
		String tool = Block.getBlockFromItem(item.getItem()).getHarvestTool(0);
		if (tool == null){
			return CANT_GET;
		}
		if (tool.compareTo(PICAXE_NAME) == 0){
			return AIinventory.PICKAXE;
		} else if (tool.compareTo(SHOVEL_NAME) == 0){
			return AIinventory.SHOVEL;
		} else if (tool.compareTo(AXE_NAME) == 0){
			return AIinventory.AXE;
		} 
		return CANT_GET;
	}
}