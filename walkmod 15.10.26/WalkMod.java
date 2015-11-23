package com.custommods.walkmod;

import com.example.examplemod.ExampleMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = WalkMod.MODID, version = WalkMod.VERSION, name = "WalkMOd")
public class WalkMod {
    public static final String MODID = "walkMod";
    public static final String VERSION = "1.0";
    public static PathNavigator pathNavigator;
    public static boolean isWalk = false;
    
	public static final Logger logger = LogManager.getLogger(MODID);
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	KeyBindings key = new KeyBindings();
    	FMLCommonHandler.instance().bus().register(new KeyInputHandler());
    	FMLCommonHandler.instance().bus().register(new PlayerTickHandler());
    	    	
    	pathNavigator = PathNavigator.getInstance();
    	FMLCommonHandler.instance().bus().register(pathNavigator);
    	KeyBindings.init();
    	
    	MinecraftForge.EVENT_BUS.register(LineDrawer.getInstance());
    }
    

    

}
