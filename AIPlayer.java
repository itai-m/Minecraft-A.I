package com.custommods.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.custommods.walkmod.IWorldInfo;
import com.custommods.walkmod.MinecraftWorldInfo;
import com.custommods.walkmod.NeighborCollector;
import com.custommods.walkmod.PathFinder;
import com.custommods.walkmod.PathSmoother;
import com.custommods.walkmod.Step;
import com.custommods.walkmod.WalkMod;
import com.mojang.realmsclient.dto.McoServer.WorldType;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.sun.javafx.event.EventHandlerManager;
import com.sun.xml.internal.ws.api.config.management.policy.ManagementAssertion.Setting;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.minecart.MinecartEvent;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType;
import scala.remote;
import scala.annotation.meta.param;

public class AIPlayer {

	public static int CANT_HARVEST = -1;
	public static int CANT_BREAK = -2;
	
	private EntityPlayer player;
	
	private boolean doneWalk = false;

	
	///Constructor
	public AIPlayer(EntityPlayer player){
		this.player = player;
	}
	
	///Copy Constructor
	public AIPlayer(AIPlayer player){
		this.player.clonePlayer(player.getPlayer(), true);
	}

	///Get the player
	public EntityPlayer getPlayer() {
		return player;
	}

	///Set the player
	public void setPlayer(EntityPlayer player) {
		this.player = player;
	}
	
	///Get entity ID
	public int getEntityID(){
		return player.getEntityId();
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
	
	/*///Harvest a Block by the player
	public boolean harvestBlock(int blockId, AIWorld world){
		int tick = UserSetting.MaxTickesForHarvest;
		Vec3 des = world.findNearestBlock(player.getPosition(0), blockId, UserSetting.BLOCK_SEARCH_SIZE);
		System.out.println(des);
		if (des == null){
			return false;
		}
		if (Objective.blockNearPlayer(this, world, blockId)){
			System.out.println("test");
			while (tick > 0 && !world.isBlockAir(des)){
				Minecraft.getMinecraft().renderEngine.tick();
				System.out.println(tick);
				//Minecraft.getMinecraft().playerController.clickBlock((int)des.xCoord, (int)des.yCoord, (int)des.zCoord, 2);
				Minecraft.getMinecraft().playerController.onPlayerDamageBlock((int)des.xCoord, (int)des.yCoord, (int)des.zCoord, 2);
				player.swingItem();
				tick--;
			}
			//TODO: harvestBlock
			return true;
		}
		else{
			return false;
		}
		
	}*/
		
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
			if (loc.distanceTo(eyesLoc()) > UserSetting.MaxDistanceLook){
				return null;
			}
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
	public boolean moveToPoint(Vec3 dest, AIWorld world){
		Logger.debug("Start the Path Finding");
		PathFinder pathFinder = new PathFinder(getLocation(), dest, world.getWorldInfo());
		Queue<Step> stepsToGoal = pathFinder.findPath();
		if (null == stepsToGoal || stepsToGoal.size() <= 0){
			return false;
		}
		PathSmoother.getInstance().smoothPath(stepsToGoal);
		Logger.debug("Start waking to: " + dest);
		walkOnPath(stepsToGoal);
		return true;
	}
	
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		Logger.debug("tick");
		if (event.side == Side.SERVER){
			Logger.debug("server side");
			doneWalk = false;
		}
		else{
			doneWalk = WalkMod.pathNavigator.isRun();
		}
		Logger.debug("doneWalk: " + doneWalk);
	}
	
	///Do the path
	public void walkOnPath(Queue<Step> steps){
		WalkMod.pathNavigator.setStepsQueue(steps);
		WalkMod.pathNavigator.run();

		while (WalkMod.pathNavigator.isRun()){
			Util.waitAndTick();
			//Logger.debug("" + Minecraft.getMinecraft().getIntegratedServer().getTickCounter() + " " + Minecraft.getMinecraft().getIntegratedServer().isServerRunning());
			//Logger.debug("running");
		}
		for (int i = 0 ; i < 10 ; i++){
			Util.waitAndTick();
		}
		WalkMod.pathNavigator.stop();
		Logger.debug("Finished run");
	}
	
