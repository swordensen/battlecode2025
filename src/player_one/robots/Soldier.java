package player_one.robots;

import battlecode.common.*;
import player_one.Utils;

import static player_one.Utils.directions;
import static player_one.Utils.rng;


public class Soldier {

    /**
     * Run a single turn for a Soldier.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    public static void run(RobotController rc) throws GameActionException {
        // Sense information about all visible nearby tiles.
        MapInfo[] nearbyTiles = rc.senseNearbyMapInfos();
        RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        // Search for a nearby ruin to complete.
        MapInfo curRuin = null;
        for (MapInfo tile : nearbyTiles){
            if (tile.hasRuin() && rc.senseRobotAtLocation(tile.getMapLocation()) == null) {
                curRuin = tile;
            }
        }

        if (curRuin != null){
            MapLocation targetLoc = curRuin.getMapLocation();
            Direction movementDirection = Utils.moveTowards(rc, targetLoc);

            // Mark the pattern we need to draw to build a tower here if we haven't already.
            MapLocation shouldBeMarked = curRuin.getMapLocation().subtract(movementDirection);
            if (rc.senseMapInfo(shouldBeMarked).getMark() == PaintType.EMPTY && rc.canMarkTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc)){
                rc.markTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc);
                System.out.println("Trying to build a tower at " + targetLoc);
            }
            // Fill in any spots in the pattern with the appropriate paint.
            for (MapInfo patternTile : rc.senseNearbyMapInfos(targetLoc, 8)){
                if (patternTile.getMark() != patternTile.getPaint() && patternTile.getMark() != PaintType.EMPTY){
                    boolean useSecondaryColor = patternTile.getMark() == PaintType.ALLY_SECONDARY;
                    if (rc.canAttack(patternTile.getMapLocation()))
                        rc.attack(patternTile.getMapLocation(), useSecondaryColor);
                }
            }
            // Complete the ruin if we can.
            if (rc.canCompleteTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc)){
                rc.completeTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc);
                rc.setTimelineMarker("Tower built", 0, 255, 0);
                System.out.println("Built a tower at " + targetLoc + "!");
            }
        } else if(enemyRobots.length > 0) {
            RobotInfo closestEnemyRobot = Utils.getClosestRobot(rc, enemyRobots);
            Utils.moveTowards(rc, closestEnemyRobot.location);
                if(rc.canAttack(closestEnemyRobot.location)){
                    rc.attack(closestEnemyRobot.location);
                }
            }   else  {
                // Move and attack randomly if no objective.
                Direction dir = directions[rng.nextInt(directions.length)];
                MapLocation nextLoc = rc.getLocation().add(dir);
                if (rc.canMove(dir)){
                    rc.move(dir);
                }
                // Try to paint beneath us as we walk to avoid paint penalties.
                // Avoiding wasting paint by re-painting our own tiles.
                MapInfo currentTile = rc.senseMapInfo(rc.getLocation());
                if (!currentTile.getPaint().isAlly() && rc.canAttack(rc.getLocation())){
                    rc.attack(rc.getLocation());
                }
            }
//        }


    }
}
