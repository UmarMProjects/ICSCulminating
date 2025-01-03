package utils;


import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector2;


public class MyHeuristic implements Heuristic<GridNode> {
    @Override
    public float estimate(GridNode node, GridNode endNode) {
        return Vector2.dst(node.getX(), node.getY(), endNode.getX(), endNode.getY());
    }
}