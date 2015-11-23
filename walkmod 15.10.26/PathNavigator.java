package com.custommods.walkmod;

import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;

public class PathNavigator {
	
	private static PathNavigator instance;
	
	private final double NORMAL_SPEED_SIZE = 0.21585;
	private final double SNEAK_SPEED_SIZE = NORMAL_SPEED_SIZE / 3.2824;
	//private final double NORMAL_SPEED_SIZE = 0.1;
	private final double WATER_SPEED_SIZE = NORMAL_SPEED_SIZE / 5;
	private final double PLAYER_HEIGHT = 1.62;
	private final double MINIMAL_DISTANCE = 0.15;
	private final double TURNING_DISTANCE = 1;
	
	
	private Queue<Step> steps;
	private Step currentStep;
	private boolean run = false;
	private boolean isJumpedFlag = false;
	private Vec3 targetVec = null;
	private int pollFlag = 0;
	
	private boolean isBridgeStep = false;
	
	static{
		instance = new PathNavigator();
	}
	
	public static PathNavigator getInstance(){
		return instance;
	}
	
	private PathNavigator(){
		this.steps = null;
		this.currentStep = null;
	}
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		//this condition is here in order to avoid this code running twice for each tick
		if (event.phase == TickEvent.Phase.END)
			return;
		if (!run)
			return;
		if (null == currentStep && steps.isEmpty()){
			run = false;
			return;
		}
		if (null == currentStep){
			currentStep = steps.poll();
			targetVec = currentStep.getLocation();
			isJumpedFlag = false;
		}
		
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		
		//integer positions are the corner of the block - in order to get to the center we add 0.5
		Vec3 direction = 
				Vec3.createVectorHelper(targetVec.xCoord + 0.5 - player.posX,
						targetVec.yCoord - player.posY + PLAYER_HEIGHT,
						targetVec.zCoord + 0.5 - player.posZ);
		if (isBridgeStep){
			double xDelta = Math.signum(targetVec.xCoord - currentStep.getParent().getLocation().xCoord);
			double zDelta = Math.signum(targetVec.zCoord - currentStep.getParent().getLocation().zCoord);
			
			direction.xCoord -= xDelta * 0.2;
			direction.zCoord -= zDelta * 0.2;
		}
		//System.out.println((targetVec.xCoord - player.posX) + ", " + (targetVec.yCoord - player.posY) + ", " + (targetVec.zCoord - player.posZ));

		direction.yCoord = 0;

//		Vec3 delta = null;
//		Step nextStep = steps.peek();
//		if(nextStep != null) {
//			Vec3 deltaBeforeNorm = nextStep.getLocation().subtract(targetVec);
//			delta = deltaBeforeNorm.normalize();
//			if(Vec3.createVectorHelper(
//					TARGET_ADVANCE_SPEED*delta.xCoord, 
//					TARGET_ADVANCE_SPEED*delta.yCoord, 
//					TARGET_ADVANCE_SPEED*delta.zCoord).lengthVector() > deltaBeforeNorm.lengthVector())
//				delta = null;
//		}
		
		
//		if (direction.lengthVector() <= MINIMAL_DISTANCE){
		if(
				direction.dotProduct(Vec3.createVectorHelper(player.motionX, 0, player.motionZ)) < 0 &&
				!(currentStep instanceof PoleStep)) {
			if (isBridgeStep){
				Vec3 location = currentStep.getLocation();
				//player.setVelocity(0, player.motionY, 0);
				
				placeBlock(event);
				//player.setPosition(location.xCoord + 0.5, location.yCoord + 2, location.zCoord + 0.5);
				pollStep(player);
				
				return;
			}
			else{
				pollStep(player);
				return;	
			}
		} else if (currentStep instanceof PoleStep){
			if (pollFlag == 0 && !player.onGround)
				return;
			if (player.motionY <= 0.02){
				
				if (pollFlag == 0)
					player.jump();
				pollFlag++;
			}
			if(pollFlag == 2)
			{
				player.rotationPitch = 180;
				placeBlock(event);
				
				pollFlag++;
			}
			
			if (pollFlag >= 3 && player.onGround){
				pollFlag = 0;
				player.rotationPitch = 0;
				
				pollStep(player);
			}
			
			return;
		}
		
		
		direction = direction.normalize();
		double multiplyFactor = (isBridgeStep) ? SNEAK_SPEED_SIZE : NORMAL_SPEED_SIZE;
		direction = Vec3.createVectorHelper(
				direction.xCoord*multiplyFactor,
				direction.yCoord*multiplyFactor,
				direction.zCoord*multiplyFactor);
		player.setVelocity(direction.xCoord, player.motionY, direction.zCoord);
		
