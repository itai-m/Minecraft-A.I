package com.custommods.walkmod;

import java.util.ArrayList;
import java.util.List;

import com.custommods.walkmod.Step.StepType;
import com.sun.org.apache.xerces.internal.impl.dv.xs.YearDV;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.Vec3;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ISpecialArmor;

class MinecraftWorldInfo implements IWorldInfo {
	private enum BlockType {
		SOLID, NON_SOLID, LIQUID
	};

	private enum Coords {
		X, Y, Z, COUNT
	};

	private static final int MAX_NEIGHBORS = 13;
	private static WorldClient theWorld;
	private static MinecraftWorldInfo instance;
	
	static{
		instance = new MinecraftWorldInfo();
	}
	
	public static MinecraftWorldInfo getInstance(){
		return instance;
	}
	
	private MinecraftWorldInfo(){
		
	}
	
	public static void init() {
		theWorld = Minecraft.getMinecraft().theWorld;
	}
	
	public static boolean Vec3Equlas(Vec3 vec1, Vec3 vec2){
		return (vec1.xCoord == vec2.xCoord &&
				vec1.yCoord == vec2.yCoord &&
				vec1.zCoord == vec2.zCoord);
	}

	@Override
	public double getMinimalDistance(Vec3 pos, Vec3 goal) {
		double yBackup = goal.yCoord;
		goal.yCoord = pos.yCoord;
		double dis = pos.distanceTo(goal);
		goal.yCoord = yBackup;
		return dis + Math.abs(pos.yCoord-goal.yCoord);
	}
	
	
	//This method returns true if the step direction is to the right, compared to the past step 
	private static boolean isRightWalk(int[] posVec, Step parent){
		if (null == parent.getParent()){
			if (Math.signum(posVec[Coords.X.ordinal()] - parent.getLocation().xCoord) == 0 && Math.signum(posVec[Coords.Z.ordinal()]- parent.getLocation().zCoord) == 1)
				return true;
			return false;
		}
		
		Vec3 stepLocation = Vec3.createVectorHelper(
				posVec[Coords.X.ordinal()],
				posVec[Coords.Y.ordinal()],
				posVec[Coords.Z.ordinal()]);
		Vec3 parentLocation = parent.getLocation();
		Vec3 parentParentLocation = parent.getParent().getLocation();
		
		Vec3 stepDirection = Vec3.createVectorHelper(
				Math.signum(stepLocation.xCoord - parentLocation.xCoord), 
				Math.signum(stepLocation.yCoord - parentLocation.yCoord),
				Math.signum(stepLocation.zCoord - parentLocation.zCoord));
		
		Vec3 parentDirection = Vec3.createVectorHelper(
				Math.signum(parentLocation.xCoord - parentParentLocation.xCoord), 
				Math.signum(parentLocation.yCoord - parentParentLocation.yCoord), 
				Math.signum(parentLocation.zCoord - parentParentLocation.zCoord));
		
		//Diagonal Walk
		if (stepDirection.xCoord != 0 && stepDirection.zCoord != 0)
			return false;
		
		if (parentDirection.xCoord == -1 && stepDirection.zCoord == 1)
			return true;
		if (parentDirection.xCoord == 1 && stepDirection.zCoord == -1)
			return true;
		if (parentDirection.zCoord == -1 && stepDirection.xCoord == -1)
			return true;
		if (parentDirection.zCoord == 1 && stepDirection.xCoord == 1)
			return true;
		
		return false;
	}
	//This method returns true if the step direction equals to the parent direction, and not diagonal
	private boolean isStraightWalk(int[] posVec, Step parent){
		if (null == parent.getParent()){
			if (Math.signum(posVec[Coords.X.ordinal()] - parent.getLocation().xCoord) == 1 && Math.signum(posVec[Coords.Z.ordinal()] - parent.getLocation().zCoord) == 0)
				return true;
			return false;
		}
		
		
		Vec3 stepLocation = Vec3.createVectorHelper(
				posVec[Coords.X.ordinal()],
				posVec[Coords.Y.ordinal()],
				posVec[Coords.Z.ordinal()]);
		Vec3 parentLocation = parent.getLocation();
		Vec3 parentParentLocation = parent.getParent().getLocation();
		
		Vec3 stepDirection = Vec3.createVectorHelper(
				Math.signum(stepLocation.xCoord - parentLocation.xCoord), 
				Math.signum(stepLocation.yCoord - parentLocation.yCoord),
				Math.signum(stepLocation.zCoord - parentLocation.zCoord));
		
		Vec3 parentDirection = Vec3.createVectorHelper(
				Math.signum(parentLocation.xCoord - parentParentLocation.xCoord), 
				Math.signum(parentLocation.yCoord - parentParentLocation.yCoord), 
				Math.signum(parentLocation.zCoord - parentParentLocation.zCoord));
		
		//Diagonal Walk
		if (stepDirection.xCoord != 0 && stepDirection.zCoord != 0)
			return false;
		
		return Vec3Equlas(stepDirection, parentDirection);
		
	
	}

