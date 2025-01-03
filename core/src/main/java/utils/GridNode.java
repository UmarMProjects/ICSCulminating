package utils;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.utils.Array;



public class GridNode {
    private int x, y;
    private boolean walkable;
    private Array<Connection<GridNode>> connections;

    public GridNode(int x, int y, boolean walkable) {
        this.x = x;
        this.y = y;
        this.walkable = walkable;
        connections = new Array<>();
    }

    public void addConnection(GridNode toNode) {
        if (toNode.isWalkable()) {
            connections.add(new DefaultConnection<>(this, toNode));
        }
    }

    public boolean isWalkable() {
        return walkable;
    }

    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }

    public Array<Connection<GridNode>> getConnections() {
        return connections;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}