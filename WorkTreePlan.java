package com.custommods.ai;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;

public class WorkTreePlan {

	public static enum Type {smeltStart, smeltEnd, craft, tool, moveTo, nothing};
	private Type type;
	private Object todo;
	private WorkTreePlan[] childs;
	private WorkTreePlan parent = null;
	private List invetoryChange;
	
	private static AIWorld world = null;
	private static boolean craftCraftingTable = false;
	private static boolean craftFurnace = false;
	private static List locationList;
	
	private final boolean USE_ITEM_PRINT = false;
	private final boolean OBJECT_PRINT = true;
	
	private final int NOT_FOUND = -1;
	private final int MAX_NUMBER_OF_CHILDES = 10;
	
	///Init the WorkTreePlan
	public void init(){
		initLocationList();
		initInveChange();
	}
	
	///Init the location list
	public void initLocationList(){
		locationList = new ArrayList<Vec3>();
	}
	
	///Get the type of the object
	public Type getType() {
		return type;
	}

	///Get the object that need to do
	public Object getTodo() {
		return todo;
	}

	///Check if there is a crafting table
	public static boolean isCraftCraftingTable() {
		return craftCraftingTable;
	}

	///Call when is get a crafting table
	public static void craftCraftingTable() {
		WorkTreePlan.craftCraftingTable = true;
	}

	///Check if there is a furnace
	public static boolean isCraftFurnace() {
		return craftFurnace;
	}

	///Call when is get a furnace
	public static void craftFurnace() {
		WorkTreePlan.craftFurnace = true;
	}

	///Constructor
	public WorkTreePlan(Object todo, Type type){
		set(todo,type);
		initLists();
	}
	
	///Check if there is items in the inventory
	public void CheckItems(AIinventory inve){
		ItemStack craftingTable = new ItemStack(Item.getItemById(UserSetting.CraftingTableId), 1);
		ItemStack furnance = new ItemStack(Item.getItemById(UserSetting.FurnaceId), 1);
		craftCraftingTable = haveItem(inve, craftingTable) >= 0;
		craftFurnace = haveItem(inve, furnance) >= 0;
	}
	
	///Constructor
	public WorkTreePlan(Object todo, Type type, WorkTreePlan parent){
		set(todo,type);
		initLists();
		setParent(parent);
	}
	
	///Constructor
	public WorkTreePlan(){
		set(Type.nothing);
		this.todo = null;
		initLists();
	}
	
	///Constructor
	public WorkTreePlan(String text){
		set(Type.nothing);
		set(text);
		initLists();
	}
	
	///Initialize the Lists
	private void initLists(){
		this.childs = new WorkTreePlan[MAX_NUMBER_OF_CHILDES];
		initInveChange();
	}
	
	///Initialize the inventory Change list
	public void initInveChange(){
		this.invetoryChange = new ArrayList<ItemStack>();
	}
	
