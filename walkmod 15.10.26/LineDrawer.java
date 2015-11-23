package com.custommods.walkmod;

import java.util.LinkedList;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;


public class LineDrawer{
	
	private static LineDrawer instance = null;
	
	private LinkedList<Step> stepsToDraw;
	
	public static LineDrawer getInstance(){
		if (instance == null)
			instance = new LineDrawer();
		return instance;
	}
	
	private LineDrawer() {
		stepsToDraw = new LinkedList<Step>();
		return;
	}
	
	//this method adds step to stepsToDraw linkedList, return true if it was added to the list and false if not.
	public boolean addStepToDraw(Step step){
		if (!stepsToDraw.contains(step)){
			stepsToDraw.add(step);
			return true;
		}
		return false;
		
	}
	
	//this method gets linkedList as an argument which is going to clone.
	public void addStepsToDraw(LinkedList<Step> stepsToDraw){
		this.stepsToDraw = (LinkedList<Step>) stepsToDraw.clone();
	}
	
	//this method remove all the steps from stepsToDraw LinkedList
	public void removeAllStepsFromList(){
		stepsToDraw = new LinkedList<Step>();
	}
	
    @SubscribeEvent
    public void renderWorldLastEvent(RenderWorldLastEvent evt)
    {
	    Minecraft mc = Minecraft.getMinecraft();
	    double playerX = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * evt.partialTicks - 0.5;
	    double playerY = mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * evt.partialTicks + 0.12;
	    double playerZ = mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * evt.partialTicks - 0.5;
	
	    GL11.glPushMatrix();
	    GL11.glTranslated(-playerX, -playerY, -playerZ);
	    GL11.glColor3ub((byte)0,(byte)0,(byte) 255);
	    
	    //************TO BE REMOVED****************/
	    float mx = 0;
	    float my = 75;
	    float mz = 0;
	    GL11.glBegin(GL11.GL_LINES);
	    GL11.glVertex3f(mx+0.4f,my,mz+0.4f);
	    GL11.glVertex3f(mx-0.4f,my,mz-0.4f);
	    GL11.glVertex3f(mx+0.4f,my,mz-0.4f);
	    GL11.glVertex3f(mx-0.4f,my,mz+0.4f);
	    
	    playerX = Math.round(playerX);
	    playerY = Math.round(playerY);
	    playerZ = Math.round(playerZ);
	   
	    for(int i = -1; i < 2; i++){
	    	for(int j = -1; j < 2; j++){
	    		if (i == 0 & j == 0)
	    			continue;
	    		if(mc.theWorld.getBlock((int)(playerX + i + 0.4),(int)(playerY - 2),(int)(playerZ + j + 0.4)) instanceof BlockLiquid){
	    			GL11.glVertex3d(playerX + i + 0.4,playerY - 1,playerZ + j + 0.4);
		    	    GL11.glVertex3d(playerX + i - 0.4,playerY - 1,playerZ + j - 0.4);
		    	    GL11.glVertex3d(playerX + i + 0.4,playerY - 1,playerZ + j - 0.4);
		    	    GL11.glVertex3d(playerX + i -0.4,playerY - 1,playerZ + j + 0.4);
	    		}
	    	}
	    }

	    //************/TO BE REMOVED****************/
	    Step parent = null;
	    for (Step step: stepsToDraw){
	    	Vec3 location = step.getLocation();
		    float stepX = (float) location.xCoord;
		    float stepY = (float) location.yCoord + 1;
		    float stepZ = (float) location.zCoord;
		    
		    if (null == parent)
		    {
		    	parent = step;
		    	continue;
		    }
		    location = parent.getLocation();
		    float parentX = (float) location.xCoord;
		    float parentY = (float) location.yCoord + 1;
		    float parentZ = (float) location.zCoord;
		    
		    WalkMod.logger.debug("parentX, parentZ: " + parentX + ", " + parentZ);
		    WalkMod.logger.debug("stepX, StepZ: " + stepX + ", " + stepZ);
		    
		    GL11.glVertex3f(stepX + 0.2f, stepY, stepZ + 0.2f);
		    GL11.glVertex3f(parentX + 0.2f, parentY, parentZ + 0.2f);
		    
		    parent = step;
	    }
	    if (null != parent){
		    Step parentsParent = parent.getParent();
		    GL11.glColor3ub((byte)100,(byte)100,(byte) 100);
		    while (parentsParent != null){
		    	Vec3 location = parentsParent.getLocation();
			    float parentsParentX = (float) location.xCoord;
			    float parentsParentY = (float) location.yCoord + 1;
			    float parentsParentZ = (float) location.zCoord;
			    
			    location = parent.getLocation();
			    float parentX = (float) location.xCoord;
			    float parentY = (float) location.yCoord + 1;
			    float parentZ = (float) location.zCoord;
			    
			    GL11.glVertex3f(parentsParentX - 0.2f, parentsParentY, parentsParentZ - 0.2f);
			    GL11.glVertex3f(parentX - 0.2f, parentY, parentZ - 0.2f);
			    
		    	
		    	parent = parentsParent;
		    	parentsParent = parentsParent.getParent();
		    }
	    }
	    GL11.glEnd();
	    GL11.glPopMatrix();
	   
    }
       
       
}