		if(!isBridgeStep){
			player.rotationYaw = -(float)(Math.atan2(player.motionX, player.motionZ) * 360 / 2/ Math.PI);
			player.setSneaking(false);
		}
		else{
			player.rotationYaw = -(float)(Math.atan2(player.motionX, player.motionZ) * 360 / 2/ Math.PI);
			player.setSneaking(true);
		}

		
		if(!isJumpedFlag && currentStep instanceof JumpStep  && (player.onGround || player.isInWater())){
				player.jump();
				isJumpedFlag = true;
		}
		
	}
	
	private void placeBlock(TickEvent.PlayerTickEvent event){
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		
		Minecraft mc = Minecraft.getMinecraft();
		World world = mc.getIntegratedServer().worldServerForDimension(mc.thePlayer.dimension);
		
		ItemStack itemStack = null;
		int itemStackSlot = -1;
		InventoryPlayer inventory = event.player.inventory;
		
		for (int i = 0; i < inventory.mainInventory.length; i++){
			ItemStack stack = inventory.mainInventory[i];
			
			if (stack != null && stack.getItem() != null && stack.getItem() instanceof ItemBlock){
				itemStack = stack;
				itemStackSlot = i; 
				break;
			}
			
		}
		
		ItemStack currentStack = inventory.getStackInSlot(inventory.currentItem);
		if (event.side == Side.SERVER){
			event.player.inventory.setInventorySlotContents(inventory.currentItem, itemStack);
			event.player.inventory.setInventorySlotContents(itemStackSlot, currentStack);
		}
		
		player.swingItem();
		
		int x = (int)Math.floor(currentStep.getLocation().xCoord);
		int y = (int)Math.floor(currentStep.getLocation().yCoord) + 1;
		int z = (int)Math.floor(currentStep.getLocation().zCoord);
		
		ItemBlock blockItem = (ItemBlock)itemStack.getItem();
		Block block = blockItem.field_150939_a;
		
		
		world.playSoundEffect(x, y, z, block.stepSound.getBreakSound(), 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
		if (!world.isRemote)
			blockItem.placeBlockAt(itemStack, event.player, world, x, y - 2, z, EnumFacing.UP.ordinal(), 0, 0, 0, itemStack.getItemDamage());//world.getBlockMetadata(x+1, y-2, z)

		if (event.side == Side.SERVER)
			inventory.consumeInventoryItem(blockItem);

		
	}
	
	private void pollStep(EntityPlayer player){
		currentStep = steps.poll();
		if(null != currentStep)
			targetVec = currentStep.getLocation();
		//System.out.println((targetVec.xCoord - player.posX) + ", " + (targetVec.yCoord - player.posY) + ", " + (targetVec.zCoord - player.posZ));
		isJumpedFlag = false;
		if (currentStep instanceof BridgeStep){
			isBridgeStep = true;
			player.rotationPitch = 90;
		}else{
			isBridgeStep = false;
			player.rotationPitch = 0;
		}
		
		player.setVelocity(0, player.motionY, 0);
	}
	public void run(){
		run = true;
	}
	public void pause(){
		run = false;
	}
	public void stop(){
		steps = null;
	}
	public void setStepsQueue(Queue<Step> steps){
		this.steps = steps;
		
		LinkedList<Step> stepsLinkedList = (LinkedList<Step>)steps;  
		
		LineDrawer.getInstance().addStepsToDraw(stepsLinkedList);

	}
	
}
