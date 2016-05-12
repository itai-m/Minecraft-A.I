package com.custommods.ai;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;

public class WorkTreePlan {

	public static enum Type {smeltStart, smeltEnd, craft, tool, moveTo, nothing};
	private Type type;
	private Object todo;
	private List childs;
	
	///Constructor
	public WorkTreePlan(Object todo, Type type){
		this.type = type;
		this.todo = todo;
		this.childs = new ArrayList<WorkTreePlan>();
	}
	
	///Constructor
	public WorkTreePlan(){
		this.type = Type.nothing;
		this.childs = new ArrayList<WorkTreePlan>();
	}
	
	//Add child, return the child if succeed otherwise null
	public WorkTreePlan addChild(Object todo, Type type){
		WorkTreePlan child = new WorkTreePlan(todo, type);
		if (childs.add(child)){
			return child;
		}
		return null;
	}
	
	///Return a string with the tree to the childs
	public String toString(){
		return print("", false);
	}
	
	private String print(String indent, boolean lastChild){
		String toReturn = "";
		toReturn += indent;
	    if (lastChild)
	    {
	        toReturn += "\\-";
	        indent += "  ";
	    }
	    else
	    {
	    	toReturn += "|-";
	        indent += "| ";
	    }
	    toReturn += printByType() + "\n";
	
	    for (int i = 0; i < childs.size() ; i++){
	    	toReturn += ((WorkTreePlan) childs.get(i)).print(indent, i == childs.size() - 1);
	    }
	    return toReturn;
	}
	
	private String printByType(){
		switch (type) {
		case nothing:
			return "Nothing";
		case moveTo:
			return "Moving to a point: " + ((Vec3)todo).toString();
		case tool:
			switch ((Integer)todo) {
			case AIinventory.AXE:
				return "Swhich to tool- axe \n";
			case AIinventory.PICKAXE:
				return "Swhich to tool- picaxe \n";
			case AIinventory.SHOVEL:
				return "Swhich to tool- shovel \n";
			default:
				return "Swhich to tool- ERROR: no tool \n";
			}
		case craft:
			return "Craft- " + ((ItemStack) todo).getDisplayName();
		case smeltEnd:
			return "Start smelting- " + ((ItemStack) todo).getDisplayName();
		case smeltStart:
			return "End smelting- " + ((ItemStack) todo).getDisplayName();
		default:
			return "Error: Type is worng"; 
		}
	}
	
}