	@Override
	public List<Step> getNeighbors(Step currStep) {
		List<Step> currNeighbors = new ArrayList<Step>(MAX_NEIGHBORS);
		
		int[] intPosVec = new int[3];
		roundVec(currStep.getLocation(), intPosVec);
		int[] intCurrVec = { 0, intPosVec[1], 0 };
		int y = 1;
		for (int x = -1; x < 2; x++) {
			for (int z = -1; z < 2; z++) {
				if (x == 0 && z == 0) // position
					continue;
				intCurrVec[Coords.X.ordinal()] = intPosVec[Coords.X.ordinal()]
						+ x;
				intCurrVec[Coords.Y.ordinal()] = intPosVec[Coords.Y.ordinal()]
						+ y;
				intCurrVec[Coords.Z.ordinal()] = intPosVec[Coords.Z.ordinal()]
						+ z;
				Step neighbor = getStepWithPos(currStep, intPosVec, intCurrVec);
				if (null != neighbor)
					currNeighbors.add(neighbor);
				
				BridgeStep bridgeNeghbor = getBridgeNeighbor(currStep, intPosVec, intCurrVec);
				if (bridgeNeghbor != null)
					currNeighbors.add(bridgeNeghbor);
				
				getMineNeighbors(currNeighbors, currStep, intPosVec, intCurrVec);
			}
		}

		return currNeighbors;
	}

	private void getMineNeighbors(List<Step> currNeighbors, Step parentStep, int[] parentLocation, int[] newStepLocation) {
		if (isStraightWalk(newStepLocation, parentStep)){
			Vec3 upwardsMineStepLocation = Vec3.createVectorHelper(
					newStepLocation[Coords.X.ordinal()], 
					parentStep.getLocation().yCoord + 1, 
					newStepLocation[Coords.Z.ordinal()]);
			Vec3 straightMineStepLocation = Vec3.createVectorHelper(
					newStepLocation[Coords.X.ordinal()], 
					parentStep.getLocation().yCoord, 
					newStepLocation[Coords.Z.ordinal()]);
			Vec3 downwardsMineStepLocation = Vec3.createVectorHelper(
					newStepLocation[Coords.X.ordinal()], 
					parentStep.getLocation().yCoord - 1, 
					newStepLocation[Coords.Z.ordinal()]);
			if (checkUpwardsMineNeighbor(upwardsMineStepLocation, parentStep)){
				currNeighbors.add(new MineStep(parentStep, calcCost(parentLocation, newStepLocation), roundVec(upwardsMineStepLocation)));
			}
			if (checkStraightMineNeighbor(straightMineStepLocation, parentStep)){

				currNeighbors.add(new MineStep(parentStep, calcCost(parentLocation, newStepLocation), roundVec(straightMineStepLocation)));
			}
			if (checkDownwardsMineNeighbor(downwardsMineStepLocation, parentStep)){
				
				currNeighbors.add(new MineStep(parentStep, calcCost(parentLocation, newStepLocation), roundVec(downwardsMineStepLocation)));
			}
		}else if (isRightWalk(newStepLocation, parentStep)){
			Vec3 straightMineStepLocation = Vec3.createVectorHelper(
					newStepLocation[Coords.X.ordinal()], 
					parentStep.getLocation().yCoord, 
					newStepLocation[Coords.Z.ordinal()]);
			if (checkStraightMineNeighbor(straightMineStepLocation, parentStep)){
				
				currNeighbors.add(new MineStep(parentStep, calcCost(parentLocation, newStepLocation), roundVec(straightMineStepLocation)));
			}
		}
		
	}

	private boolean checkDownwardsMineNeighbor(Vec3 downwardsMineStepLocation, Step parentStep) {
		Vec3 upLocation = Vec3.createVectorHelper(
				downwardsMineStepLocation.xCoord, downwardsMineStepLocation.yCoord + 1, downwardsMineStepLocation.zCoord);
		if (!isUnderBlockStandable(downwardsMineStepLocation))
			return false;
		if (!isPossiblePlaceToStand(downwardsMineStepLocation) || !isPossiblePlaceToStand(upLocation))
			return true;
		return false;
	}

