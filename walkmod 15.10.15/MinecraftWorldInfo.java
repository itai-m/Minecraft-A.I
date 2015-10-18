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

class MinecraftWorldInfo implements IWorldInfo {
	private enum BlockType {
		SOLID, NON_SOLID
	};

	private enum Coords {
		X, Y, Z, COUNT
	};

	private static final int MAX_NEIGHBORS = 8;
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

	@Override
	public double getMinimalDistance(Vec3 pos, Vec3 goal) {
		double yBackup = goal.yCoord;
		goal.yCoord = pos.yCoord;
		double dis = pos.distanceTo(goal);
		goal.yCoord = yBackup;
		return dis + Math.abs(pos.yCoord-goal.yCoord);
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
			}
		}

		return currNeighbors;
	}
	
	private static boolean checkIfDiagonalWalk(int[] parentPos, int[] newStepPos){
		return (parentPos[Coords.X.ordinal()] != newStepPos[Coords.X.ordinal()] &&
				parentPos[Coords.Z.ordinal()] != newStepPos[Coords.Z.ordinal()]) ? true : false;
	}
	private static boolean checkIfDiagonalWalkBlocked(Step parentStep, int[] newStepPos, int[] parentPos){
		int[] mutualNeighborPos1 = { newStepPos[Coords.X.ordinal()],
				newStepPos[Coords.Y.ordinal()],
				parentPos[Coords.Z.ordinal()]};
		int[] mutualNeighborPos2 = {
				parentPos[Coords.X.ordinal()],
				newStepPos[Coords.Y.ordinal()],
				newStepPos[Coords.Z.ordinal()] };

		return (getBlockType(mutualNeighborPos1) == BlockType.SOLID ||getBlockType(mutualNeighborPos2) == BlockType.SOLID) ? true : false;
	}
	
	private static Step getStepWithPos(Step parentStep, int[] parentPos,
			int[] newStepPos) {// //
		int[] targetPos = checkFirstNotAirBlock(newStepPos);
		
		Vec3 returnVec = Vec3.createVectorHelper(
				targetPos[Coords.X.ordinal()], targetPos[Coords.Y.ordinal()],
				targetPos[Coords.Z.ordinal()]);
		StepType type = StepType.Unassinged;
		
		//DIAGONAL WALK
		if(checkIfDiagonalWalk(parentPos, targetPos) && checkIfDiagonalWalkBlocked(parentStep, targetPos, parentPos)){
				return new Step(parentStep, Step.StepType.Unassinged, calcCost(
						parentPos, targetPos), returnVec);
		}

		if (parentPos[Coords.Y.ordinal()] >= targetPos[Coords.Y.ordinal()])
			type = StepType.Walk;
		else
				type = StepType.Jump;
		
		
		int[] newStepPosUp = {targetPos[Coords.X.ordinal()], targetPos[Coords.Y.ordinal()] + 1, targetPos[Coords.Z.ordinal()]};
		if (getBlockType(targetPos) == BlockType.SOLID || getBlockType(newStepPosUp) == BlockType.SOLID)
			return new Step(parentStep, Step.StepType.Unassinged, calcCost(
					parentPos, targetPos), returnVec);
		if(type == StepType.Jump){
			
			int[] posUpperParent = {parentPos[Coords.X.ordinal()], parentPos[Coords.Y.ordinal()] + 1, parentPos[Coords.Z.ordinal()]};
			//this condition was added in order to make sure that we won't get stuck on leaves (or any other object above you) while jumping.
			if (getBlockType(posUpperParent) == BlockType.SOLID)
				return new Step(parentStep, Step.StepType.Unassinged, calcCost(
						parentPos, targetPos), returnVec);
			
			//this condition is here to prevent unnatural jumps that is larger than one block
			 if (targetPos[Coords.Y.ordinal()] - parentPos[Coords.Y.ordinal()] > 1)
				 return new Step(parentStep, Step.StepType.Unassinged, calcCost(
							parentPos, targetPos), returnVec);
			
			int[] underStepPos = { targetPos[Coords.X.ordinal()],targetPos[Coords.Y.ordinal()] - 1, targetPos[Coords.Z.ordinal()]};
			return new Step(parentStep, Step.StepType.Jump, calcCost(
						parentPos, underStepPos) + 0.8, returnVec);
		}
		return new Step(parentStep, Step.StepType.Walk, calcCost(parentPos,
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

	// TO BE REMOVED WHEN MY OPTION IS AVAILABLE
	public boolean isPossiblePlaceToStand(Vec3 pos) {
		pos = roundVec(pos);
		int[] upperBlockLocation = { (int) pos.xCoord, (int) pos.yCoord + 1, (int) pos.zCoord};
		int[] blockLocation = { (int) pos.xCoord, (int) pos.yCoord, (int) pos.zCoord};
		if (getBlockType(blockLocation) == BlockType.NON_SOLID && getBlockType(upperBlockLocation) == BlockType.NON_SOLID)
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
		return /*theWorld.isBlockNormalCubeDefault(
				blockLocation[Coords.X.ordinal()],
				blockLocation[Coords.Y.ordinal()],
				blockLocation[Coords.Z.ordinal()], true) &&*/
				theWorld.getBlock(				
				blockLocation[Coords.X.ordinal()],
				blockLocation[Coords.Y.ordinal()],
				blockLocation[Coords.Z.ordinal()]).getBlockHardness(theWorld, blockLocation[Coords.X.ordinal()],
						blockLocation[Coords.Y.ordinal()],
						blockLocation[Coords.Z.ordinal()]) >= 0.01f				
				? BlockType.SOLID
				: BlockType.NON_SOLID;
	}
}
