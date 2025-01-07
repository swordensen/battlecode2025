package player_one.robots;

import battlecode.common.*;
import player_one.TowerPatterns;
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
            MapInfo[] tilesToPaint = rc.senseNearbyMapInfos(targetLoc, 8);


            boolean iThinkPatternIsFinished = true;
            // Fill in any spots in the pattern with the appropriate paint.
            for (MapInfo patternTile : tilesToPaint){
                MapLocation patternTileLocation = patternTile.getMapLocation();
                int x = targetLoc.x - patternTileLocation.x + 2;
                int y = targetLoc.y - patternTileLocation.y + 2;
                PaintType intendedPaintValue = TowerPatterns.LEVEL_ONE_PAINT_TOWER_PATTERN[x][y];
                if(intendedPaintValue == PaintType.EMPTY){
                    continue;
                }
                PaintType actualPaintValue = patternTile.getPaint();
                if (intendedPaintValue != actualPaintValue){

                    boolean useSecondaryColor = intendedPaintValue == PaintType.ALLY_SECONDARY;
                    if (rc.canAttack(patternTileLocation)){
                        rc.attack(patternTileLocation, useSecondaryColor);
                    } else {
                        Utils.moveTowards(rc, patternTileLocation);
                    }
                    iThinkPatternIsFinished = false;
                    break;
                }
            }


            // Complete the ruin if we can.
            if (iThinkPatternIsFinished && rc.canCompleteTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc)){
                rc.completeTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc);
                rc.setTimelineMarker("Tower built", 0, 255, 0);
                System.out.println("Built a tower at " + targetLoc + "!");
            } else {
                Utils.moveTowards(rc, targetLoc);
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
