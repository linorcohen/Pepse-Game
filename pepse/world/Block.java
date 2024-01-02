package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * The Block class is a class for creating a block in the game
 */
public class Block extends GameObject {

    /**
     * Constance representing the block size
     */
    public static final int SIZE = 30;

    /**
     * Block constructor.
     *
     * @param topLeftCorner the position of the block
     * @param renderable    render object that represent the visual block
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }

}
