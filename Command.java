package com.custommods.ai;

import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class Command implements ICommand{

	public static final String COMMAND_NAME = "ai"; 
	
	private ArtificialIntelligence ai;
	
	public Command() {
	}
	
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		return COMMAND_NAME;
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "ai <text>";
	}

	@Override
	public List getCommandAliases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] argu) {
		EntityPlayer player;
  
        if(icommandsender instanceof EntityPlayer){
        	if (argu[0].equals("start")){
        		ai = new ArtificialIntelligence((EntityPlayer)icommandsender);
        	}
        	else if (argu[0].equals("test")){
        		System.out.println((int)(argu[1].charAt(0) - '0'));
        		ai.invtTest((int)(argu[1].charAt(0) - '0'));
        	}
        } 
        else {
                return;
        }
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		// TODO Auto-generated method stub
		return false;
	}
	
}