	private boolean checkStraightMineNeighbor(Vec3 straightMineStepLocation, Step parentStep) {
		Vec3 parentLocation = parentStep.getLocation();
		Vec3 upLocation = Vec3.createVectorHelper(
				parentLocation.xCoord, parentLocation.yCoord + 1, parentLocation.zCoord);
		if (!isUnderBlockStandable(straightMineStepLocation))
			return false;
		if (!isPossiblePlaceToStand(straightMineStepLocation)|| !isPossiblePlaceToStand(upLocation))
			return true;
		return false;
	}

	private boolean checkUpwardsMineNeighbor(Vec3 upwardsMineStepLocation, Step parentStep) {
		if (!isUnderBlockStandable(upwardsMineStepLocation))
			return false;
		if (!isPossiblePlaceToStand(upwardsMineStepLocation))
			return true;
		return false;
	}
	
	private boolean isUnderBlockStandable(Vec3 location){
		location = roundVec(location);
		int[] underLocation = {(int)location.xCoord, (int)location.yCoord - 1, (int)location.zCoord};
		if (getBlockType(underLocation) == BlockType.SOLID)
			return true;
		return false;
	}

	private BridgeStep getBridgeNeighbor(Step parent, int[] parentPos, int[] targetPos){
		if(checkIfDiagonalWalk(parentPos, targetPos))
			return null;
		Vec3 bridgeLocation = Vec3.createVectorHelper(
				targetPos[Coords.X.ordinal()], 
				targetPos[Coords.Y.ordinal()] - 1,
				targetPos[Coords.Z.ordinal()]);
		if (isPossiblePlaceToBridge(bridgeLocation))
			return new BridgeStep(parent, 2, bridgeLocation);
		return null;
	}
	private boolean checkIfDiagonalWalk(int[] parentPos, int[] newStepPos){
		return (parentPos[Coords.X.ordinal()] != newStepPos[Coords.X.ordinal()] &&
				parentPos[Coords.Z.ordinal()] != newStepPos[Coords.Z.ordinal()]) ? true : false;
	}
	private boolean checkIfDiagonalWalkBlocked(Step parentStep, int[] newStepPos, int[] parentPos){
		Vec3 mutualNeighborPos1 = Vec3.createVectorHelper(newStepPos[Coords.X.ordinal()],
				newStepPos[Coords.Y.ordinal()],
				parentPos[Coords.Z.ordinal()]);
		Vec3 mutualNeighborPos2 = Vec3.createVectorHelper(
				parentPos[Coords.X.ordinal()],
				newStepPos[Coords.Y.ordinal()],
				newStepPos[Coords.Z.ordinal()]);

		return (!isPossiblePlaceToStand(mutualNeighborPos1)||!isPossiblePlaceToStand(mutualNeighborPos2)) ? true : false;
	}
	
	private Step getStepWithPos(Step parentStep, int[] parentPos,
			int[] newStepPos) {// //
		int[] targetPos = checkFirstNotAirBlock(newStepPos);
		
		Vec3 returnVec = Vec3.createVectorHelper(
				targetPos[Coords.X.ordinal()], targetPos[Coords.Y.ordinal()],
				targetPos[Coords.Z.ordinal()]);
		StepType type = StepType.Unassigned;
		boolean diagonalWalk = checkIfDiagonalWalk(parentPos, targetPos);
		
		//DIAGONAL WALK
		if(diagonalWalk && checkIfDiagonalWalkBlocked(parentStep, targetPos, parentPos)){
				return new UnassignedStep(parentStep, calcCost(
						parentPos, targetPos), returnVec);
		}

		if (parentPos[Coords.Y.ordinal()] >= targetPos[Coords.Y.ordinal()])
			type = StepType.Walk;
		else
			type = StepType.Jump;
		
		
		int[] newStepPosUp = {targetPos[Coords.X.ordinal()], targetPos[Coords.Y.ordinal()] + 1, targetPos[Coords.Z.ordinal()]};
		//if (getBlockType(targetPos) == BlockType.SOLID || getBlockType(newStepPosUp) == BlockType.SOLID)
		if (!isPossiblePlaceToStand(
				Vec3.createVectorHelper(newStepPos[Coords.X.ordinal()], newStepPos[Coords.Y.ordinal()], newStepPos[Coords.Z.ordinal()])
				) ||
			!isPossiblePlaceToStand(
					Vec3.createVectorHelper(targetPos[Coords.X.ordinal()], targetPos[Coords.Y.ordinal()], targetPos[Coords.Z.ordinal()])
				)){
				return new UnassignedStep(parentStep, calcCost(
						parentPos, targetPos), returnVec);
		}
		if(type == StepType.Jump){
			Vec3 posUpperParent = Vec3.createVectorHelper(parentPos[Coords.X.ordinal()], parentPos[Coords.Y.ordinal()] + 1, parentPos[Coords.Z.ordinal()]);
			//this condition was added in order to make sure that we won't get stuck on leaves (or any other object above you) while jumping.
			if (!isPossiblePlaceToStand(posUpperParent))
				return new UnassignedStep(parentStep, calcCost(
						parentPos, targetPos), returnVec);
			
			//this condition is here to prevent unnatural jumps that is larger than one block
			 if (targetPos[Coords.Y.ordinal()] - parentPos[Coords.Y.ordinal()] > 1)
				 return new UnassignedStep(parentStep, calcCost(
							parentPos, targetPos), returnVec);
			
			int[] underStepPos = { targetPos[Coords.X.ordinal()],targetPos[Coords.Y.ordinal()] - 1, targetPos[Coords.Z.ordinal()]};
			return new JumpStep(parentStep, calcCost(
						parentPos, underStepPos) + 0.8, returnVec);
		}
		return new WalkStep(parentStep, calcCost(parentPos,
				targetPos), returnVec);
	}
	
