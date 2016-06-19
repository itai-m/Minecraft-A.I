package com.custommods.ai;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;

public class UserSetting {

	public final static int BLOCK_SEARCH_SIZE = 50;
	public final static double rechDistance = Minecraft.getMinecraft().playerController.getBlockReachDistance();
	public final static int MaxTickesForHarvest = 200;
	public final static int LogBlockId = Block.getIdFromBlock(Block.getBlockFromName("log"));
	public final static int AirBlockId = Block.getIdFromBlock(Block.getBlockFromName("air"));
	public final static int EnchantingTableId = Block.getIdFromBlock(Block.getBlockFromName("enchanting_table"));
	public final static int CraftingTableId = Block.getIdFromBlock(Block.getBlockFromName("crafting_table"));
	public final static int DirtID = Block.getIdFromBlock(Block.getBlockFromName("dirt"));
	public final static int FurnaceId = Block.getIdFromBlock(Block.getBlockFromName("furnace"));
	public final static double MaxDistanceLook = 50;
}
