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
		Logger.debug("b: " + dest);
		Vec3 newDest = MinecraftWorldInfo.checkFirstNotAirBlock(dest);
		Logger.debug("a: " + newDest + " and dist: " + newDest.distanceTo(dest));
//		if (newDest.distanceTo(dest) != 0){
//			moveToPoint(newDest, world);
//		}
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
		WorkTreePlan workTreePlan = new WorkTreePlan(item.getDisplayName());
		workTreePlan.setWorld(world);
		workTreePlan.init();
		workTreePlan.CheckItems(inve);
		workTreePlan.addLoc(getLocation());
		if (planTree (item, world, inve, workTreePlan) == Util.Max){
			Logger.debug("Cant get this item", Logger.LOG);
			return false;
		}
		Logger.debug(workTreePlan.toString(), Logger.LOG);
		return doWorkTreePlan(workTreePlan, inve, world);
	} 
	
	///The tree  of the plan
	private double planTree(ItemStack item,AIWorld world, AIinventory inve, WorkTreePlan workTreePlan){
		List<ItemStack> craftInger = RecipesList.getIngredientList(item);
		ItemStack smeltInger = RecipesList.getSmeltingItem(item);
		double craftHeur = 0;
		double goGetHeur = 0;
		ItemStack tempTool;
		ItemStack tempBlock;
		Vec3[] blocksLoc = null;
		int gotoNum = 0;
		WorkTreePlan craftTree = new WorkTreePlan(item, WorkTreePlan.Type.craft, workTreePlan);
		WorkTreePlan togoTree = new WorkTreePlan(null, WorkTreePlan.Type.moveTo, workTreePlan);
		WorkTreePlan smeltTree = new WorkTreePlan(item, WorkTreePlan.Type.smeltStart, workTreePlan);
		WorkTreePlan toolTree = new WorkTreePlan(null, WorkTreePlan.Type.tool, togoTree);
		boolean needTool = false;
		int toolKind = -1;
		boolean needToSmelt = false;
		
		int smeltNum;
		int craftNum;
		int togoNum;
		
		//Check if the player already have the item
		if ((item.stackSize = workTreePlan.haveItem(inve, item)) >= 0){
			Logger.debug("PlanTree: allready have " + item.getDisplayName());
			return 0;
		}
		item.stackSize *= -1;
		
		
		
		
		//Check if the item can made by melting
		if (smeltInger !=null){
			smeltNum = workTreePlan.addChild(smeltTree);
			
			//Check if need to craft a furnace
			ItemStack furnance = new ItemStack(Item.getItemById(UserSetting.FurnaceId), 1);
			//craftHeur += planTree(furnance, world, inve, smeltTree);

			Logger.debug("PlanTree: can use smelt to get " + item.getDisplayName());
			smeltInger.stackSize = item.stackSize;
			craftHeur += planTree(smeltInger.copy(), world, inve, smeltTree);
			needToSmelt = true;
			workTreePlan.removeChild(smeltNum);
		}
		
		//Check if the item can made by crafting
		else if (craftInger == null){
			Logger.debug("PlanTree: no craft for " + item.getDisplayName());
			craftHeur = Util.Max;
		}
		else{
			craftNum = workTreePlan.addChild(craftTree);
			gotoNum = workTreePlan.countLoc();
			
			//Check if need to craft a crafting table
			ItemStack craftingTable = new ItemStack(Item.getItemById(UserSetting.CraftingTableId), 1);
			//craftHeur = planTree(craftingTable, world, inve, craftTree);
			
			for (ItemStack itemStack : craftInger) {		
				double tempHeur = planTree(itemStack.copy(), world, inve, craftTree);
				craftHeur += tempHeur;
				workTreePlan.AddUseItem(itemStack, -itemStack.stackSize);
			}
			gotoNum = gotoNum - workTreePlan.countLoc();
			workTreePlan.removeChild(craftNum);
		}
		
		//Check if need an tool to mine
		if ((tempTool = Util.getMinToolToCraft(item) ) == null){
			goGetHeur = Util.Max;
		}
		else{
			togoNum = workTreePlan.addChild(togoTree);
			//Logger.debug("Tool is: " + tempTool.getDisplayName() + " Need Tool: " + Util.idItemEqual(tempTool, Util.getItemStack(Util.EMPTY_ID)) + " " + inve.betterTool(tempTool.copy()), Logger.LOG);
			if (!(Util.idItemEqual(tempTool, Util.getItemStack(Util.EMPTY_ID)) || inve.betterTool(tempTool.copy()))){
				Logger.debug("PlanTree: tool need to mine " + item.getDisplayName() + " is: " + tempTool.getDisplayName());
				togoTree.addChild(toolTree);
				goGetHeur = planTree( tempTool.copy(), world, inve, toolTree);
				toolKind = getToolType(tempTool);
				toolTree.set(toolKind);
				needTool = true;
			}
			else {
				needTool = false;
			}
			//Find the nearest blocks of this kind
			blocksLoc = world.findNearestBlocks(workTreePlan.peekLoc(), Item.getIdFromItem(item.getItem()), item.stackSize, UserSetting.BLOCK_SEARCH_SIZE, workTreePlan.GetLoctionArr());
			
			//Check if there is a block near the player
			if (blocksLoc ==null){
				Logger.debug("PlanTree: there is no block for " + item.getDisplayName());
				goGetHeur = Util.Max;
			}
			else{
				
				//second check for the tool need to the mining
				if (!needTool){
					tempTool = Util.getMinToolToCraft(world.getBlock(blocksLoc[0]));
					if (tempTool.getItem() != null){
						if (!inve.betterTool(tempTool)){
							Logger.debug("PlanTree: second check - tool need to mine " + item.getDisplayName() + " is: " + tempTool.getDisplayName());
							togoTree.addChild(toolTree);
							goGetHeur = planTree( tempTool.copy(), world, inve, toolTree);
							toolTree.set(getToolType(tempTool));
							
						}
					}
				}
				
				Logger.debug("planTree: blocks ammunt: " + blocksLoc.length);
				for (int i = 0 ; i < blocksLoc.length  ; i++){ 
					Logger.debug("planTree: goto block to " + blocksLoc[i]);
				}
				goGetHeur += Util.getHeuristic(workTreePlan.peekLoc(), blocksLoc[0]);
				for (int i = 0 ; i < blocksLoc.length -1 ; i++){
					goGetHeur += Util.getHeuristic(blocksLoc[i], blocksLoc[i+1]);
				}
			}
			workTreePlan.removeChild(togoNum);
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
				workTreePlan.addChild(smeltTree);
				workTreePlan.AddUseItem(smeltInger, -smeltInger.stackSize);
				workTreePlan.AddUseItem(RecipesList.getSmeltingResult(smeltInger), smeltInger.stackSize);
			}
			else{
				Logger.debug("planTree: need to craft for: " + item.getDisplayName());
				workTreePlan.addChild(craftTree);
				ItemStack newItem = item.copy();
				newItem.stackSize = 1;
				workTreePlan.AddUseItem(RecipesList.getRecipes(newItem).getRecipeOutput());
			}
			for (int i = 0 ; i < gotoNum ; i++){
				workTreePlan.removeLoc();
			}
			return craftHeur;
		}
		else{
			Logger.debug("planTree: need to go get: " + item.getDisplayName());
			togoTree.set(blocksLoc);
			workTreePlan.addChild(togoTree);
			workTreePlan.AddUseItem(item, blocksLoc.length);
			workTreePlan.addLoc(blocksLoc[blocksLoc.length - 1]);
			return goGetHeur;
		}
	}
	
	///Do the WorkTreePlan
	private boolean doWorkTreePlan(WorkTreePlan plan, AIinventory inve, AIWorld world){
		boolean succeded = true;
		for (int i = 0; i < plan.childrenLenght() ; i++){
			if (plan.getChild(i) != null){
				succeded = succeded && doWorkTreePlan(plan.getChild(i), inve, world);
			}
		}
		succeded = succeded && doNodeInWorkTree(plan.getTodo(), plan.getType(), inve, world);
		return succeded;
	}
	
	///Do one node in the WorkTreePlan
	private boolean doNodeInWorkTree(Object obj, WorkTreePlan.Type type, AIinventory inve, AIWorld world){
		switch (type) {
		case nothing:
			return true;
		case smeltEnd:
			return true;
		case smeltStart:
			Logger.debug("doWorkPlan: smelt:" + ((ItemStack)obj).getDisplayName() );
			return smeltItem(inve, (ItemStack)obj, world);
		case craft:
			Logger.debug("craft - " + ((ItemStack)obj).getDisplayName());
			((ItemStack)obj).stackSize = 1;
			return craftItem(inve, (ItemStack)obj, world);
		case tool:
			Logger.debug("use tool: " + obj, Logger.LOG);
			if ((Integer)obj != AIinventory.NOT_FOUND){
				inve.useTool((Integer)obj);
			}
			return true;
		case moveTo:
			Vec3[] loctaion = (Vec3[])obj;
			boolean succeeded = true;
			for (Vec3 vec3 : loctaion) {
				Logger.debug("Move to: " + vec3, Logger.LOG);
				succeeded = succeeded && moveToPoint(vec3, world);
			}
			return succeeded;
		default:
			break;
		}
		return false;
	}
	
}
