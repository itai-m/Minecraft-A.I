package com.custommods.walkmod;

import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class PathNavigator {
	
	private static PathNavigator instance;
	
	private final double NORMAL_SPEED_SIZE = 0.21585;
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
		if(direction.dotProduct(Vec3.createVectorHelper(player.motionX, 0, player.motionZ)) < 0 && Step.StepType.Pole != currentStep.getType()) {
			currentStep = steps.poll();
			if(null != currentStep)
				targetVec = currentStep.getLocation();
			//System.out.println((targetVec.xCoord - player.posX) + ", " + (targetVec.yCoord - player.posY) + ", " + (targetVec.zCoord - player.posZ));
			isJumpedFlag = false;
			player.setVelocity(0, 0, 0);
			return;
		} else if (Step.StepType.Pole == currentStep.getType()){
			if (player.motionY <= 0.02){
				if (pollFlag == 0)
					player.jump();
				pollFlag++;
			}
			if(pollFlag == 2)
			{
				Minecraft mc = Minecraft.getMinecraft();
				World world = mc.getIntegratedServer().worldServerForDimension(mc.thePlayer.dimension);
				
				ItemStack itemStack = player.getHeldItem();
				
				player.rotationPitch = 180;
				player.swingItem();
				
				int x = (int)Math.floor(player.posX);
				int y = (int)Math.floor(player.posY);
				int z = (int)Math.floor(player.posZ);
				ItemBlock blockItem = (ItemBlock)player.getHeldItem().getItem();
				Block block = blockItem.field_150939_a;
				
				world.playSoundEffect(x, y, z, block.stepSound.getBreakSound(), 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
				
				blockItem.placeBlockAt(itemStack, player, world, x, y - 2, z, EnumFacing.UP.ordinal(), 0, 0, 0, player.getHeldItem().getItemDamage());//world.getBlockMetadata(x+1, y-2, z)
				player.getHeldItem().splitStack(1);
				pollFlag++;
			}
			
			if (pollFlag >= 3 && player.onGround){
				pollFlag = 0;
				player.rotationPitch = 0;
				
				currentStep = steps.poll();
			}
			
			return;
		}
		
		

		
		//		else if (direction.lengthVector() < TURNING_DISTANCE) {
//			if(delta != null) {
//				targetVec = targetVec.addVector(
//						TARGET_ADVANCE_SPEED*delta.xCoord, 
//						TARGET_ADVANCE_SPEED*delta.yCoord, 
//						TARGET_ADVANCE_SPEED*delta.zCoord); 
//			}
//		}
		//Since we set yCoord to 0, it checks only if the length in x and z is samller than MINIMAL_DISTANCE, 
		// to make sure that the player won't "shake" during the fall
		

		direction = direction.normalize();
		double multiplyFactor = (player.isInWater()) ? WATER_SPEED_SIZE : NORMAL_SPEED_SIZE;
		direction = Vec3.createVectorHelper(
				direction.xCoord*multiplyFactor,
				direction.yCoord*multiplyFactor,
				direction.zCoord*multiplyFactor);
		player.setVelocity(direction.xCoord, player.motionY, direction.zCoord);
		player.rotationYaw = -(float)(Math.atan2(player.motionX, player.motionZ) * 360 / 2/ Math.PI);
		
		if(!isJumpedFlag && Step.StepType.Jump == currentStep.getType()  && (player.onGround || player.isInWater())){ 
			player.jump();
			isJumpedFlag = true;
		}
		
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