	///Add a item that need to be used
	public void AddUseItem(ItemStack item){
		boolean found = false;
		for (Object object : invetoryChange) {
			if (Util.idItemEqual(item, (ItemStack)object)){
				((ItemStack)object).stackSize += item.stackSize;
				found = true;
				/*if (((ItemStack)object).stackSize == 0){
					invetoryChange.remove(object);
				}*/
			}
		}
		if (!found){
			invetoryChange.add(item.copy());
		}
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
	public int haveItem(AIinventory inve, ItemStack item){
		int inventoryStack = inve.stackSize(item);
		int treeStack = stackInTree(item);
		
		Logger.debug("StackTree: " + treeStack + ", inveStack: " + inventoryStack + ", " + item.getDisplayName() + " whit " + item.stackSize, Logger.LOG);
		return (inventoryStack + treeStack - item.stackSize);
	}
	
	private int stackInTree2(ItemStack item){
		int toReturn = 0;
		for (Object object : invetoryChange) {
			if (Util.idItemEqual(item, (ItemStack)object)){
				toReturn += ((ItemStack)object).stackSize;
			}
		}
		return toReturn;
	}
	
	///Stack Size of an item in all the tree
	private int stackInTree(ItemStack item){
		int toReturn = 0;
		WorkTreePlan tempParent = this;
		while (tempParent.getParent() != null){
			tempParent = tempParent.getParent();
		}
		return stackSize(tempParent, item);
	}
	
	///Get stack form the tree
	private int stackSize(WorkTreePlan plan, ItemStack item){
		int toReturn = 0;
		//Logger.debug(plan.printByType(),Logger.LOG);
		for (Object object : plan.invetoryChange) {
			//Logger.debug("looking for " + item.getDisplayName() + " and this is " + ((ItemStack)object).getDisplayName(), Logger.LOG);
			if (Util.idItemEqual(item, (ItemStack)object)){
				//Logger.debug("got one " + item.getDisplayName() , Logger.LOG);
				toReturn += ((ItemStack)object).stackSize;
			}
		}
		for (int i = 0; i < plan.childs.length ; i++){
			if (plan.childs[i]!=null){
				toReturn += stackSize((WorkTreePlan)plan.childs[i], item);
			}
		}
		/*for (Object object : plan.childs) {
			toReturn += stackSize((WorkTreePlan)object, item);
		}*/
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
	public int addChild(Object todo, Type type){
		WorkTreePlan child = new WorkTreePlan(todo, type);
		return addChild(child);
	}
	
	///Add child, return the child if succeed otherwise null
	public int addChild(WorkTreePlan treePlan){
		for (int i=0 ; i < childs.length ; i++){
			if (childs[i] == null){
				childs[i] = treePlan;
				return i;
			}
		}
		return NOT_FOUND;
	}
	
	///Get the number of children
	public int childrenLenght(){
		return childs.length;
	}
	
	///Get child in location
	public WorkTreePlan getChild(int index){
		return (WorkTreePlan) childs[index];
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
	
	///Remove the current node
	public void remove(){
		
	}
	
	///Remove a child
	public void removeChild(int index){
		childs[index].setParent(null);
		childs[index] = null;
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
	
	///Return the string of for the top node
	public String printFromParent(){
		WorkTreePlan tempNode = this;
		String toReturn = "";
		while (tempNode.getParent() != null){
			toReturn += tempNode.printByType();
			tempNode = tempNode.getParent();
		}
		return tempNode.toString();
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
	
	    for (int i = 0; i < childs.length ; i++){
	    	if (childs[i] != null){
	    		toReturn += ((WorkTreePlan) childs[i]).print(indent, i == childs.length - 1, whoToPrint);
	    	}
	    }
	    return toReturn;
	}
	
	///Return one insteps in the tree
	private String printByType(){
		if (todo == null){
			return "the object is null";
		}
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
	
	///Union a similar node in the tree
	public void unionTree(){
		List items = new ArrayList<WorkTreePlan>();
		union(this,items);
	}
	
	///Do the recursive union
	private void union(WorkTreePlan plan,List items){
		if (plan.getType() == Type.moveTo){
			int index;
			if ((index = blockInList(items, plan)) == NOT_FOUND){
				items.add(plan);
			}
			else{
				union((WorkTreePlan)items.get(index), plan);
				plan.remove();
			}
		}
		for (Object object : childs) {
			union((WorkTreePlan)object, items);
		}
	}
	
	///Check if the block is the list, if found return the number in the list otherwise return NOT_FOUND  
	private int blockInList(List items, WorkTreePlan plan){
		for (int i = 0; i < items.size() ; i++) {
			if (((WorkTreePlan)items.get(i)).blockId() == plan.blockId()){
				return i;
			}
		}
		return NOT_FOUND;
	}
	
	///Union two locations
	private void union(WorkTreePlan plan1, WorkTreePlan plan2){
		plan1.set(ArrayUtils.addAll((Vec3[])plan1.getTodo(), (Vec3[])plan2.getTodo()));
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
}
