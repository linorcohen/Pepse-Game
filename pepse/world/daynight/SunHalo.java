package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.Component;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * The SunHalo class is a class for creating a Sun halo effect in the game
 */
public class SunHalo {

    /**
     * Constance representing the sun halo tag
     */
    private static final String SUN_HALO_TAG = "sun halo";
    /**
     * Constance representing the sun halo size
     */
    private static final int SUN_HALO_SIZE = 230;

    /**
     * this method creates the sun halo game object. and attach it to track the center the sun object.
     *
     * @param gameObjects the game objects collection
     * @param layer       an integer representing the layer that sun halo object should be added to
     * @param sun         the sun object
     * @param color       the color of the sun
     * @return the sun halo GameObject
     */
    public static GameObject create(
            GameObjectCollection gameObjects, int layer, GameObject sun, Color color) {
        GameObject sunHalo = new GameObject(Vector2.ZERO, Vector2.ZERO,
                new OvalRenderable(color));
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sunHalo.setDimensions(new Vector2(SUN_HALO_SIZE, SUN_HALO_SIZE));
        gameObjects.addGameObject(sunHalo, layer);
        sunHalo.setTag(SUN_HALO_TAG);
        setSanHaloTransition(sunHalo, sun);
        return sunHalo;
    }

    /**
     * private method that sets the sun halo object transition ( set the center of the halo to
     * the center of the sun object.
     *
     * @param sunHalo the sun halo object
     * @param sun     the sun object
     */
    private static void setSanHaloTransition(GameObject sunHalo, GameObject sun) {
        Component setSanHaloCenter = deltaTime -> sunHalo.setCenter(sun.getCenter());
        sunHalo.addComponent(setSanHaloCenter);
    }

}

