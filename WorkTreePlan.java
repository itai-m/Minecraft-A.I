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
	
	///Get the type of the object
	public Type getType() {
		return type;
	}

	///Get the object that need to do
	public Object getTodo() {
		return todo;
	}

	///Constructor
	public WorkTreePlan(Object todo, Type type){
		set(todo,type);
		this.childs = new ArrayList<WorkTreePlan>();
	}
	
	///Constructor
	public WorkTreePlan(){
		set(Type.nothing);
		this.childs = new ArrayList<WorkTreePlan>();
	}
	
	///Add child, return the child if succeed otherwise null
	public WorkTreePlan addChild(Object todo, Type type){
		WorkTreePlan child = new WorkTreePlan(todo, type);
		if (childs.add(child)){
			return child;
		}
		return null;
	}
	
	///Add child, return the child if succeed otherwise null
	public WorkTreePlan addChild(WorkTreePlan treePlan){
		if (childs.add(treePlan)){
			return treePlan;
		}
		return null;
	}
	
	///Get the number of children
	public int childrenLenght(){
		return childs.size();
	}
	
	///Get child in location
	public WorkTreePlan getChild(int index){
		return (WorkTreePlan) childs.get(index);
	}
	
	///Set the object
	public void set(Object todo){
		this.todo = todo;
	}
	
	///Set the type
	public void set(Type type){
		this.type = type;
	}
	
	///Set the object and type
	public void set(Object todo, Type type){
		set(type);
		set(todo);
	}
	
	///Return a string with the tree to the children
	public String toString(){
		return "\n" + print("", true);
	}
	
	//Return a string of a tree
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
	
	///Return one insteps in the tree
	private String printByType(){
		switch (type) {
		case nothing:
			return "Nothing";
		case moveTo:
			Vec3[] loctions = (Vec3[])todo;
			String toReturn = "Moving to a points: ";
			for (Vec3 vec3 : loctions) {
				toReturn += vec3.toString() + " ";
			}
			return toReturn;
		case tool:
			switch ((Integer)todo) {
			case AIinventory.AXE:
				return "Swhich to tool- axe";
			case AIinventory.PICKAXE:
				return "Swhich to tool- picaxe";
			case AIinventory.SHOVEL:
				return "Swhich to tool- shovel";
			default:
				return "Swhich to tool- ERROR: no tool";
			}
		case craft:
			return "Craft- " + ((ItemStack) todo).getDisplayName();
		case smeltEnd:
			return "End smelting- " + ((ItemStack) todo).getDisplayName();
		case smeltStart:
			return "Start smelting- " + ((ItemStack) todo).getDisplayName();
		default:
			return "Error: Type is worng"; 
		}
	}
	
}
