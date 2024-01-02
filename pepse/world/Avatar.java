package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

/**
 * The Avatar class is a class for creating the avatar in the game.
 */
public class Avatar extends GameObject {

    /**
     * constance representing the avatar tag
     */
    private static final String AVATAR_TAG = "avatar";
    /**
     * list of constants representing the avatar images:
     */
    private static final String ASSETS_AVATAR_WALK_1_PNG = "assets/avatar/AvatarWalk1.png";
    private static final String ASSETS_AVATAR_WALK_2_PNG = "assets/avatar/AvatarWalk2.png";
    private static final String ASSETS_AVATAR_STAND_PNG = "assets/avatar/AvatarStand.png";
    /**
     * constance representing the avatar dimensions
     */
    public static final Vector2 AVATAR_DIMENSIONS = new Vector2(60, 60);
    /**
     * constance representing the time between clips
     */
    private static final double TIME_BETWEEN_CLIPS = 0.1;
    /**
     * constance representing the avatar acceleration speed
     */
    private static final int ACCELERATION_SPEED = 400;
    /**
     * constance representing the energy factor
     */
    private static final float ENERGY_FACTOR = 0.5f;
    /**
     * constance representing the avatar movement speed
     */
    private static final int MOVE_SPEED = 150;
    /**
     * constance representing the avatar max energy
     */
    private static final int MAX_ENERGY = 100;
    /**
     * constance representing the avatar jump factor
     */
    private static final int JUMP_FACTOR = 3;
    /**
     * constance representing the number 0
     */
    private static final int ZERO = 0;

    /**
     * standing image Renderable object
     */
    private final Renderable standingImage;
    /**
     * running animation object
     */
    private final Renderable runningRightImages;

    /**
     * energy value of the avatar
     */
    private float energy;
    /**
     * jumping sound of the avatar
     */
    private Sound jumpSound;
    private final UserInputListener inputListener;

    /**
     * Avatar constructor.
     * create an avatar object with initial MAX_ENERGY, and standing image.
     *
     * @param topLeftCorner Vector2 of the avatar location
     * @param inputListener UserInputListener object
     * @param imageReader   ImageReader object
     */
    public Avatar(Vector2 topLeftCorner, UserInputListener inputListener, ImageReader imageReader) {
        super(topLeftCorner, AVATAR_DIMENSIONS, null);
        this.standingImage = renderStandingImage(imageReader);
        this.runningRightImages = renderRunningImage(imageReader);
        this.inputListener = inputListener;
        this.jumpSound = null;
        this.energy = MAX_ENERGY;
        setStandingImage();
    }

    /**
     * this method creates the avatar game object and adds it to the game.
     *
     * @param gameObjects   the game objects collection
     * @param layer         an integer representing the layer that avatar object should be added to
     * @param topLeftCorner vector2 of the avatar position
     * @param inputListener UserInputListener object
     * @param imageReader   ImageReader object
     * @return the avatar game object
     */
    public static Avatar create(GameObjectCollection gameObjects, int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener, ImageReader imageReader) {

        Avatar avatar = new Avatar(topLeftCorner, inputListener, imageReader);
        avatar.setTag(AVATAR_TAG);
        avatar.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    }

    /**
     * this method sets the avatar jumping sound
     *
     * @param sound avatar jumping sound
     */
    public void setJumpingSound(Sound sound) {
        this.jumpSound = sound;
    }

    /**
     * private method that render the avatar running images and return their animation render
     *
     * @param imageReader ImageReader object
     * @return animation object of the avatar running images
     */
    private Renderable renderRunningImage(ImageReader imageReader) {
        Renderable runningImage1 = imageReader.readImage(ASSETS_AVATAR_WALK_1_PNG, true);
        Renderable runningImage2 = imageReader.readImage(ASSETS_AVATAR_WALK_2_PNG, true);
        return new AnimationRenderable(
                new Renderable[]{runningImage1, standingImage, runningImage2, standingImage},
                TIME_BETWEEN_CLIPS);
    }

