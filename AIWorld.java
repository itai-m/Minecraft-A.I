package com.custommods.ai;

import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class AIWorld{
	
	private World world;
	
	///Constructor
	public AIWorld(World world){
		this.world = world;
	}
	
	///Return the nearest block
	public Vec3 findNearestBlock(){
		return null;
	}

	///Get the world
	public World getWorld() {
		return world;
	}

	///Set the world
	public void setWorld(World world) {
		this.world = world;
	}
}