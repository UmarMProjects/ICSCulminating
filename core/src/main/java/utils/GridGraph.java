package utils;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;




import entities.Platform;

import java.util.List;



public class GridGraph implements IndexedGraph<GridNode> {
    private GridNode[][] nodes;
    private int width;
    private int height;

    public GridGraph(int width, int height, List<Platform> platforms) {
        this.width = width;
        this.height = height;
        nodes = new GridNode[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                nodes[x][y] = new GridNode(x, y, true); // Initialize all nodes as non-walkable
            }
        }

        // Mark nodes as walkable based on platform positions
        for (Platform platform : platforms) {
            float platformX = platform.getBody().getPosition().x;
            float platformY = platform.getBody().getPosition().y;
            int nodeX = Math.round(platformX);
            int nodeY = Math.round(platformY);

            // Mark the node as walkable
            if (nodeX >= 0 && nodeX < width && nodeY >= 0 && nodeY < height) {
                nodes[nodeX][nodeY].setWalkable(true);
                System.out.println("Marked walkable node: (" + nodeX + ", " + nodeY + ")");
            }
        }

        // Add only horizontal connections between nodes
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (nodes[x][y].isWalkable()) {
                    if (x > 0 && nodes[x - 1][y].isWalkable()) {
                        nodes[x][y].addConnection(nodes[x - 1][y]);
                        System.out.println("Horizontal connection added: (" + x + ", " + y + ") -> (" + (x - 1) + ", " + y + ")");
                    }
                    if (x < width - 1 && nodes[x + 1][y].isWalkable()) {
                        nodes[x][y].addConnection(nodes[x + 1][y]);
                        System.out.println("Horizontal connection added: (" + x + ", " + y + ") -> (" + (x + 1) + ", " + y + ")");
                    }
                }
            }
        }
    }

    @Override
    public int getIndex(GridNode node) {
        return node.getX() * height + node.getY();
    }

    @Override
    public int getNodeCount() {
        return width * height;
    }

    @Override
    public Array<Connection<GridNode>> getConnections(GridNode fromNode) {
        return fromNode.getConnections();
    }

    public GridNode getNode(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return nodes[x][y];
        }
        return null;
    }
}