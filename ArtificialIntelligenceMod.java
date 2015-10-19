package com.custommods.ai;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = ArtificialIntelligenceMod.MODID, version = ArtificialIntelligenceMod.VERSION, name = "A.I Mod")
public class ArtificialIntelligenceMod {
	
	public static final String MODID = "A.I Mod";
    public static final String VERSION = "1.0";
    
    @EventHandler
    public void init(FMLInitializationEvent event){
    	
    }
    
    @EventHandler
    public void serverLoad(FMLServerStartingEvent event)
    {
      event.registerServerCommand(new Command());
    }
}
