package player_one;

import battlecode.common.*;

import static player_one.Utils.CURRENT_MAP_LOCATION;
import static player_one.Utils.isLocationBlocked;

public class Movable {
    /**
     * this is our intended destination. We persist this so we don't change our mind
     * if something new and shiny comes our way
     */
    static MapLocation destination = null;

    public static void moveTowardsDestination(RobotController rc) throws GameActionException {
        // if we don't have a destination. Do not move
        if(destination == null ) {
            throw new GameActionException(GameActionExceptionType.CANT_DO_THAT, "we don't have a destination");
        }
        checkIfWeHaveArrived();

        if(destination == null) return;

        rc.setIndicatorLine(CURRENT_MAP_LOCATION, destination, 0, 255, 0);

        if(!rc.isMovementReady()){
            throw new GameActionException(GameActionExceptionType.CANT_DO_THAT, "we are not ready to move");
        }


        MapLocation nextStep = AStar.astar(CURRENT_MAP_LOCATION, destination);
        Direction directionTowardsTarget = CURRENT_MAP_LOCATION.directionTo(nextStep);

        if(!rc.canMove(directionTowardsTarget)){
            rc.setIndicatorLine(CURRENT_MAP_LOCATION, destination, 255, 0, 0);
            System.out.println("cooldown" + rc.getMovementCooldownTurns());
            return;
        }

        rc.move(directionTowardsTarget);


    }

    public static boolean hasDestination(){
        return destination != null;
    }



    public static void setDestination(MapLocation newDestination){
        if(destination == null){
            destination = newDestination;
        }
    }

    public static void clearDestination(){
        destination = null;
    }

    public static void checkIfWeHaveArrived(){
        if(destination == null) return;
        if(destination.equals(CURRENT_MAP_LOCATION)) {
            clearDestination();
            return;
        }
        if(CURRENT_MAP_LOCATION.isAdjacentTo(destination) && isLocationBlocked(destination)){
            clearDestination();
        }
    }
}
