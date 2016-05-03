package com.custommods.ai;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

public class InventoryTree {

	private ItemStack item;
	private int stackSize;
	private List childs;
	private int numberOfChilds = 0;
	
	public InventoryTree(ItemStack item , int stack) {
		this.item = new ItemStack(item.getItem());
		this.stackSize = stack;
	}
	
	public InventoryTree AddChild (ItemStack item , int stack){
		if (numberOfChilds == 0){
			childs = new ArrayList<InventoryTree>();
		}
		childs.add(new InventoryTree(item, stack));
		numberOfChilds++;
		return (InventoryTree)childs.get(numberOfChilds -1);
	}
	
	public int GetNumberOfChilds(){
		return numberOfChilds;
	}
	
	public InventoryTree GetChild(int index){
		if (index <= numberOfChilds){
			return (InventoryTree) childs.get(index);
		}
		return null;
	}
	
	public boolean removeChild(int index){
		if (index <= numberOfChilds){
			childs.remove(index);
			return true;
		}
		return false;
	}
}
