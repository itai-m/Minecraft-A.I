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
        	String argu0 = argu[0].toLowerCase();
        	if (argu0.equals("start")){
        		ai = new ArtificialIntelligence((EntityPlayer)icommandsender);
        	}
        	else if (ai == null){
        		Logger.debug("Need to start the mod");
        	}
        	else if (argu0.equals("get")){
        		ai.get(argu[1]);
        	}
        	else if (argu0.equals("craft")){
        		ai.craft(argu[1]);
        	}
        	else if (argu0.equals("smelt")){
        		ai.smelt(argu[1]);
        	}
        	else if (argu0.equals("goto")){
        		ai.goToPoint(argu[1], argu[2], argu[3]);
        	}
        	else if (argu0.equals("test")){
        		ai.test(argu[1]);
        	}
        	else{
        		ai.commandNotExist();
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