package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * The Night class is a class for creating a night effect in a game
 */
public class Night {

    /**
     * Constance representing the night object tag
     */
    private static final String NIGHT_TAG = "night";
    /**
     * Constance representing the midnight opacity
     */
    private static final float MIDNIGHT_OPACITY = 0.5f;
    /**
     * Constance representing the initial transition value
     */
    private static final float INITIAL_TRANSITION_VALUE = 0f;
    /**
     * Constance representing the number two
     */
    private static final int TWO = 2;

    /**
     * this method creates the night object.
     * It then calls the setNightTransition method to set the transition for the night sky's opaqueness.
     *
     * @param gameObjects      the game objects collection
     * @param layer            an integer representing the layer that night object should be added to
     * @param windowDimensions a Vector2 object representing the dimensions of the window
     * @param cycleLength      a float representing the length of the night sky's transition from
     *                         fully transparent to fully black
     * @return the night GameObject
     */
    public static GameObject create(
            GameObjectCollection gameObjects, int layer, Vector2 windowDimensions, float cycleLength) {
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions,
                new RectangleRenderable(Color.BLACK));
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(night, layer);
        night.setTag(NIGHT_TAG);
        setNightTransition(night, cycleLength);
        return night;
    }

    /**
     * private method that create the night transition.
     *
     * @param night       the night GameObject
     * @param cycleLength a float representing the length of the night sky's transition from
     *                    fully transparent to fully black
     */
    private static void setNightTransition(GameObject night, float cycleLength) {
        new Transition<>(
                night,
                night.renderer()::setOpaqueness,
                INITIAL_TRANSITION_VALUE,
                MIDNIGHT_OPACITY,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                cycleLength / TWO,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
    }

}
