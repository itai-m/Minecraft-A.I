package com.custommods.walkmod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class PlayerTickHandler {

	private final double SPEED_SIZE = 0.21585;
	
	// Called whenever the player is updated or ticked.
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		if (WalkMod.isWalk) {
			Vec3 lookVec = player.getLookVec();
			Vec3 direction = 
					Vec3.createVectorHelper(lookVec.xCoord, 0, lookVec.zCoord);
			direction = direction.normalize();
			direction = Vec3.createVectorHelper(
					direction.xCoord*SPEED_SIZE,
					direction.yCoord*SPEED_SIZE,
					direction.zCoord*SPEED_SIZE);
			player.setVelocity(direction.xCoord, player.motionY, direction.zCoord);
		}
		
	}


}