    /**
     * private method that render the avatar standing image
     *
     * @param imageReader ImageReader object
     * @return Renderable object of the avatar standing image
     */
    private static Renderable renderStandingImage(ImageReader imageReader) {
        return imageReader.readImage(ASSETS_AVATAR_STAND_PNG, true);
    }

    /**
     * private method that sets the avatar moving direction
     *
     * @return Vector2 of the avatar movement direction
     */
    private Vector2 setMovementDirection() {
        transform().setAcceleration(ZERO, ACCELERATION_SPEED);
        Vector2 movementDir = leftOrRightMovement();
        movementDir = jumpOrFlyMovement(movementDir);
        return movementDir;
    }

    /**
     * private method that sets the avatar moving direction to jump of fly according to the user
     * input key pressed
     *
     * @param movementDir Vector2 of the avatar given movement direction
     * @return Vector2 of the avatar movement direction
     */
    private Vector2 jumpOrFlyMovement(Vector2 movementDir) {
        boolean spacePressed = inputListener.isKeyPressed(KeyEvent.VK_SPACE);
        boolean jumpOperation = spacePressed && transform().getVelocity().y() == ZERO;
        if (jumpOperation) {
            jumpSound.play();
            return new Vector2(movementDir.x(), -MOVE_SPEED * JUMP_FACTOR);
        }
        boolean flyOperation = spacePressed && inputListener.isKeyPressed(KeyEvent.VK_SHIFT) && energy > ZERO;
        if (flyOperation) {
            energy -= ENERGY_FACTOR;
            transform().setAccelerationY(ZERO);
            return new Vector2(movementDir.x(), -MOVE_SPEED);
        }
        return new Vector2(movementDir.x(), getVelocity().y());
    }

    /**
     * private method that sets the avatar moving direction to right or left according to the user
     * input key pressed
     *
     * @return Vector2 of the avatar movement direction
     */
    private Vector2 leftOrRightMovement() {
        Vector2 movementDir = Vector2.ZERO;
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            movementDir = new Vector2(Vector2.LEFT.mult(MOVE_SPEED));
            setRunningLeftRender();
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            movementDir = new Vector2(Vector2.RIGHT.mult(MOVE_SPEED));
            setRunningRightRender();
        }
        return movementDir;
    }

    /**
     * private method that sets the avatar energy lever while he is not moving
     */
    private void noMovement() {
        if (transform().getVelocity().y() == ZERO && transform().getVelocity().x() == ZERO) {
            setStandingImage();
            if (energy < MAX_ENERGY) {
                energy += ENERGY_FACTOR;
            }
        }
    }

    /**
     * private method that sets the avatar to his standing image
     */
    private void setStandingImage() {
        this.renderer().setRenderable(standingImage);
    }

    /**
     * private method that sets the avatar to his running animation to the right direction
     */
    private void setRunningRightRender() {
        this.renderer().setIsFlippedHorizontally(false);
        this.renderer().setRenderable(runningRightImages);

    }

    /**
     * private method that sets the avatar to his running animation to the left direction
     */
    private void setRunningLeftRender() {
        if (!this.renderer().isFlippedHorizontally()) {
            this.renderer().setIsFlippedHorizontally(true);
        }
        this.renderer().setRenderable(runningRightImages);
    }

    /**
     * this method set the avatar velocity to zero while he collide with the ground
     *
     * @param other     The GameObject with which a collision occurred.
     * @param collision Information regarding this collision
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        transform().setVelocity(getVelocity().flipped(collision.getNormal()));
        setVelocity(Vector2.ZERO);
    }

    /**
     * this method updates the avatar while the game runs, in particular the avatar movement.
     *
     * @param deltaTime used in sup method
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        setVelocity(setMovementDirection());
        noMovement();
    }
}
