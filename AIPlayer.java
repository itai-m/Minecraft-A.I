package com.custommods.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ChatComponentText;
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
	
	///Harvest a Block by the player
	private boolean harvestBlock(String blockName, AIWorld world){
		Vec3 des = world.findNearestBlock(player.getPosition(0), blockName, UserSetting.BLOCK_SEARCH_SIZE);
		if (des == null){
			return false;
		}
		else{
			return true;
		}
	}
		
	///Place a block in the world
	public boolean placeBlock(AIWorld world, Vec3 place){
		return world.getWorld().canMineBlock(player, (int)place.xCoord, (int)place.yCoord, (int)place.zCoord);
	}
	
	///Get the Vec3 of the position of the eyes
	public Vec3 eyesLoc(){
		return player.getPosition(0).addVector(0, player.getEyeHeight(), 0);
	}
	
	///Find the point that the player look it
	public Vec3 getLookPoint(AIWorld world){
		Vec3 loc = eyesLoc();
		Vec3 look = player.getLookVec();
		while (world.isBlockAir(loc)){
			loc = loc.addVector(look.xCoord, look.yCoord, look.zCoord);
		}
		return loc;
	}
	
	///Find the location of the empty block that the player look at
	public Vec3 getLookEmptyBlock(AIWorld world){
		Vec3 loc = eyesLoc();
		Vec3 look = player.getLookVec();
		while (world.isBlockAir(loc)){
			loc = loc.addVector(look.xCoord, look.yCoord, look.zCoord);
		}
		loc = loc.addVector(-look.xCoord/2, -look.yCoord/2, -look.zCoord/2);
		if (!world.isBlockAir(loc)){
			loc = loc.addVector(-look.xCoord/2, -look.yCoord/2, -look.zCoord/2);
		}
		return loc;
	}
	
	///Get the location of the player
	public Vec3 getLocation(){
		return player.getPosition(0);
	}
	
	///Move the player to a point in the world
	public void moveToPoint(Vec3 loc){
		
	}
	
	///Craft an item
	public boolean craftItem (AIinventory inve, ItemStack item, AIWorld world){
		if (!Objective.canCraft(item, inve)){
			return false;
		}
		if (!Objective.blockNearPlayer(this, world, Block.getBlockFromName("crafting_table"))){
			//TODO: need to go to the crafting_table
		}
		return inve.craftItem(item);
	}
	
	///Send a message to the player
	public void sendMessage(String msg){
		player.addChatComponentMessage(new ChatComponentText(msg));
	}
	
	///Smelt an item
	public boolean smeltItem (AIinventory inve, ItemStack item, AIWorld world){
		String furnacerBlock = "furnace";
		ItemStack inge;
		if (((inge = RecipesList.getSmeltingItem(item)) == null) || (inve.haveItem(inge))){
			return false;
		}
		if (!Objective.blockNearPlayer(this, world, Block.getBlockFromName(furnacerBlock))){
			
		}
		Vec3 furnaceLoc = world.findNearestBlock(getLocation(), Block.getBlockFromName(furnacerBlock), (int)UserSetting.rechDistance);
		inve.decItem(inge);
		TileEntityFurnace furnaceEntity = world.getFurnaceEntity(furnaceLoc);
		/*System.out.println(furnaceEntity.getInventoryName());
		System.out.println(furnaceEntity.getSizeInventory());
		System.out.println(furnaceEntity.getDistanceFrom(getLocation().xCoord, getLocation().yCoord, getLocation().zCoord));
		System.out.println(furnaceEntity.getStackInSlot(0));
		System.out.println(furnaceEntity.getStackInSlot(1));
		System.out.println(furnaceEntity.getStackInSlot(2));
		System.out.println(furnaceEntity.getInventoryStackLimit());
		furnaceEntity.setInventorySlotContents(0, new ItemStack(Block.getBlockById(1)));
		furnaceEntity.setInventorySlotContents(1, new ItemStack(Block.getBlockById(2)));
		furnaceEntity.setInventorySlotContents(2, new ItemStack(Block.getBlockById(3)));*/
		
		
		
		return true;
	}
}
