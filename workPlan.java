package com.custommods.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.custommods.walkmod.Step;

import net.minecraft.item.ItemStack;

public class WorkPlan {

	public static enum type {smelt, craft};
	private List list;
	
	///Constructor
	public WorkPlan(){
		this.list = new ArrayList();
	}
	
	///Add item to Craft
	public void add (ItemStack item){
		list.add(item);
	}
	
	///Add Queue of steps
	public void add (Queue<Step> steps){
		list.add(steps);
	}
	
	///Check if the plan is empty
	public boolean isEmpty(){
		return list.isEmpty();
	}
	
	///Remove the last one in the plan
	public void removeLast(){
		list.remove(list.lastIndexOf(list));
	}
	
	///Peek the last one in the plan
	public Object peekFirst(){
		return list.get(0);
	}
	
	///Pull the last one in the plan
	public Object pullFirst(){
		Object toReturn  = list.get(0);
		list.remove(0);
		return toReturn;
	}
	
	///Return the string with all the plan
	public String toString(){
		String toReturn = "";
		int index = 0;
		if (list.isEmpty()){
			return "Plan is empty";
		}
		for (Object object : list) {
			if (object instanceof ItemStack){
				toReturn += index++ + ": Craft- " + ((ItemStack) object).getDisplayName() + "\n";
			}
			else if (object instanceof Queue){
				toReturn += index++ + ": Move to- " + ((Step) ((Queue)object).peek()).getLocation() + "\n";
			}
			else{
				toReturn += "Not Found \n";
			}
		}
		return toReturn;
	}

}