	public static Vec3 checkFirstNotAirBlock(Vec3 pos){
		int[] posInt = new int[Coords.COUNT.ordinal()];
		roundVec(pos, posInt);
		int[] result = checkFirstNotAirBlock(posInt);
		return Vec3.createVectorHelper(result[Coords.X.ordinal()], result[Coords.Y.ordinal()], result[Coords.Z.ordinal()]);
	}

	private static int[] checkFirstNotAirBlock(int[] stepPos){
		int[] firstNotAirBlock = stepPos.clone();
		for (int i = stepPos[Coords.Y.ordinal()]; i > 0 ; i--){
			firstNotAirBlock[Coords.Y.ordinal()] = i;
			if (getBlockType(firstNotAirBlock) == BlockType.SOLID)
				break;
		}
		firstNotAirBlock[Coords.Y.ordinal()]++;
		return firstNotAirBlock;
	}

	public boolean isPossiblePlaceToStand(Vec3 pos) {
		pos = roundVec(pos);
		int[] upperBlockLocation = { (int) pos.xCoord, (int) pos.yCoord + 1, (int) pos.zCoord};
		int[] blockLocation = { (int) pos.xCoord, (int) pos.yCoord, (int) pos.zCoord};
		if (getBlockType(blockLocation) == BlockType.NON_SOLID && getBlockType(upperBlockLocation) == BlockType.NON_SOLID)
			return true;
		return false;
	}
	
	public boolean isPossiblePlaceToBridge(Vec3 pos) {
		pos = roundVec(pos);
		int[] upperBlockLocation = { (int) pos.xCoord, (int) pos.yCoord + 1, (int) pos.zCoord};
		int[] blockLocation = { (int) pos.xCoord, (int) pos.yCoord, (int) pos.zCoord};
		int[] underBlockLocation = { (int) pos.xCoord, (int) pos.yCoord - 1, (int) pos.zCoord};
		if (getBlockType(blockLocation) == BlockType.NON_SOLID && 
				getBlockType(upperBlockLocation) == BlockType.NON_SOLID && 
				getBlockType(underBlockLocation) == BlockType.NON_SOLID)
			return true;
		return false;
	}

	private static void roundVec(Vec3 v, int[] result) {
		result[0] = (int) Math.floor(v.xCoord);
		result[1] = (int) Math.round(v.yCoord);
		result[2] = (int) Math.floor(v.zCoord);
	}

	public static Vec3 roundVec(Vec3 v) {
		int[] goalArr = new int[Coords.COUNT.ordinal()];
		MinecraftWorldInfo.roundVec(v, goalArr);
		return Vec3.createVectorHelper(goalArr[0], goalArr[1], goalArr[2]);
	}

	private static double calcCost(int[] pos1, int[] pos2) {// //
		double res = 0;
		for (int i = 0; i < pos1.length; i++)
			res += Math.pow(pos2[i] - pos1[i], 2);
		return Math.sqrt(res);
	}

	private static BlockType getBlockType(int[] blockLocation) {
		Block block = theWorld.getBlock(				
				blockLocation[Coords.X.ordinal()],
				blockLocation[Coords.Y.ordinal()],
				blockLocation[Coords.Z.ordinal()]); 
		if(
			block.getBlockHardness(
				theWorld, 
				blockLocation[Coords.X.ordinal()],
				blockLocation[Coords.Y.ordinal()],
				blockLocation[Coords.Z.ordinal()]) 
			< 0.01f)
				return BlockType.NON_SOLID;
		if (block.getMaterial().isLiquid())
			return BlockType.LIQUID;
		return BlockType.SOLID;
	}
}
