package com.custommods.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.custommods.walkmod.Step;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;

public class WorkPlan {

	public static enum type {smelt, craft};
	public static int NOT_FOUND = -1;
	private List list;
	private List usedItems;
	private List locationList;
	
	///Constructor
	public WorkPlan(){
		this.list = new ArrayList();
		this.usedItems = new ArrayList<ItemStack>();
		this.locationList = new ArrayList<ItemStack>();
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
	
	///Add item that is used
	public void addUsedItem(ItemStack item){
		usedItems.add(item);
	}
	
	///Remove an item from the used
	public boolean removeUsedItem(ItemStack item){
		int stack = item.stackSize;
		for (Object object : usedItems) {
			if (object instanceof ItemStack){
				if (Item.getIdFromItem(item.getItem()) == Item.getIdFromItem(((ItemStack) object).getItem())){
					if (((ItemStack) object).stackSize <= stack){
						stack -= ((ItemStack) object).stackSize;
						usedItems.remove(object);
						if (stack==0){
							return true;
						}
					}
					else{
						((ItemStack) object).stackSize -= stack;
						return true;
					}
				}
			}
		}
		return false;
	}
	
	///Add location to the locationList
	public void addLoc(Vec3 loc){
		locationList.add(loc);
	}
	
	///Peek location from the locationList
	public Vec3 peekLoc(){
		return (Vec3) locationList.get(locationList.size()-1);
	}
	
	///Pull location from the locationList
	public Vec3 pullLoc(){
		Vec3 toReturn = peekLoc();
		removeLoc();
		return toReturn;
	}
	
	///Remove the last form the locationList
	public void removeLoc(){
		locationList.remove(locationList.size() - 1);
	}
	
	///Count the number of location
	public int countLoc(){
		return locationList.size();
	}
	
	///Return array of all the location in the location list
	public Vec3[] GetLoctionArr(){
		Vec3[] toReturn = new Vec3[locationList.size()];
		int conter = 0;
		for (Object object : locationList) {
			if (object instanceof Vec3){
				toReturn[conter++] = (Vec3)object;
			}
		}
		return toReturn;
	}
	
	///Get the number of stack of the item been used, return NOT_FOUND if the item not found
	public int getStackInUsed(ItemStack item){
		int conter = 0;
		for (Object object : usedItems) {
			if (object instanceof ItemStack){
				if (Item.getIdFromItem(item.getItem()) == Item.getIdFromItem(((ItemStack) object).getItem())){
					conter += ((ItemStack) object).stackSize;
				}
			}
		}
		if (conter != 0){
			return conter;
		}
		return NOT_FOUND;
	}
	
	///Check if can used an item
	public boolean canUsedItem(ItemStack item, AIinventory inve){
		int stack = 0;
		ItemStack newItem = new ItemStack(item.getItem());
		newItem.stackSize = item.stackSize;
		if ((stack = getStackInUsed(item))!=NOT_FOUND){
			newItem.stackSize += stack;
		}
		return inve.haveItem(newItem);
	}
	
	///Remove the last one in the plan
	public void removeLast(){
		list.remove(list.size()-1);
	}
	
	///Peek the first one in the plan
	public Object peekLast(){
		return list.get(list.size()-1);
	}
	
	///Pull the first one in the plan
	public Object pullLast(){
		Object toReturn  = list.get(list.size()-1);
		this.removeLast();
		return toReturn;
	}
		
	///Remove the first one in the plan
	public void removeFirst(){
		list.remove(0);
	}
	
	///Peek the first one in the plan
	public Object peekFirst(){
		return list.get(0);
	}
	
	///Pull the first one in the plan
	public Object pullFirst(){
		Object toReturn  = list.get(0);
		this.removeFirst();
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
				if (object == null){
					toReturn += index++ + ": is null \n";
				}
				else{
					toReturn += index++ + ": Not Found: " + object.toString() + "\n";
				}
			}
		}
		return toReturn;
	}

}