	///Move the player next to a block 
	public boolean standNextTo(Vec3 blockLoc, AIWorld world){
		Vec3 gotoLoc = world.findNearestBlock(blockLoc, UserSetting.AirBlockId, (int)UserSetting.rechDistance);
		if (gotoLoc == null){
			return false;
		}
		moveToPoint(gotoLoc, world);
		return true;
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
		if (!Objective.canSmelt(item, inve)){
			return false;
		}
		if (!Objective.blockNearPlayer(this, world, Block.getBlockFromName(furnacerBlock))){
			//TODO: go to get the furnace
		}
		return inve.smeltItem(item);
		
		/*
		Vec3 furnaceLoc = world.findNearestBlock(getLocation(), Block.getBlockFromName(furnacerBlock), (int)UserSetting.rechDistance);
		inve.decItem(inge);
		TileEntityFurnace furnaceEntity = world.getFurnaceEntity(furnaceLoc);
		
		
		System.out.println(furnaceEntity.getInventoryName());
		System.out.println(furnaceEntity.getSizeInventory());
		System.out.println(furnaceEntity.getDistanceFrom(getLocation().xCoord, getLocation().yCoord, getLocation().zCoord));
		System.out.println(furnaceEntity.getStackInSlot(0));
		System.out.println(furnaceEntity.getStackInSlot(1));
		System.out.println(furnaceEntity.getStackInSlot(2));
		System.out.println(furnaceEntity.getAccessibleSlotsFromSide(0));
		System.out.println(furnaceEntity.getAccessibleSlotsFromSide(1));
		System.out.println(furnaceEntity.getAccessibleSlotsFromSide(2));
		System.out.println(furnaceEntity.getInventoryStackLimit());
		furnaceEntity.setInventorySlotContents(0, new ItemStack(Block.getBlockById(4)));
		furnaceEntity.setInventorySlotContents(1, new ItemStack(Block.getBlockById(5)));
		//furnaceEntity.setInventorySlotContents(2, new ItemStack(Block.getBlockById(3)));
		furnaceEntity.updateContainingBlockInfo();
		furnaceEntity.updateEntity();
		System.out.println(furnaceEntity.isUseableByPlayer(player));
		furnaceEntity.validate();*/
		
		
		
		//return true;
	}
	
	///Return the tool type
	private int getToolType(ItemStack item){
		if (item.getItem() instanceof ItemPickaxe){
			return AIinventory.PICKAXE;
		}
		else if (item.getItem() instanceof ItemAxe){
			return AIinventory.AXE;
		}
		else if (item.getItem() instanceof ItemSpade){
			return AIinventory.SHOVEL;
		}
		else{
			return AIinventory.NOT_FOUND;
		}
	}
	
	///Get an item
	public boolean getItem(ItemStack item, AIWorld world, AIinventory inve){
		InventoryTree inveTree = new InventoryTree(new ItemStack(Item.getItemById(Util.EMPTY_ID)), 0);
		WorkPlan plan = new WorkPlan();
		plan.addLoc(getLocation());
		if (planTree (item, world, plan, inve, inveTree) == Util.Max){
			return false;
		}
		Logger.debug(plan.toString());
		return doWorkPlan(plan, inve, world);
	}
	
