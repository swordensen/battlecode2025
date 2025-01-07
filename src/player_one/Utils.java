package player_one;

import battlecode.common.*;

import java.util.Random;

public class Utils {

    /**
     * A random number generator.
     * We will use this RNG to make some random moves. The Random class is provided by the java.util.Random
     * import at the top of this file. Here, we *seed* the RNG with a constant number (6147); this makes sure
     * we get the same sequence of numbers every time this code is run. This is very useful for debugging!
     */
    public static final Random rng = new Random(6147);


    /** Array containing all the possible movement directions. */
    public static final Direction[] directions = {
            Direction.NORTH,
            Direction.NORTHEAST,
            Direction.EAST,
            Direction.SOUTHEAST,
            Direction.SOUTH,
            Direction.SOUTHWEST,
            Direction.WEST,
            Direction.NORTHWEST,
    };



    public static void updateEnemyRobots(RobotController rc) throws GameActionException {
        // Sensing methods can be passed in a radius of -1 to automatically
        // use the largest possible value.
        RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        if (enemyRobots.length != 0){
            rc.setIndicatorString("There are nearby enemy robots! Scary!");
            // Save an array of locations with enemy robots in them for possible future use.
            MapLocation[] enemyLocations = new MapLocation[enemyRobots.length];
            for (int i = 0; i < enemyRobots.length; i++){
                enemyLocations[i] = enemyRobots[i].getLocation();
            }
            RobotInfo[] allyRobots = rc.senseNearbyRobots(-1, rc.getTeam());
            // Occasionally try to tell nearby allies how many enemy robots we see.
            if (rc.getRoundNum() % 20 == 0){
                for (RobotInfo ally : allyRobots){
                    if (rc.canSendMessage(ally.location, enemyRobots.length)){
                        rc.sendMessage(ally.location, enemyRobots.length);
                    }
                }
            }
        }
    }



    public static Direction moveTowards(RobotController rc, MapLocation target) throws GameActionException {
        MapLocation currentLocation = rc.getLocation();
        Direction directionTowardsTarget = currentLocation.directionTo(target);
        if(rc.canMove(directionTowardsTarget)){
            rc.move(directionTowardsTarget);
        } else {
            int attempts = 0;
            while(attempts < 8){
                directionTowardsTarget = directionTowardsTarget.rotateRight();
                if(rc.canMove(directionTowardsTarget)){
                    rc.move(directionTowardsTarget);
                    return directionTowardsTarget;
                }
                attempts++;
            }
        }

        throw new GameActionException(GameActionExceptionType.CANT_DO_THAT, "I'm stuck :(");
    }

    public static RobotInfo getClosestRobot(RobotController rc, RobotInfo[] robots) throws GameActionException{
        MapLocation currentLocation = rc.getLocation();
        RobotInfo currentClosestRobot = null;
        int currentDistance = Integer.MAX_VALUE;
        for(RobotInfo robot : robots){
            MapLocation robotLocation = robot.location;
            int distance = currentLocation.distanceSquaredTo(robotLocation);
            if(distance < currentDistance){
                currentClosestRobot = robot;
                currentDistance = distance;
            }
        }
        return currentClosestRobot;
    }


}
