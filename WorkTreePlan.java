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
	private WorkTreePlan parent = null;
	private List invetoryChange;
	
	private static AIWorld world = null;
	
	private final boolean USE_ITEM_PRINT = false;
	private final boolean OBJECT_PRINT = true;
	
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
		this.invetoryChange = new ArrayList<ItemStack>();
	}
	
	///Constructor
	public WorkTreePlan(Object todo, Type type, WorkTreePlan parent){
		set(todo,type);
		this.childs = new ArrayList<WorkTreePlan>();
		this.invetoryChange = new ArrayList<ItemStack>();
		setParent(parent);
	}
	
	///Constructor
	public WorkTreePlan(){
		set(Type.nothing);
		this.todo = null;
		this.childs = new ArrayList<WorkTreePlan>();
		this.invetoryChange = new ArrayList<ItemStack>();
	}
	
	///Constructor
	public WorkTreePlan(String text){
		set(Type.nothing);
		set(text);
		this.childs = new ArrayList<WorkTreePlan>();
		this.invetoryChange = new ArrayList<ItemStack>();
	}
	
	///Add a item that need to be used
	public void AddUseItem(ItemStack item){
		invetoryChange.add(item);
	}
	
	///Add a item that need to be used
	public void AddUseItem(ItemStack item, int stack){
		ItemStack tempItem = new ItemStack(item.getItem());
		tempItem.stackSize = stack;
		AddUseItem(tempItem);
	}
		
	///Get the parent
	public WorkTreePlan getParent(){
		return parent;
	}
	
	///Check if already have the item
	public boolean haveItem(AIinventory inve, ItemStack item){
		int inventoryStack = inve.stackSize(item);
		int treeStack = StackInTree(item);
		if (inventoryStack + treeStack >= item.stackSize){
			return true;
		}
		return false;
	}
	
	///Stack Size of an item in all the tree
	private int StackInTree(ItemStack item){
		WorkTreePlan tempParent = this;
		while (tempParent.getParent() != null){
			tempParent = tempParent.getParent();
		}
		return stackSize(tempParent, item);
	}
	
	///Get stack form the tree
	private int stackSize(WorkTreePlan plan, ItemStack item){
		int toReturn = 0;
		for (Object object : plan.invetoryChange) {
			if (Util.idItemEqual(item, (ItemStack)object)){
				toReturn += ((ItemStack)object).stackSize;
			}
		}
		for (Object object : plan.childs) {
			toReturn += stackSize((WorkTreePlan)object, item);
		}
		return toReturn;
	}
	
	///Set the world
	public void setWorld(AIWorld world){
		Logger.debug(world.toString(), Logger.LOG);
		this.world = world;
	}
	
	///Set the parent of this
	public void setParent(WorkTreePlan parent){
		this.parent = parent;
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
		return "\n" + print("", true, OBJECT_PRINT) + "\n" + print("", true, USE_ITEM_PRINT);
	}
	
	///Return the id of the block in the todo, otherwise return Util.CANT_GET
	public int blockId(){
		if (todo instanceof Vec3[] && world != null){
			return world.dropBlockId(((Vec3[])todo)[0]);
		}
		return Util.CANT_GET;
	}
	
	//Return a string of a tree
	private String print(String indent, boolean lastChild, boolean whoToPrint){
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
	    if (whoToPrint){
	    	toReturn += printByType() + "\n";
	    }
	    else{
	    	toReturn += printUseItem() + "\n";
	    }
	
	    for (int i = 0; i < childs.size() ; i++){
	    	toReturn += ((WorkTreePlan) childs.get(i)).print(indent, i == childs.size() - 1, whoToPrint);
	    }
	    return toReturn;
	}
	
	///Return one insteps in the tree
	private String printByType(){
		switch (type) {
		case nothing:
			if(todo == null){
				return "Nothing";
			}
			else{
				return "Get - " + todo;
			}
		case moveTo:
			Vec3[] loctions = (Vec3[])todo;
			int blockid = blockId();
			String toReturn = "";
			if (blockid != Util.CANT_GET){
				toReturn = "Moving to a points to get " + Util.getItemStack(blockid).getDisplayName() + ": ";
			}
			else{
				toReturn = "Moving to a points: ";
			}
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
	
	///Print the used item
	public String printUseItem(){
		String toReturn = "";
		for (Object object : invetoryChange) {
			toReturn += ((ItemStack)object).getDisplayName() + "- Size:" + ((ItemStack)object).stackSize + ", ";
		}
		return toReturn;
	}
	
}