	///The tree  of the plan
	private double planTree(ItemStack item,AIWorld world, WorkPlan plan, AIinventory inve, InventoryTree inveTree){
		List<ItemStack> craftInger = RecipesList.getIngredientList(item);
		ItemStack smeltInger = RecipesList.getSmeltingItem(item);
		double craftHeur = 0;
		double goGetHeur = 0;
		ItemStack tempTool;
		Vec3[] blocksLoc = null;
		int gotoNum = 0;
		Queue<Step> steps = null;
		InventoryTree craftInveTree;
		InventoryTree goInveTree;
		List usedItems = new ArrayList<ItemStack>();
		List allPath = new ArrayList<Queue<Step>>();
		boolean needTool = false;
		int toolKind = -1;
		boolean needToSmelt = false;
		
		//Check if the player already have the item
		if (plan.canUsedItem(item, inve)){
			Logger.debug("PlanTree: allready have " + item.getDisplayName());
			inveTree.AddChild(item, -item.stackSize);
			plan.addUsedItem(item);
			return 0;
		}
		
		//Check if the item can made by melting
		if (smeltInger !=null){
			Logger.debug("PlanTree: can use smetl to get " + item.getDisplayName());
			craftInveTree = inveTree.AddChild(item, 0);
			smeltInger.stackSize = item.stackSize;
			craftHeur = planTree(smeltInger, world, plan, inve, craftInveTree);
			needToSmelt = true;
		}
		
		//Check if the item can made by crafting
		else if (craftInger == null){
			Logger.debug("PlanTree: no craft for " + item.getDisplayName());
			craftHeur = Util.Max;
		}
		else{
			usedItems.clear();
			gotoNum = plan.countLoc();
			craftInveTree = inveTree.AddChild(item, 0);
			for (ItemStack itemStack : craftInger) {
				double tempHeur = planTree(itemStack, world, plan, inve, craftInveTree);
				craftHeur += tempHeur;
				if (tempHeur ==  0){
					usedItems.add(itemStack);
				}
			}
			gotoNum = gotoNum - plan.countLoc();
			for (Object object : usedItems) {
				Logger.debug("remove used: " + ((ItemStack) object).getDisplayName());
				plan.removeUsedItem((ItemStack) object);
			}
		}
		
		//Check if need an tool to mine
		if ((tempTool = Util.getMinToolToCraft(item) ) == null){
			goGetHeur = Util.Max;
		}
		else{
			goInveTree = inveTree.AddChild(item, 0);
			needTool = true;
			if (!Util.idItemEqual(tempTool, Util.getItemStack(Util.EMPTY_ID))){
				Logger.debug("PlanTree: tool need to mine " + item.getDisplayName() + " is: " + tempTool.getDisplayName());
				goGetHeur = planTree( tempTool, world, plan, inve, goInveTree);
				toolKind = getToolType(tempTool);
			}
			else if ((toolKind = Util.toolForItem(item)) == Util.CANT_GET){
				needTool = false;
			}
			
			//Find the nearest blocks of this kind
			blocksLoc = world.findNearestBlocks(plan.peekLoc(), Item.getIdFromItem(item.getItem()), item.stackSize, UserSetting.BLOCK_SEARCH_SIZE, plan.GetLoctionArr());
			
			//Check if there is a block near the player
			if (blocksLoc ==null){
				Logger.debug("PlanTree: there is no block for " + item.getDisplayName());
				goGetHeur = Util.Max;
			}
			else{
				Logger.debug("planTree: findPath lenght: " + blocksLoc.length);
				for (int i = 0 ; i < blocksLoc.length  ; i++){ 
					Logger.debug("planTree: findPath to " + blocksLoc[i]);
				}
				steps = world.findPath(plan.peekLoc(), blocksLoc[0]);
				allPath.add(steps);
				goGetHeur += Util.getHeuristic(steps, inve);
				for (int i = 0 ; i < blocksLoc.length -1 ; i++){
					steps = world.findPath(blocksLoc[i], blocksLoc[i+1]);
					allPath.add(steps);
					goGetHeur += Util.getHeuristic(steps, inve);
				}
			}
		}
		
		Logger.debug(item.getDisplayName() + ": craftHeur: " + craftHeur + " goGetHeur: " + goGetHeur);
		if (craftHeur >= Util.Max && goGetHeur >= Util.Max){
			Logger.debug("planTree: can't find a way to get: " + item.getDisplayName());
			return Util.Max;
		}
		
		//Check if the item need to craft or to get
		if (craftHeur < goGetHeur){
			if (needToSmelt){
				Logger.debug("planTree: need to smelt for: " + item.getDisplayName());
				plan.add(item, WorkPlan.Type.smelt);
			}
			else{
				Logger.debug("planTree: need to craft for: " + item.getDisplayName());
				plan.add(item, WorkPlan.Type.craft);
			}
			for (int i = 0 ; i < gotoNum ; i++){
				plan.removeLoc();
			}
			return craftHeur;
		}
		else{
			Logger.debug("planTree: need to go get: " + item.getDisplayName());
			for (Object object : usedItems) {
				plan.removeLast();
			}
			if (needTool){
				plan.add(toolKind, WorkPlan.Type.tool);
			}
			for (Object object : allPath) {
				plan.add((Queue<Step>)object);
			}
			plan.addLoc(blocksLoc[blocksLoc.length - 1]);
			return goGetHeur;
		}
	}
	
	///Do the workPlan
	private boolean doWorkPlan(WorkPlan plan, AIinventory inve, AIWorld world){
		boolean succeeded = true;
		if (plan.isEmpty()){
			return false;
		}
		while (!plan.isEmpty()){
			WorkPlan.Type type = plan.peekFirstType();
			Object obj = plan.pullFirst();
			if (type == WorkPlan.Type.craft){
				Logger.debug("doWorkPlan: craft - " + ((ItemStack)obj).getDisplayName());
				succeeded = succeeded && craftItem(inve, (ItemStack)obj, world);
				Logger.debug("doWorkPlan: craft: " + ((ItemStack)obj).getDisplayName() + " craft success: " + succeeded);
			}
			else if (obj instanceof Queue){
				Logger.debug("doWorkPlan: goto");
				walkOnPath((Queue<Step>)obj);
			}
			else if (type == WorkPlan.Type.tool){
				Logger.debug("doWorkPlan: use tool: " + obj);
				if ((Integer)obj != AIinventory.NOT_FOUND){
					inve.useTool((Integer)obj);
				}
				else{
					Logger.debug("doWorkPlan: item not found to use.");
				}
			}
			else if (type == WorkPlan.Type.smelt){
				Logger.debug("doWorkPlan: smelt:" + ((ItemStack)obj).getDisplayName() );
				succeeded = succeeded && smeltItem(inve, (ItemStack)obj, world);
				Logger.debug("doWorkPlan: smelt: " + ((ItemStack)obj).getDisplayName() + " smelt success: " + succeeded);
			}
			else{
				Logger.debug("doWorkPlan: not reguzie");
			}
		}
		return succeeded;
	}
}
