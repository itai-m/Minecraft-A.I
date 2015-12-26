package com.custommods.walkmod;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.custommods.walkmod.Step.StepType;
import com.sun.org.apache.xerces.internal.impl.dv.xs.YearDV;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.Vec3;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ISpecialArmor;

public class MinecraftWorldInfo implements IWorldInfo {
	private enum BlockType {
		SOLID, NON_SOLID, LIQUID
	};

	private enum Coords {
		X, Y, Z, COUNT
	};
	public static final int RELATIVE_FEET_POS = 0;
	public static final int RELATIVE_HEAD_POS = 1;
	public static final int RELATIVE_BLOCK_UNDER_POS = -1;

	private static final double JUMP_COST = 0.4;
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
	
	public static boolean vec3Equlas(Vec3 vec1, Vec3 vec2){
		if (vec1.xCoord == vec2.xCoord &&
				vec1.yCoord == vec2.yCoord &&
				vec1.zCoord == vec2.zCoord)
			return true;
		return false;
	}

	@Override
	public double getMinimalDistance(Vec3 pos, Vec3 goal) {
		double yBackup = goal.yCoord;
		goal.yCoord = pos.yCoord;
		double dis = pos.distanceTo(goal);
		goal.yCoord = yBackup;
		return dis + JUMP_COST * Math.abs(pos.yCoord-goal.yCoord);
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
	private boolean isStraightWalk(int[] posVec, Step currentStep){
		if (null == currentStep.getParent()){
			if (Math.signum(posVec[Coords.X.ordinal()] - currentStep.getLocation().xCoord) == 1 && Math.signum(posVec[Coords.Z.ordinal()] - currentStep.getLocation().zCoord) == 0)
				return true;
			return false;
		}
		
		Vec3 stepLocation = Vec3.createVectorHelper(
				posVec[Coords.X.ordinal()],
				posVec[Coords.Y.ordinal()],
				posVec[Coords.Z.ordinal()]);
		Vec3 parentLocation = currentStep.getLocation();
		Vec3 parentParentLocation = currentStep.getParent().getLocation();
		
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
		
		return vec3Equlas(stepDirection, parentDirection);
		
	
	}

	@Override
	public List<Step> getNeighbors(Step currStep) {
		List<Step> currNeighbors = new ArrayList<Step>(MAX_NEIGHBORS);
		
		int[] intPosVec = new int[3];
		roundVec(currStep.getLocation(), intPosVec);
		int[] intCurrVec = { 0, intPosVec[1], 0 };
		for (int x = -1; x < 2; x++) {
			for (int z = -1; z < 2; z++) {
				if (x == 0 && z == 0) // position
					continue;
				//if(x != 0 && z != 0) continue; ////eliminate diagonals
				intCurrVec[Coords.X.ordinal()] = intPosVec[Coords.X.ordinal()]
						+ x;
				intCurrVec[Coords.Y.ordinal()] = intPosVec[Coords.Y.ordinal()]
						+ RELATIVE_FEET_POS;
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

	private void getMineNeighbors(List<Step> currNeighbors, Step currentStep, int[] currentStepLocation, int[] newStepLocation) {
		Vec3 parentLoc = Vec3.createVectorHelper(0, 0, 0); 
		if(null != currentStep.getParent())
			parentLoc = currentStep.getParent().getLocation();

		
		int stepDirX = (int) Math.signum(currentStepLocation[0]-parentLoc.xCoord);  
		int stepDirZ = 0;
		//if stepDirX != 0, then stepDirZ is considered 0 to support diagonal walk
		if(0 == stepDirX)
			stepDirZ = (int) Math.signum(currentStepLocation[2]-parentLoc.zCoord);  
		
		int newStepDirX = (int) Math.signum(newStepLocation[0]-currentStepLocation[0]);
		int newStepDirZ = (int) Math.signum(newStepLocation[2]-currentStepLocation[2]);
		
		if(newStepDirX != 0 && newStepDirZ != 0)
			return;//no support for diagonal mining
		if(stepDirX == -newStepDirX && stepDirZ == -newStepDirZ)
			return;//no support for backwards mining

		//add mining at the same level
		if(isSolidUnder(newStepLocation)) {
			LinkedList<MineSpot> mineSpots = getMineSpots(newStepLocation, 2);
			if(mineSpots != null && mineSpots.size() != 0) {
				currNeighbors.add(
						new MineStep(
								currentStep, 
								getSpotsCost(mineSpots)+calcCost(currentStepLocation, newStepLocation), 
								Vec3.createVectorHelper(newStepLocation[0], newStepLocation[1], newStepLocation[2]),
								mineSpots,
								"Forward Mine Step"
								));
			}
		}	

		//digging up and down, in case of forward mining
		if(stepDirX == newStepDirX && stepDirZ == newStepDirZ) {
			int[] newDownwardsStepLocation = {newStepLocation[Coords.X.ordinal()], newStepLocation[Coords.Y.ordinal()] + RELATIVE_BLOCK_UNDER_POS, newStepLocation[Coords.Z.ordinal()]};
			LinkedList<MineSpot> downwardsMineSpots = getMineSpots(newDownwardsStepLocation, 3);
			if(downwardsMineSpots != null && downwardsMineSpots.size() != 0) {
				newDownwardsStepLocation[1]--;
				int[] locationAfterFall = checkFirstNotAirBlock(newDownwardsStepLocation);
								
				currNeighbors.add(
						new MineStep(
								currentStep, 
								getSpotsCost(downwardsMineSpots)+calcCost(currentStepLocation, locationAfterFall), 
								Vec3.createVectorHelper(locationAfterFall[0], locationAfterFall[1], locationAfterFall[2]),
								downwardsMineSpots,
								"Downwards Mine Step"
								)
						);

			}
			
			int[] newUpwardsStepLocation = {newStepLocation[Coords.X.ordinal()], newStepLocation[Coords.Y.ordinal()] + RELATIVE_FEET_POS+1, newStepLocation[Coords.Z.ordinal()]};
			int[] blockAboveParentHead = {currentStepLocation[Coords.X.ordinal()], currentStepLocation[Coords.Y.ordinal()] + RELATIVE_HEAD_POS+1, currentStepLocation[Coords.Z.ordinal()]};
			LinkedList<MineSpot> upwardsMiningSpots = getMineSpots(newUpwardsStepLocation, 2);
			LinkedList<MineSpot> blockAboveHeadMiningSpots = getMineSpots(blockAboveParentHead, 1);
			upwardsMiningSpots = mergeMineSpotsLists(upwardsMiningSpots, blockAboveHeadMiningSpots);
			if(upwardsMiningSpots != null && upwardsMiningSpots.size() != 0 && isSolidUnder(newUpwardsStepLocation)) {
				currNeighbors.add(
						new MineStep(
								currentStep, 
								getSpotsCost(upwardsMiningSpots)+calcCost(currentStepLocation, newUpwardsStepLocation),
								Vec3.createVectorHelper(newUpwardsStepLocation[0], newUpwardsStepLocation[1], newUpwardsStepLocation[2]),
								upwardsMiningSpots,
								"Upwards Mine Step"
								)
						);
			}
			
		}
		/*
		if(1 == 1)
			return;
		if (isStraightWalk(newStepLocation, currentStep)){
			Vec3 upwardsMineStepLocation = Vec3.createVectorHelper(
					newStepLocation[Coords.X.ordinal()], 
					currentStep.getLocation().yCoord + 1, 
					newStepLocation[Coords.Z.ordinal()]);
			Vec3 straightMineStepLocation = Vec3.createVectorHelper(
					newStepLocation[Coords.X.ordinal()], 
					currentStep.getLocation().yCoord, 
					newStepLocation[Coords.Z.ordinal()]);
			Vec3 downwardsMineStepLocation = Vec3.createVectorHelper(
					newStepLocation[Coords.X.ordinal()], 
					currentStep.getLocation().yCoord - 1, 
					newStepLocation[Coords.Z.ordinal()]);
			if (checkUpwardsMineNeighbor(upwardsMineStepLocation, currentStep)){
				currNeighbors.add(new MineStep(currentStep, calcCost(currentStepLocation, newStepLocation), roundVec(upwardsMineStepLocation)));
			}
			if (checkStraightMineNeighbor(straightMineStepLocation, currentStep)){

				currNeighbors.add(new MineStep(currentStep, calcCost(currentStepLocation, newStepLocation), roundVec(straightMineStepLocation)));
			}
			if (checkDownwardsMineNeighbor(downwardsMineStepLocation, currentStep)){
				
				currNeighbors.add(new MineStep(currentStep, calcCost(currentStepLocation, newStepLocation), roundVec(downwardsMineStepLocation)));
			}
		}else if (isRightWalk(newStepLocation, currentStep)){
			Vec3 straightMineStepLocation = Vec3.createVectorHelper(
					newStepLocation[Coords.X.ordinal()], 
					currentStep.getLocation().yCoord, 
					newStepLocation[Coords.Z.ordinal()]);
			if (checkStraightMineNeighbor(straightMineStepLocation, currentStep)){
				
				currNeighbors.add(new MineStep(currentStep, calcCost(currentStepLocation, newStepLocation), roundVec(straightMineStepLocation)));
			}
		}
		*/
	}
	
	private boolean isSolidUnder(int[] pos) {
		pos[Coords.Y.ordinal()] += RELATIVE_BLOCK_UNDER_POS;
		boolean result = getBlockType(pos) == BlockType.SOLID;
		pos[Coords.Y.ordinal()] -= RELATIVE_BLOCK_UNDER_POS;
		return result;
	}

	private double getSpotsCost(LinkedList<MineSpot> mineSpots) {
		if (null == mineSpots)
			return 0;
		double totalCost = 0;
		for (MineSpot mineSpot: mineSpots){
			totalCost += mineSpot.getMineCost();
		}
		return totalCost;
	}

	private LinkedList<MineSpot> mergeMineSpotsLists(LinkedList<MineSpot> list1, LinkedList<MineSpot> list2){
		if (list1 == null)
			return list2;
		if (list2 == null){
			return list1;
		}
		list1.addAll(list2);
		return list1;
	}
	
	//height is the height of the player, or in other words how many blocks to go up
	private LinkedList<MineSpot> getMineSpots(int[] loc, int height) {
		LinkedList<MineSpot> mineSpots = new LinkedList<MineSpot>();
		int[] locCopy = loc.clone();
		int i;
		for(i = 0; i < height; i++){
			if (getBlockType(locCopy) == BlockType.SOLID){
				mineSpots.add(new MineSpot(
						Vec3.createVectorHelper(locCopy[Coords.X.ordinal()], 
								locCopy[Coords.Y.ordinal()], locCopy[Coords.Z.ordinal()]), 1));
			}
			locCopy[Coords.Y.ordinal()]++;
		}
		
		return mineSpots;
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
				targetPos[Coords.Y.ordinal()],
				targetPos[Coords.Z.ordinal()]);
		if (isPossiblePlaceToBridge(bridgeLocation))
			return new BridgeStep(parent, 2.5, bridgeLocation);
		return null;
	}
	private boolean checkIfDiagonalWalk(int[] parentPos, int[] newStepPos){
		return (parentPos[Coords.X.ordinal()] != newStepPos[Coords.X.ordinal()] &&
				parentPos[Coords.Z.ordinal()] != newStepPos[Coords.Z.ordinal()]) ? true : false;
	}
	private boolean checkIfDiagonalWalkBlocked(int[] newStepPos, int[] parentPos){
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
				targetPos[Coords.X.ordinal()], 
				targetPos[Coords.Y.ordinal()] + RELATIVE_FEET_POS,
				targetPos[Coords.Z.ordinal()]);
		StepType type = StepType.Unassigned;
		boolean diagonalWalk = checkIfDiagonalWalk(parentPos, targetPos);
		
		//DIAGONAL WALK
		if(diagonalWalk && checkIfDiagonalWalkBlocked(targetPos, parentPos)){
				return new UnassignedStep(parentStep, calcCost(
						parentPos, targetPos), returnVec);
		}

		if (parentPos[Coords.Y.ordinal()] >= targetPos[Coords.Y.ordinal()])
			type = StepType.Walk;
		else
			type = StepType.Jump;
		
		
		int[] newStepPosUp = {targetPos[Coords.X.ordinal()], targetPos[Coords.Y.ordinal()] + RELATIVE_HEAD_POS, targetPos[Coords.Z.ordinal()]};
		if (!isPossiblePlaceToStand(
					Vec3.createVectorHelper(targetPos[Coords.X.ordinal()], targetPos[Coords.Y.ordinal()], targetPos[Coords.Z.ordinal()])
				)
				||
				(isCliffStep(newStepPos, targetPos)&&
				!isPossiblePlaceToStand(
						Vec3.createVectorHelper(newStepPos[Coords.X.ordinal()], newStepPos[Coords.Y.ordinal()], newStepPos[Coords.Z.ordinal()])
					)
				)
			){
			//the second part of the condition makes sure that even in "cliff" state it would make sure that the block in front of the player won't be blocked
				return new UnassignedStep(parentStep, calcCost(
						parentPos, targetPos), returnVec);
		}
		if(type == StepType.Jump){
			Vec3 posUpperParent = Vec3.createVectorHelper(parentPos[Coords.X.ordinal()], parentPos[Coords.Y.ordinal()] + RELATIVE_HEAD_POS, parentPos[Coords.Z.ordinal()]);
			//this condition was added in order to make sure that we won't get stuck on leaves (or any other object above you) while jumping.
			if (!isPossiblePlaceToStand(posUpperParent))
				return new UnassignedStep(parentStep, calcCost(
						parentPos, targetPos), returnVec);
			
			//this condition is here to prevent unnatural jumps that is larger than one block
			 if (targetPos[Coords.Y.ordinal()] - parentPos[Coords.Y.ordinal()] > 1)
				 return new UnassignedStep(parentStep, calcCost(
							parentPos, targetPos), returnVec);
			
			int[] underStepPos = { targetPos[Coords.X.ordinal()],targetPos[Coords.Y.ordinal()] + RELATIVE_BLOCK_UNDER_POS, targetPos[Coords.Z.ordinal()]};
			return new JumpStep(parentStep, calcCost(
						parentPos, underStepPos) + JUMP_COST, returnVec);
		}
		return new WalkStep(parentStep, calcCost(parentPos,
				targetPos), returnVec);
	}
	
	private boolean isCliffStep(int[] location, int[] locationAfterFirstNotAirBlockMethod){
		return locationAfterFirstNotAirBlockMethod[Coords.Y.ordinal()] < location[Coords.Y.ordinal()];
	}
	
	public static Vec3 checkFirstNotAirBlock(Vec3 pos){
		int[] posInt = new int[Coords.COUNT.ordinal()];
		roundVec(pos, posInt);
		int[] result = checkFirstNotAirBlock(posInt);
		return Vec3.createVectorHelper(result[Coords.X.ordinal()], result[Coords.Y.ordinal()], result[Coords.Z.ordinal()]);
	}

	private static int[] checkFirstNotAirBlock(int[] stepPos){
		int[] firstNotAirBlock = stepPos.clone();
		for (int i = stepPos[Coords.Y.ordinal()] + RELATIVE_FEET_POS; i > 0 ; i--){
			firstNotAirBlock[Coords.Y.ordinal()] = i;
			if (getBlockType(firstNotAirBlock) == BlockType.SOLID)
				break;
		}
		firstNotAirBlock[Coords.Y.ordinal()]++;
		return firstNotAirBlock;
	}

	public boolean isPossiblePlaceToStand(Vec3 pos) {
		pos = roundVec(pos);
		int[] upperBlockLocation = { (int) pos.xCoord, (int) pos.yCoord + RELATIVE_HEAD_POS, (int) pos.zCoord};
		int[] blockLocation = { (int) pos.xCoord, (int) pos.yCoord + RELATIVE_FEET_POS, (int) pos.zCoord};
		if (getBlockType(blockLocation) == BlockType.NON_SOLID && getBlockType(upperBlockLocation) == BlockType.NON_SOLID)
			return true;
		return false;
	}
	
	private boolean isPossiblePlaceToBridge(Vec3 pos) {
		pos = roundVec(pos);
		int[] headLocation = { (int) pos.xCoord, (int) pos.yCoord + RELATIVE_HEAD_POS, (int) pos.zCoord};
		int[] feetLocation = { (int) pos.xCoord, (int) pos.yCoord + RELATIVE_FEET_POS, (int) pos.zCoord};
		int[] blockLocation = { (int) pos.xCoord, (int) pos.yCoord + RELATIVE_BLOCK_UNDER_POS, (int) pos.zCoord};
		return getBlockType(blockLocation) == BlockType.NON_SOLID && 
				getBlockType(headLocation) == BlockType.NON_SOLID && 
				getBlockType(feetLocation) == BlockType.NON_SOLID;
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
		if (block.getMaterial().isLiquid())
			return BlockType.LIQUID;
		if(
			block.getBlockHardness(
				theWorld, 
				blockLocation[Coords.X.ordinal()],
				blockLocation[Coords.Y.ordinal()],
				blockLocation[Coords.Z.ordinal()]) 
			< 0.01f)
				return BlockType.NON_SOLID;
		return BlockType.SOLID;
	}
}