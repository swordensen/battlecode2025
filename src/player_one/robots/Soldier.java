package player_one.robots;

import battlecode.common.*;
import player_one.TowerPatterns;
import player_one.Utils;

import static player_one.Utils.*;


public class Soldier {
    static MapLocation target = null;
    /**
     * Run a single turn for a Soldier.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    public static void run(RobotController rc) throws GameActionException {
        // Sense information about all visible nearby tiles.
        checkTarget();
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

                            setTarget(patternTileLocation);
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
                setTarget(closestEnemyRobot.location);
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
                    setTarget(nearbyPaintableTile);

                    if (rc.canAttack(nearbyPaintableTile)) {
                        rc.attack(nearbyPaintableTile);
                    }
                }
            }
            if(target != null){
                Utils.moveTowards(rc, target);
            } else {
                moveAndAttackRandomly(rc);

            }

        } catch (GameActionException e) {
            e.printStackTrace();
            moveAndAttackRandomly(rc);
        }
        if(target != null){
            rc.setIndicatorLine(CURRENT_MAP_LOCATION, target, 0, 255, 0);
        }

    }

    public static void setTarget(MapLocation newTarget){
        if(target == null){
            target = newTarget;
        }
    }

    public static void clearTarget(){
        target = null;
    }

    public static void checkTarget(){
        if(target != null && CURRENT_MAP_LOCATION.isAdjacentTo(target)){
            clearTarget();
        }
    }

    public static void moveAndAttackRandomly(RobotController rc) throws GameActionException {
        Direction dir = directions[rng.nextInt(directions.length)];
        if (rc.canMove(dir)) {
            rc.move(dir);
        }
        // Try to paint beneath us as we walk to avoid paint penalties.
        // Avoiding wasting paint by re-painting our own tiles.
        MapInfo currentTile = rc.senseMapInfo(rc.getLocation());
        if (currentTile.getPaint() == PaintType.EMPTY && rc.canAttack(rc.getLocation())) {
            rc.attack(rc.getLocation());
        }
    }
}
