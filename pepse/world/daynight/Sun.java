package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.function.Consumer;

/**
 * The Sun class is a class for creating a sun object in the game
 */
public class Sun {

    /**
     * Constance representing the sun tag
     */
    private static final String SUN_TAG = "sun";
    /**
     * Constance representing the initial sun degree value
     */
    private static final float INITIAL_DEGREE_VALUE = 160;
    /**
     * Constance representing the final sun degree value
     */
    private static final float FINAL_DEGREE_VALUE = 520;
    /**
     * Constance representing the A radius for the sine function
     */
    private static final int A_RADIUS = 600;
    /**
     * Constance representing the B radius for the sine function
     */
    private static final int B_RADIUS = 300;
    /**
     * Constance representing the sun size
     */
    private static final int SUN_SIZE = 110;
    /**
     * Constance representing the number two
     */
    private static final int TWO = 2;


    /**
     * this method creates the sun GameObject and adds to it the sun elliptic transition.
     *
     * @param gameObjects      the game objects collection
     * @param layer            An integer value representing the layer on which the sun should be displayed.
     * @param windowDimensions A Vector2 object representing the dimensions of the game window.
     * @param cycleLength      A float value representing the length of time it takes for the sun to complete
     *                         one full cycle of transitions.
     * @return the sun GameObject
     */
    public static GameObject create(
            GameObjectCollection gameObjects, int layer, Vector2 windowDimensions, float cycleLength) {
        GameObject sun = new GameObject(Vector2.ZERO, windowDimensions, new OvalRenderable(Color.YELLOW));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setDimensions(new Vector2(SUN_SIZE, SUN_SIZE));
        gameObjects.addGameObject(sun, layer);
        sun.setTag(SUN_TAG);
        setSunTransition(sun, cycleLength, windowDimensions);
        return sun;
    }

    /**
     * this private method that sets up a transition for the sun object.
     * The transition animates the sun's position in the game world by changing its center coordinates
     * over time in an elliptic shape.
     *
     * @param sun              the sun object
     * @param cycleLength      A float value representing the length of time it takes for the sun to complete
     *                         one full cycle of transitions.
     * @param windowDimensions A Vector2 object representing the dimensions of the game window.
     */
    private static void setSunTransition(GameObject sun, float cycleLength, Vector2 windowDimensions) {
        Consumer<Float> setSunPosition = getSunTransitionFunction(sun, windowDimensions);

        new Transition<>(
                sun,
                setSunPosition,
                INITIAL_DEGREE_VALUE,
                FINAL_DEGREE_VALUE,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null);
    }

    /**
     * private method that returns the sun elliptic movement function of transition.
     *
     * @param sun              the sun object
     * @param windowDimensions A Vector2 object representing the dimensions of the game window.
     * @return Consumer<Float> function of the sun elliptic movement
     */
    private static Consumer<Float> getSunTransitionFunction(GameObject sun, Vector2 windowDimensions) {
        return angleInSky -> {
            double angleInRadians = Math.toRadians(angleInSky);
            sun.setCenter(
                    new Vector2((float) Math.sin(angleInRadians) * A_RADIUS + (windowDimensions.x() / TWO),
                            (float) Math.cos(angleInRadians) * B_RADIUS + (windowDimensions.y() / TWO)));
        };
    }

}
