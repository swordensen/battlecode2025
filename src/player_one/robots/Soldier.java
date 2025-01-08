package player_one.robots;

import battlecode.common.*;
import player_one.Movable;
import player_one.TowerPatterns;
import player_one.Utils;

import static player_one.Utils.*;


public class Soldier  {
    /**
     * Run a single turn for a Soldier.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    public static void run(RobotController rc) throws GameActionException {

        // Sense information about all visible nearby tiles.
        MapInfo[] nearbyTiles = NEARBY_MAP_INFOS;
        RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        // Search for a nearby ruin to complete.
        MapInfo curRuin = null;
        for (MapInfo tile : nearbyTiles) {
            if (tile.hasRuin() && rc.senseRobotAtLocation(tile.getMapLocation()) == null) {
                curRuin = tile;
            }
        }
        try {
            if (curRuin != null) {
                MapLocation targetLoc = curRuin.getMapLocation();
                MapInfo[] tilesToPaint = rc.senseNearbyMapInfos(targetLoc, 8);


                boolean iThinkPatternIsFinished = true;
                // Fill in any spots in the pattern with the appropriate paint.
                for (MapInfo patternTile : tilesToPaint) {
                    MapLocation patternTileLocation = patternTile.getMapLocation();
                    int x = targetLoc.x - patternTileLocation.x + 2;
                    int y = targetLoc.y - patternTileLocation.y + 2;
                    PaintType intendedPaintValue = TowerPatterns.LEVEL_ONE_PAINT_TOWER_PATTERN[x][y];
                    if (intendedPaintValue == PaintType.EMPTY) {
                        continue;
                    }
                    PaintType actualPaintValue = patternTile.getPaint();
                    if(actualPaintValue == PaintType.ENEMY_PRIMARY || actualPaintValue == PaintType.ENEMY_SECONDARY){
                        iThinkPatternIsFinished = false;
                        continue;
                    }
                    if (intendedPaintValue != actualPaintValue) {

                        boolean useSecondaryColor = intendedPaintValue == PaintType.ALLY_SECONDARY;
                        if (rc.canAttack(patternTileLocation)) {
                            rc.attack(patternTileLocation, useSecondaryColor);
                        } else {

                            Movable.setDestination(patternTileLocation);
                        }
                        iThinkPatternIsFinished = false;
                        break;
                    }
                }


                // Complete the ruin if we can.
                if (iThinkPatternIsFinished && rc.canCompleteTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc)) {
                    rc.completeTowerPattern(UnitType.LEVEL_ONE_PAINT_TOWER, targetLoc);
                    rc.setTimelineMarker("Tower built", 0, 255, 0);
                    System.out.println("Built a tower at " + targetLoc + "!");
                }
            } else if (enemyRobots.length > 0) {
                RobotInfo closestEnemyRobot = Utils.getClosestRobot(rc, enemyRobots);
                Movable.setDestination(closestEnemyRobot.location);
                if (rc.canAttack(closestEnemyRobot.location)) {
                    rc.attack(closestEnemyRobot.location);
                }
            } else {
                MapLocation nearbyPaintableTile = null;
                for (MapInfo tile : nearbyTiles) {
                    MapLocation tileLocation = tile.getMapLocation();
                    PaintType paintType = tile.getPaint();
                    if (paintType == PaintType.EMPTY && !isLocationBlocked(tileLocation)) {
                        nearbyPaintableTile = tileLocation;
                        break;
                    }
                }

                if (nearbyPaintableTile != null) {
                    Movable.setDestination(nearbyPaintableTile);

                    if (rc.canAttack(nearbyPaintableTile)) {
                        rc.attack(nearbyPaintableTile);
                    }
                }
            }

            if(!Movable.hasDestination()){
                Direction dir = directions[rng.nextInt(directions.length)];
                Movable.setDestination(CURRENT_MAP_LOCATION.add(dir));
            }


            Movable.moveTowardsDestination(rc);

        } catch (GameActionException e) {
            e.printStackTrace();
        }


    }






}
