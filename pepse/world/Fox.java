package pepse.world;

import danogl.GameObject;
import danogl.components.ScheduledTask;
import danogl.gui.ImageReader;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.Random;

/**
 * The Fox class. creates a jumping fox game object in the game
 */
public class Fox extends GameObject {

    /**
     * Constance representing the fox moving images paths:
     */
    private static final String ASSETS_FOX_W_1_PNG = "assets/fox/fox2.png";
    private static final String ASSETS_FOX_W_2_PNG = "assets/fox/fox3.png";
    private static final String ASSETS_FOX_W_3_PNG = "assets/fox/fox1.png";
    /**
     * Constance representing the fox standing images paths:
     */
    private static final String ASSETS_FOX_S_1_PNG = "assets/fox/fox4.png";
    private static final String ASSETS_FOX_S_2_PNG = "assets/fox/fox4_1.png";
    private static final String ASSETS_FOX_S_3_PNG = "assets/fox/fox4_2.png";

    /**
     * Constance representing the fox dimensions
     */
    public static final Vector2 FOX_DEFAULT_DIMENSIONS = new Vector2(50, 50);
    /**
     * Constance representing the time between clips animation
     */
    private static final double TIME_BETWEEN_CLIPS = 0.1;
    /**
     * Constance representing the random jump time bond
     */
    private static final int RANDOM_JUMP_BOUND = 7;
    /**
     * Constance representing the fox move speed
     */
    private static final int MOVE_SPEED = 200;
    /**
     * Constance representing the fox acceleration
     */
    private static final int ACCELERATION = 400;
    /**
     * Constance representing the number 0
     */
    private static final int ZERO = 0;

    /**
     * the standing render of the fox
     */
    private final Renderable standingRender;
    /**
     * the moving render of the fox
     */
    private final Renderable movingRender;
    /**
     * Random object
     */
    private final Random rand;

    /**
     * Fox Constructor.
     *
     * @param topLeftCorner Vector2 of the fox initial location
     * @param imageReader   ImageReader object
     * @param seed          an integer seed for the random object
     */
    public Fox(Vector2 topLeftCorner, ImageReader imageReader, int seed) {
        super(topLeftCorner, FOX_DEFAULT_DIMENSIONS, null);
        this.standingRender = getStandingAnimation(imageReader);
        this.movingRender = getMovingAnimation(imageReader);
        this.rand = new Random(seed);
        this.renderer().setRenderable(standingRender);
        setRandomJumpScheduleTask();
    }

    /**
     * private method that sets random jumping to the fox
     */
    private void setRandomJumpScheduleTask() {
        new ScheduledTask(this,
                rand.nextInt(RANDOM_JUMP_BOUND),
                true,
                () -> transform().setVelocity(new Vector2(transform().getVelocity().x(), -MOVE_SPEED))
        );
    }

    /**
     * private method that render the fox moving animation
     *
     * @return moving fox AnimationRenderable object
     */
    private static AnimationRenderable getMovingAnimation(ImageReader imageReader) {
        Renderable foxImage1 = imageReader.readImage(ASSETS_FOX_W_1_PNG, true);
        Renderable foxImage2 = imageReader.readImage(ASSETS_FOX_W_2_PNG, true);
        Renderable foxImage3 = imageReader.readImage(ASSETS_FOX_W_3_PNG, true);
        return new AnimationRenderable(new Renderable[]{foxImage1, foxImage2, foxImage3}, TIME_BETWEEN_CLIPS);
    }

    /**
     * private method that render the fox standing animation
     *
     * @return standing fox AnimationRenderable object
     */
    private static AnimationRenderable getStandingAnimation(ImageReader imageReader) {
        Renderable foxImage1 = imageReader.readImage(ASSETS_FOX_S_1_PNG, true);
        Renderable foxImage2 = imageReader.readImage(ASSETS_FOX_S_2_PNG, true);
        Renderable foxImage3 = imageReader.readImage(ASSETS_FOX_S_3_PNG, true);
        return new AnimationRenderable(new Renderable[]{foxImage1, foxImage2, foxImage3}, TIME_BETWEEN_CLIPS);
    }

    /**
     * this method updates the renderer of the fox, and sets its y-coordinate acceleration
     *
     * @param deltaTime used in super method
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        transform().setAcceleration(ZERO, ACCELERATION);
        updateRenderable();
    }

    /**
     * this private method updates the fox renderer according to his current velocity
     */
    private void updateRenderable() {
        Renderable renderable = renderer().getRenderable();
        float velocityY = transform().getVelocity().y();
        if (velocityY != ZERO && renderable != movingRender) {
            renderer().setRenderable(movingRender);
        } else if (velocityY == ZERO && renderable != standingRender) {
            renderer().setRenderable(standingRender);
        }
    }
}
