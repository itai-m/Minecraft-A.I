package com.custommods.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class AIPlayer {

	public static int CANT_HARVEST = -1;
	public static int CANT_BREAK = -2;
	
	private EntityPlayer player;
	
	///Constructor
	public AIPlayer(EntityPlayer player){
		this.player = player;
	}

	///Get the player
	public EntityPlayer getPlayer() {
		return player;
	}

	///Set the player
	public void setPlayer(EntityPlayer player) {
		this.player = player;
	}
	
	///Get the time to dig the block by the player
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
