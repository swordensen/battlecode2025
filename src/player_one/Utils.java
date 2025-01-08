package player_one;

import battlecode.common.*;
import battlecode.schema.RobotType;

import java.util.HashMap;
import java.util.Random;

import static player_one.MAP_DATA.NOT_VISITED;
import static player_one.MAP_DATA.VISITED;

enum MAP_DATA {
    NOT_VISITED,
    VISITED,
    WALL,
    RUIN,
    FRIENDLY_TOWER,
    ENEMY_TOWER
}



public class Utils {

    /**
     * A random number generator.
     * We will use this RNG to make some random moves. The Random class is provided by the java.util.Random
     * import at the top of this file. Here, we *seed* the RNG with a constant number (6147); this makes sure
     * we get the same sequence of numbers every time this code is run. This is very useful for debugging!
     */
    public static final Random rng = new Random(6147);
    public static int MAP_HEIGHT = 0;
    public static int MAP_WIDTH = 0;

    public static int[][] MAP_GRID = null;
    public static int[][] ROBOT_MAP_GRID = null;

    public static RobotInfo[] NEARBY_ROBOTS = null;
    public static MapInfo[] NEARBY_MAP_INFOS = null;

    public static MapLocation CURRENT_MAP_LOCATION = null;

    public static int CURRENT_PAINT_STASH = 0;
    public static Team MY_TEAM = null;
    public static HashMap<Integer, MapLocation> REFILL_TOWERS = new HashMap<Integer, MapLocation>();

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

    public static void initializeMapGrid(RobotController rc){
        MAP_HEIGHT = rc.getMapHeight();
        MAP_WIDTH = rc.getMapWidth();
        MAP_GRID = new int[MAP_WIDTH][MAP_HEIGHT];
        MY_TEAM = rc.getTeam();
    }

    public static void senseNearby(RobotController rc){
        ROBOT_MAP_GRID = new int[MAP_WIDTH][MAP_HEIGHT];
        NEARBY_ROBOTS = rc.senseNearbyRobots();
        NEARBY_MAP_INFOS = rc.senseNearbyMapInfos();
        CURRENT_MAP_LOCATION = rc.getLocation();
        CURRENT_PAINT_STASH = rc.getPaint();
        for(MapInfo mapInfo : NEARBY_MAP_INFOS){
            MapLocation mapLocation = mapInfo.getMapLocation();
            if(MAP_GRID[mapLocation.x][mapLocation.y] == MAP_DATA.NOT_VISITED.ordinal()){
                if(mapInfo.isWall()){
                    MAP_GRID[mapLocation.x][mapLocation.y] = MAP_DATA.WALL.ordinal();
                } else if(mapInfo.hasRuin()){
                    MAP_GRID[mapLocation.x][mapLocation.y] = MAP_DATA.RUIN.ordinal();
                }
            }
        }

        for(RobotInfo robotInfo : NEARBY_ROBOTS){
            MapLocation mapLocation = robotInfo.location;
//            we have to add 1 here because the soldier robot type ordinal is 0 which is default
            ROBOT_MAP_GRID[mapLocation.x][mapLocation.y] = robotInfo.getType().ordinal() + 1;
            if(robotInfo.type == UnitType.LEVEL_ONE_PAINT_TOWER && robotInfo.team == MY_TEAM){
                if(REFILL_TOWERS.containsKey(robotInfo.getID())){
                    REFILL_TOWERS.
                }
            }
        }
    }

    public static boolean isLocationBlocked(MapLocation location){
        int mapGridValue = MAP_GRID[location.x][location.y];

        if(mapGridValue == 0){
            int robotMapGridValue = ROBOT_MAP_GRID[location.x][location.y];
            return robotMapGridValue != 0;
        }
        return true;
    }



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



    public static void moveTowards(RobotController rc, MapLocation target) throws GameActionException {
        MapLocation currentLocation = rc.getLocation();
        MapLocation nextStep = AStar.astar(currentLocation, target);
        Direction directionTowardsTarget = currentLocation.directionTo(nextStep);

        if(rc.canMove(directionTowardsTarget)){
            rc.move(directionTowardsTarget);
        }


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
