package com.custommods.ai;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;

public class UserSetting {

	public static int BLOCK_SEARCH_SIZE = 100;
	public static double rechDistance = Minecraft.getMinecraft().playerController.getBlockReachDistance();
	public static int MaxTickesForHarvest = 200;
	public static int LogBlockId = Block.getIdFromBlock(Block.getBlockFromName("log"));
	public static int AirBlockId = Block.getIdFromBlock(Block.getBlockFromName("air"));
}
