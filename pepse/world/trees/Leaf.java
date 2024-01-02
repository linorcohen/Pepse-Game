package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.util.Random;

/**
 * The Leaf class is a class for creating a leaf in the game
 */
public class Leaf extends GameObject {

    /**
     * Constance representing the leaf tag
     */
    private static final String LEAF_TAG = "leaf";
    /**
     * Constance representing the leaf size
     */
    public static final float SIZE = 30;
    /**
     * Constance representing the initial leaf degree
     */
    private static final float INITIAL_LEAF_DEGREE = 180;
    /**
     * Constance representing the final leaf degree
     */
    private static final float FINAL_LEAF_DEGREE = 190;
    /**
     * Constance representing the leaf movement cycle length
     */
    private static final int MOVEMENT_CYCLE_LENGTH = 1;
    /**
     * Constance representing the minimum leaf size
     */
    private static final float MIN_LEAF_SIZE = 20;
    /**
     * Constance representing the leaf fade out time
     */
    private static final float FADEOUT_TIME = 10;
    /**
     * Constance representing the random maximum value
     */
    private static final int RANDOM_BOND = 200;
    /**
     * Constance representing the leaf initial velocity in the x direction
     */
    private static final float LEAF_INITIAL_VELOCITY_X = 30;
    /**
     * Constance representing the leaf velocity in the y direction
     */
    private static final int LEAF_Y_VELOCITY = 50;
    /**
     * Constance representing the leaf final velocity in the x direction
     */
    private static final float LEAF_FINAL_VELOCITY_X = -30;
    /**
     * Constance representing the leaf falling cycle length
     */
    private static final int FALLING_CYCLE_LENGTH = 2;
    /**
     * Constance representing the leaf reborn random bond
     */
    private static final int REBORN_RANDOM_BOND = 5;
    /**
     * Constance representing the number 0
     */
    private static final int ZERO = 0;
    /**
     * Constance representing the leaf fade in time
     */
    private static final int FADE_IN_TIME = 3;

    /**
     * random object
     */
    private static final Random rand = new Random();
    /**
     * movement transition object
     */
    private Transition<Float> movementTransition;
    private final Vector2 topLeftCorner;


    /**
     * Construct a new Leaf instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     */
    public Leaf(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
        this.topLeftCorner = topLeftCorner;
        setTag(LEAF_TAG);
        setLeafProperties();
    }

    /**
     * private method that sets the leaf properties.
     */
    private void setLeafProperties() {
        setMovingScheduleTask();
        setSizeChangeScheduleTask();
        setLeafFadeOutScheduleTask();
    }

    /**
     * private method that set the leaf moving schedule task
     */
    private void setMovingScheduleTask() {
        new ScheduledTask(this,
                (float) Math.random(),
                true,
                getMovingTransition());
    }

    /**
     * private method that set the leaf size change schedule task
     */
    private void setSizeChangeScheduleTask() {
        new ScheduledTask(this,
                (float) Math.random(),
                true,
                getSizeTransition());
    }

    /**
     * private method that get the moving transition
     *
     * @return Runnable object of Transition
     */
    private Runnable getMovingTransition() {
        return () ->
                new Transition<>(
                        this,
                        (angle) -> renderer().setRenderableAngle(angle),
                        INITIAL_LEAF_DEGREE,
                        FINAL_LEAF_DEGREE,
                        Transition.LINEAR_INTERPOLATOR_FLOAT,
                        MOVEMENT_CYCLE_LENGTH,
                        Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                        null);
    }

    /**
     * private method that get the size change transition
     *
     * @return Runnable object of Transition
     */
    private Runnable getSizeTransition() {
        return () ->
                new Transition<>(
                        this,
                        (newLength) -> setDimensions(new Vector2(newLength, SIZE)),
                        SIZE,
                        MIN_LEAF_SIZE,
                        Transition.LINEAR_INTERPOLATOR_FLOAT,
                        MOVEMENT_CYCLE_LENGTH,
                        Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                        null);
    }

    /**
     * private method that sets the leaf fade out task schedule
     */
    private void setLeafFadeOutScheduleTask() {
        new ScheduledTask(
                this,
                rand.nextInt(RANDOM_BOND),
                false,
                () -> {
                    setLeafFallingMovement();
                    renderer().fadeOut(FADEOUT_TIME, getRebornScheduleTask());
                });
    }

    /**
     * private method that sets the leaf falling movement
     */
    private void setLeafFallingMovement() {
        movementTransition = new Transition<>(
                this,
                (horizontalVelocity) -> {
                    transform().setVelocityY(LEAF_Y_VELOCITY);
                    transform().setVelocityX(horizontalVelocity);
                },
                LEAF_INITIAL_VELOCITY_X,
                LEAF_FINAL_VELOCITY_X,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                FALLING_CYCLE_LENGTH,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
    }

    /**
     * private method that gets the reborn schedule task
     *
     * @return Runnable object of schedule task
     */
    private Runnable getRebornScheduleTask() {
        return () -> new ScheduledTask(
                this,
                rand.nextInt(REBORN_RANDOM_BOND),
                false,
                () -> {
                    transform().setTopLeftCorner(topLeftCorner);
                    renderer().setOpaqueness(ZERO);
                    renderer().fadeIn(FADE_IN_TIME);
                    setLeafFadeOutScheduleTask();
                });
    }

    /**
     * this method sets the leaves to collide with the ground blocks
     *
     * @param other the item the object collide with
     * @return true, if other is Block, else otherwise.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        return other instanceof Block;
    }

    /**
     * this method sets the leaf to stop when it collides with the ground blocks,
     * and remove its falling transition component.
     *
     * @param other     the item the object collide with
     * @param collision the collision
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        transform().setVelocity(getVelocity().flipped(collision.getNormal())); //<- to block better the fall
        transform().setVelocity(Vector2.ZERO);
        removeComponent(movementTransition);
    }
}
