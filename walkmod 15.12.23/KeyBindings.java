package com.custommods.walkmod;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;

public class KeyBindings {

    // Declare two KeyBindings, ping and pong
    public static KeyBinding ping;
    public static KeyBinding pong;
    public static KeyBinding keyU;
    public static KeyBinding keyJ;
    
    public static void init() {
        // Define the "ping" binding, with (unlocalized) name "key.ping" and
        // the category with (unlocalized) name "key.categories.mymod" and
        // key code 24 ("O", LWJGL constant: Keyboard.KEY_O)
        ping = new KeyBinding("key.ping", Keyboard.KEY_O, "key.categories.mymod");
        pong = new KeyBinding("key.pong", Keyboard.KEY_L, "key.categories.mymod");
        
        keyU = new KeyBinding("key.U", Keyboard.KEY_U, "key.categories.mymod");
        keyJ = new KeyBinding("key.J", Keyboard.KEY_J, "key.categories.mymod");
        
        // Register both KeyBindings to the ClientRegistry
        ClientRegistry.registerKeyBinding(ping);
        ClientRegistry.registerKeyBinding(pong);
        ClientRegistry.registerKeyBinding(keyU);
        ClientRegistry.registerKeyBinding(keyJ);
    }

}
