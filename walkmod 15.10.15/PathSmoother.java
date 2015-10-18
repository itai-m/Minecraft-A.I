package com.custommods.walkmod;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.vecmath.Vector3d;

import com.custommods.walkmod.Step.StepType;

import net.minecraft.util.Vec3;

class PathSmoother {
	private static final double PLAYER_WIDTH = 1;

	private static final PathSmoother instance;

	static {
		instance = new PathSmoother();
	}

	public static PathSmoother getInstance() {
		return instance;
	}

	private PathSmoother() {
	}

	public void smoothPath(Queue<Step> path) {
		WalkMod.logger.info("Smoothing the path.");
		if(path.size() < 3)
			return;
		Step[] steps = new Step[3];
		int oldPathSize;
		do {
			oldPathSize = path.size();
			Step firstStep = path.peek();
			do {
				steps[0] = path.poll();
				if (path.peek() == firstStep) {
					path.add(steps[0]);
					continue;
				}
				steps[1] = path.poll();
				if (path.peek() == firstStep) {
					path.add(steps[0]);
					path.add(steps[1]);
					continue;
				}
				steps[2] = path.peek();

				boolean canSmooth = microSmooth(steps);
				path.add(steps[0]);
				if(!canSmooth)
					path.add(steps[1]);
			} while (path.peek() != firstStep && path.size() >= 3);
		} while (oldPathSize > path.size() && path.size() >= 3);
		//if the size is 2, it means the first step was left at the end
		if(path.size() == 2) {
			path.add(path.poll()); //pop the first and push it to the back
		}
	}
	
//	public static void main(String[] args) {
//		LinkedList<Step> path = new LinkedList<Step>();
//		Step parent = null;
//		for(int i = 0 ; i < 15 ; i++) {
//			path.add(parent = 
//					new Step(parent, StepType.Walk, 1, Vec3.createVectorHelper(i,0,i)));
//		}
//		PathSmoother.getInstance().smoothPath(path);
//		
//		for(Step step : path) {
//			////System.out.println(step.getLocation());
//		}
//	}
	
	private boolean microSmooth(Step[] steps) {
		//don't smooth falls or jumps. originally supported falls,
		//but sometimes that means going through the floor
		//we check that all three are of the same height
		if(steps[0].getLocation().yCoord != steps[2].getLocation().yCoord || steps[0].getLocation().yCoord != steps[1].getLocation().yCoord)
			return false;
		
		// use middle of blocks, not corners
		Vec3 startPos = Vec3.createVectorHelper(steps[0].getLocation().xCoord+.5, steps[0].getLocation().yCoord, steps[0].getLocation().zCoord+.5);
		Vec3 endPos = Vec3.createVectorHelper(steps[2].getLocation().xCoord+.5, steps[2].getLocation().yCoord, steps[2].getLocation().zCoord+.5);
		
		double xDelta = endPos.xCoord-startPos.xCoord;
		double zDelta = endPos.zCoord-startPos.zCoord;
		
		if(xDelta == 0 && zDelta == 0) //then don't put any of the two steps back
			return true;
		
		int majorAxis = Math.abs(xDelta) > Math.abs(zDelta) ? 0 : 2;
		int minorAxis = 2 - majorAxis;
		
		double lineA, lineB;
		lineA = majorAxis == 0 ? zDelta/xDelta : xDelta/zDelta;
		lineB = getCoord(startPos, minorAxis) - lineA
				* getCoord(startPos, majorAxis);
		// cos^2 = 1/(tan^2+1)
		// lineA := tan 
		double cosAlpha = Math.sqrt(1 / (lineA * lineA + 1));

		int minMajor = (int)Math.floor(Math.min(getCoord(endPos, majorAxis),getCoord(startPos, majorAxis))); 
		int maxMajor = (int)Math.floor(Math.max(getCoord(endPos, majorAxis),getCoord(startPos, majorAxis)));

		for(int majorI = minMajor ; majorI <= maxMajor ; majorI++) {
			double minorCoordBegin = lineA * majorI + lineB;
			double minorCoordEnd = lineA * (majorI+1) + lineB;
				
			//ask minecraft whether (majorI, floor(minorCoordBegin))
			//is at the same height
			if(MinecraftWorldInfo.checkFirstNotAirBlock(
					createVec3WithMajor(majorAxis, majorI, Math.floor(minorCoordBegin), startPos.yCoord)).yCoord !=
				startPos.yCoord) {
				return false;				
			}
			
			double effectiveWidthDiv2 = PLAYER_WIDTH / cosAlpha / 2;
			//the minimal and maximal coord (in the minor axis) of the player when he passes through majorI to majorI+1
			double minorCoordMin = Math.min(minorCoordBegin, minorCoordEnd);
			double minorCoordMax = Math.max(minorCoordBegin, minorCoordEnd);
			//the same, after accounting for player width
			double minorIBottom = Math.min(minorCoordBegin, minorCoordEnd)-effectiveWidthDiv2;
			double minorITop = Math.max(minorCoordBegin,minorCoordEnd)+effectiveWidthDiv2;

			//edge condition where going in a straight line, which otherwise results in cheking two blocks for each majorI
			if(lineA == 0)
				minorITop--;
			
			for(int minorI = (int)Math.floor(minorIBottom) ; minorI <= (int)Math.floor(minorITop) ; minorI++) {
				Vec3 coordsToCheck = createVec3WithMajor(majorAxis, majorI, minorI, startPos.yCoord);
				if(!isVecInRange(coordsToCheck, steps[0].getLocation(), steps[2].getLocation()))
					continue;
				if (!MinecraftWorldInfo.getInstance().isPossiblePlaceToStand(coordsToCheck)) {
					return false;
				}
			}
		}
		return true;
	}
	
	private static Vec3 createVec3WithMajor(int majorAxis, double major, double minor, double y){
		boolean majorIsX = majorAxis == 0;
		return Vec3.createVectorHelper(majorIsX? major : minor, y, majorIsX? minor : major);
	}
	
	private static boolean isVecInRange(Vec3 vec, Vec3 start, Vec3 end) {
		if(vec.xCoord < Math.min(start.xCoord, end.xCoord) || vec.xCoord > Math.max(start.xCoord, end.xCoord))
			return false;
		if(vec.yCoord < Math.min(start.yCoord, end.yCoord) || vec.yCoord > Math.max(start.yCoord, end.yCoord))
			return false;
		if(vec.zCoord < Math.min(start.zCoord, end.zCoord) || vec.zCoord > Math.max(start.zCoord, end.zCoord))
			return false;
		return true;
	}
	private static double getCoord(Vec3 vec, int coord) {
		switch (coord) {
		case 0:
			return vec.xCoord;
		case 1:
			return vec.yCoord;
		case 2:
			return vec.zCoord;
		default:
			return 0;
		}
	}

	private double getCoord(Step s, int coord) {
		return getCoord(s.getLocation(), coord);
	}
}