package player_one;


import battlecode.common.*;

import java.util.*;

import static player_one.Utils.directions;

class Node implements Comparable<Node> {
    MapLocation location;
    double g, h;
    Node parent;

    Node(MapLocation location, double g, double h, Node parent){
        this.location = location;
        this.g = g;
        this.h = h;
        this.parent = parent;
    }

    double f(){
        return g+h;
    }

    @Override
    public int compareTo(Node other){
        return Double.compare(this.f(), other.f());
    }
}


 public class AStar{
    static final double DIAGONAL_STEP_COST = 1.414;
    static final double STEP_COST = 1;

    static double heuristic(MapLocation currentLocation, MapLocation targetLocation){
        int dx = Math.abs(currentLocation.x - targetLocation.x);
        int dy = Math.abs(currentLocation.y - targetLocation.y);
        return STEP_COST * (dx + dy) + (DIAGONAL_STEP_COST - 2 * STEP_COST) * Math.min(dx, dy);
    }

    static List<Node> getNeighbors(Node node, int mapWidth, int mapHeight, RobotController rc) throws GameActionException {
        List<Node> neighbors = new ArrayList<>();

        for(Direction dir: directions){
            MapLocation nextLoc = node.location.add(dir);
            if(nextLoc.x >= 0 && nextLoc.x < mapWidth && nextLoc.y >= 0 && nextLoc.y < mapHeight ){
                boolean isPassable = rc.canSenseLocation(nextLoc) ? rc.senseMapInfo(nextLoc).isPassable() : true;
                if(isPassable){
                    neighbors.add(new Node(nextLoc, node.g + 1, 0, null));
                }
            }
        }
        return neighbors;
    }

    static MapLocation getNextLocation(Node finalNode, MapLocation startLocation){
        while(finalNode.parent != null){
            if(finalNode.parent.location.equals(startLocation)){
                return finalNode.location;
            }
            finalNode = finalNode.parent;
        }
        return startLocation;
    }

    public static MapLocation astar( MapLocation start, MapLocation target, RobotController rc) throws GameActionException{
        PriorityQueue<Node> openList = new PriorityQueue<>();
        Map<String,Node> allNodes = new HashMap<>();

        int mapWidth = rc.getMapWidth();
        int mapHeight = rc.getMapHeight();

        Node startNode = new Node(start, 0 , heuristic(start, target), null);
        openList.add(startNode);
        allNodes.put(startNode.location.toString(), startNode);

        while(!openList.isEmpty()){

            Node current = openList.poll();

            if(current.location.equals(target) || Clock.getBytecodeNum() > 10000){
                return getNextLocation(current, start);
            }

            for(Node neighbor : getNeighbors(current, mapWidth, mapHeight,rc)){
                neighbor.h = heuristic(neighbor.location, target);
                neighbor.parent = current;
                String key = neighbor.location.toString();
                if(!allNodes.containsKey(key) || neighbor.g < allNodes.get(key).g){
                    allNodes.put(key, neighbor);
                    openList.add(neighbor);
                }
            }
        }
        throw new GameActionException(GameActionExceptionType.CANT_DO_THAT, "No path found!");
    }
    
}