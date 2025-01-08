package player_one.robots;

import battlecode.common.*;
import player_one.Utils;

import static player_one.Utils.*;

public class Tower {

    static UnitType SPAWN_TYPE = UnitType.SOLDIER;
    /**
     * Run a single turn for towers.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    public static void run(RobotController rc) throws GameActionException {

        MapLocation spawnLocation = getSpawnLocation(rc);
        try{
            rc.buildRobot(SPAWN_TYPE, spawnLocation);
        }catch(GameActionException e){
            // we dont need to build robots
        }



        // Read incoming messages
        Message[] messages = rc.readMessages(-1);
        for (Message m : messages) {
            System.out.println("Tower received message: '#" + m.getSenderID() + " " + m.getBytes());
        }

        // TODO: can we attack other bots?
    }

    static MapLocation getSpawnLocation(RobotController rc) throws GameActionException {
        for(Direction dir : Utils.directions){
            MapLocation nextLoc = rc.getLocation().add(dir).add(dir);
            if (!rc.canBuildRobot(UnitType.SOLDIER, nextLoc)) {
                nextLoc = nextLoc.subtract(dir);
                rc.canBuildRobot(UnitType.SOLDIER, nextLoc);
            }
            return nextLoc;
        }

        throw new GameActionException(GameActionExceptionType.CANT_DO_THAT, "cannot spawn any units nearby");
    }
}
