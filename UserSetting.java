package com.custommods.ai;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;

public class UserSetting {

	public static int BLOCK_SEARCH_SIZE = 50;
	public static double rechDistance = Minecraft.getMinecraft().playerController.getBlockReachDistance();
	public static int MaxTickesForHarvest = 200;
	public static int LogBlockId = Block.getIdFromBlock(Block.getBlockFromName("log"));
	public static int AirBlockId = Block.getIdFromBlock(Block.getBlockFromName("air"));
	public static int EnchantingTableId = Block.getIdFromBlock(Block.getBlockFromName("enchanting_table"));
	public static int CraftingTableId = Block.getIdFromBlock(Block.getBlockFromName("crafting_table"));
	public static int FurnaceId = Block.getIdFromBlock(Block.getBlockFromName("furnace"));
	public static double MaxDistanceLook = 40;
}
