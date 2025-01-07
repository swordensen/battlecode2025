import java.util.*;

class Node implements Comparable<Node> {
    int x, y;
    int g, h;
    Node parent;

    Node(int x, int y, int g, int h, Node parent){
        this.x = x;
        this.y = y;
        this.g = g;
        this.h = h;
        this.parent = parent;
    }

    int f(){
        return g+h;
    }

    @Override
    public int compareTo(Node other){
        return Int.compare(this.f(), other.f())
    }
}


public class AStar{
    static final double DIAGONAL_STEP_COST = 1.414;
    static final int STEP_COST = 1;

    static int heuristic(int x1, int y1, int x2, int y2){
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
        return STEP_COST * (dx + dy) + (DIAGONAL_STEP_COST - 2 * STEP_COST) * Math.min(dx, dy);
    }

    
}