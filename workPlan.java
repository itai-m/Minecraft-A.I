package com.custommods.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.custommods.walkmod.Step;

import net.minecraft.item.ItemStack;

public class workPlan {

	public static enum type {smelt, craft};
	private List list;
	
	///Constructor
	public workPlan(){
		this.list = new ArrayList();
	}
	
	public void add (ItemStack item){
		
	}
	
	public void add (Queue<Step> steps){
		
	}
	
	public void add (){
		
	}
}
