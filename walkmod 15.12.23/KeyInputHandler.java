package com.custommods.walkmod;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.Packet;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom.Item;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class KeyInputHandler {
	IWorldInfo worldInfo;
	
	public void init(){
		MinecraftWorldInfo minecraftWorldInfo = MinecraftWorldInfo.getInstance();
		minecraftWorldInfo.init();
		worldInfo = new NeighborCollector(minecraftWorldInfo);
	}
	
	private static void printQueue(Queue<Step> q) {
		if(null == q || q.size() <= 1)
			return;
		System.out.println("queue: ");	
		Step first = q.peek();
		Step curr = first;
		do {
			curr = q.poll();
			System.out.print(curr + ", ");
			q.add(curr);
		} while(q.peek() != first);
		
		System.out.println();
	}
	
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if(null == worldInfo)
			init();
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (KeyBindings.ping.isPressed())
			WalkMod.isWalk = true;
		if (KeyBindings.pong.isPressed()) {
			WalkMod.isWalk = false;
		}
		if (KeyBindings.keyJ.isPressed()){
			Vec3 playerPos = Vec3.createVectorHelper(player.posX ,player.posY - 2, player.posZ);
    		
			playerPos = MinecraftWorldInfo.roundVec(playerPos);
			MovingObjectPosition goalMovObj = player.rayTrace(100,  1);
			
			Vec3 goalPos = Vec3.createVectorHelper(goalMovObj.blockX, goalMovObj.blockY, goalMovObj.blockZ);
			
			Vec3 delta = goalPos.subtract(playerPos);
			delta = delta.subtract(delta.normalize());
			delta.xCoord = Math.round(delta.xCoord);
			delta.yCoord = Math.round(delta.yCoord);
			delta.zCoord = Math.round(delta.zCoord);

			goalPos = playerPos.addVector(delta.xCoord, delta.yCoord, delta.zCoord);
			goalPos = MinecraftWorldInfo.roundVec(goalPos);
			goalPos = MinecraftWorldInfo.checkFirstNotAirBlock(goalPos);
			
			PathFinder pathFinder = new PathFinder(playerPos, goalPos, worldInfo);
			Queue<Step> stepsToGoal = pathFinder.findPath();
			
			printQueue(stepsToGoal);
			if (null != stepsToGoal && stepsToGoal.size() > 0)
				PathSmoother.getInstance().smoothPath(stepsToGoal);
			
			if (null == stepsToGoal)
				return;
			
			for(Step step: stepsToGoal){
				System.out.println("Step. ");
				if (null != step){
					System.err.println("(" + step.getLocation().xCoord + ", " + step.getLocation().yCoord + ", " + step.getLocation().zCoord + ") " + step.getClass().getName() + " " + step.getCost());
					if (step instanceof MineStep){
						LinkedList<MineSpot> mineSpots = ((MineStep)step).getStepsToMine();
						for (MineSpot mineSpot: mineSpots){
							System.err.println("	" + "(" + mineSpot.getLocation().xCoord + ", " + mineSpot.getLocation().yCoord + ", " + mineSpot.getLocation().zCoord + ") " + "MineSpot");
						}
						System.err.println("	" + ((MineStep)step).mineType);
					}
				}
				else
					System.err.println("Unassigned");
			}
			
			WalkMod.pathNavigator.setStepsQueue(stepsToGoal);
			WalkMod.pathNavigator.run();

			
		}
		if (KeyBindings.keyU.isPressed()) {
			if(null == worldInfo)
				init();
			
			NeighborCollector neighborCollector = new NeighborCollector(worldInfo);
			
			Vec3 playerPos = Vec3.createVectorHelper(player.posX,player.posY - 2, player.posZ);
			playerPos = MinecraftWorldInfo.roundVec(playerPos);
			List<Step> neighbors = neighborCollector.getNeighbors(new WalkStep(null, 0, playerPos));
			/*
			
			
			
			
			Minecraft mc = Minecraft.getMinecraft();
			World world = mc.getIntegratedServer().worldServerForDimension(mc.thePlayer.dimension);
		
			mc.theWorld.destroyBlockInWorldPartially(mc.thePlayer.getEntityId(), 0, 75, 0, -1);
			
			
			*/
			
			
			int i = 0;
			for (Step step : neighbors){
				if (null != step)
					System.out.println("(" + step.getLocation().xCoord + ", " + step.getLocation().yCoord + ", " + step.getLocation().zCoord + ") " + step.getClass().getName());
				else
					System.out.println("Unassigned");
			}
			System.out.println();
			
			Minecraft mc = Minecraft.getMinecraft();
			World world = mc.getIntegratedServer().worldServerForDimension(mc.thePlayer.dimension);
			
			int x = (int)Math.floor(player.posX);
			int y = (int)Math.floor(player.posY);
			int z = (int)Math.floor(player.posZ);
			
			
			Minecraft minecraft = Minecraft.getMinecraft();
			World serverWorld = minecraft.getIntegratedServer().worldServerForDimension(player.dimension);
			if (!serverWorld.isRemote)
				serverWorld.destroyBlockInWorldPartially(player.getEntityId(), x, y, z, -1);
			
			/*Block block = world.getBlock(x, y - 2, z);
			block.harvestBlock(serverWorld, player, x, y, z, 0);
			*/

			System.out.println("============================");
		}

	}

	/*
	 * public void moveForwardWithoutStopping() {
	 * 
	 * 
	 * Vec3 lookVec = player.getLookVec(); player.setVelocity(lookVec.xCoord,
	 * lookVec.yCoord, lookVec.zCoord);
	 * 
	 * }
	 */

}