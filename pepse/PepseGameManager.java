package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.*;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;

import java.awt.*;

/**
 * the PepseGameManager class. responsible for creating the Pepse game.
 */
public class PepseGameManager extends GameManager {

    /**
     * Constance representing the sun halo color
     */
    private static final Color SUN_HALO_COLOR = new Color(255, 255, 0, 20);
    /**
     * Constance representing the window dimensions
     */
    private static final Vector2 WINDOW_DIMENSIONS_VECTOR = new Vector2(1400, 700);
    /**
     * Constance representing the avatar jump sound path
     */
    private static final String ASSETS_SOUNDS_JUMP_WAV = "assets/sounds/jump.wav";
    /**
     * Constance representing the game title
     */
    private static final String PEPSE_GAME_TITLE = "Pepse Game";
    /**
     * Constance representing the sky layer
     */
    private static final int SKY_LAYER = Layer.BACKGROUND;
    /**
     * Constance representing the tree layer
     */
    private static final int TREE_LAYER = Layer.STATIC_OBJECTS;
    /**
     * Constance representing the sun layer
     */
    private static final int SUN_LAYER = Layer.BACKGROUND + 2;
    /**
     * Constance representing the sun halo layer
     */
    private static final int SUN_HALO_LAYER = Layer.BACKGROUND + 3;
    /**
     * Constance representing the ground layer
     */
    private static final int GROUND_LAYER = Layer.STATIC_OBJECTS;
    /**
     * Constance representing the avatar layer
     */
    private static final int AVATAR_LAYER = Layer.STATIC_OBJECTS + 2;
    /**
     * Constance representing the leaf layer
     */
    private static final int LEAF_LAYER = Layer.STATIC_OBJECTS + 1;
    /**
     * Constance representing the animal layer
     */
    private static final int ANIMAL_LAYER = Layer.STATIC_OBJECTS + 3;
    /**
     * Constance representing the night layer
     */
    private static final int NIGHT_LAYER = Layer.FOREGROUND;
    /**
     * Constance representing the target framerate
     */
    private static final int TARGET_FRAMERATE = 50;
    /**
     * Constance representing the day cycle length
     */
    private static final int CYCLE_LENGTH = 30;
    /**
     * Constance representing the game seed
     */
    private static final int SEED = 0;
    /**
     * Constance representing the avatar initial x-coordinate
     */
    private static final int AVATAR_INITIAL_X_COORDS = 500;
    /**
     * Constance representing the initial world factor
     */
    private static final int INITIAL_WORLD_FACTOR = 2;
    /**
     * Constance representing the camera factor
     */
    private static final float CAMERA_FACTOR = 0.5f;
    /**
     * Constance representing the sun image
     */
    private static final String ASSETS_SUN_PNG = "assets/sun/sun_shiny.png";

    /**
     * the game Terrain object
     */
    private Terrain terrain;
    /**
     * the game Avatar object
     */
    private Avatar avatar;
    /**
     * the game Tree object
     */
    private Tree trees;
    /**
     * the game animals manager object
     */
    private Animals animalsManager;
    /**
     * the current right side minimum x-coordinate for infinityLoop update
     */
    private int rightSideMinX;
    /**
     * the current left side minimum x-coordinate for infinityLoop update
     */
    private int leftSideMinX;
    /**
     * InfinityLoop object
     */
    private InfinityLoop infinityLoop;

    private final Vector2 windowDimensions;
    private UserInputListener inputListener;
    private ImageReader imageReader;
    private SoundReader soundReader;


    /**
     * PepseGameManager Constructor.
     *
     * @param gameTitle        string of the game title
     * @param windowDimensions a Vector2 object representing the dimensions of the window
     */
    public PepseGameManager(String gameTitle, Vector2 windowDimensions) {
        super(gameTitle, windowDimensions);
        this.windowDimensions = windowDimensions;
    }

    /**
     * this method initialize the game by adding all objects of the game.
     *
     * @param imageReader      ImageReader object
     * @param soundReader      SoundReader object
     * @param inputListener    UserInputListener object
     * @param windowController WindowController object
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        this.inputListener = inputListener;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        this.rightSideMinX = (int) (windowDimensions.x() * INITIAL_WORLD_FACTOR);
        this.leftSideMinX = -rightSideMinX;

        windowController.setTargetFramerate(TARGET_FRAMERATE);

        addSkyToGame();
        addSunToGame();
        addGroundToGame();
        addNightToGame();
        addTreeToGame();
        addAvatarToGame();
        addAnimalsToGame();
        setLayersCollision();
        setCameraInGame(windowController);
        addInfinityLoopToGame();
    }

    /**
     * private method that adds the infinity world object to the game
     */
    private void addInfinityLoopToGame() {
        this.infinityLoop = new InfinityLoop(new RangeMutator[]{trees, terrain, animalsManager},
                rightSideMinX, leftSideMinX);
    }

    /**
     * private method that adds the camera object to the game, and sets it accordingly
     */
    private void setCameraInGame(WindowController windowController) {
        setCamera(new Camera(avatar, windowController.getWindowDimensions().mult(CAMERA_FACTOR).
                subtract(getAvatarLocation()),
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
    }

    /**
     * private method that sets the layer collision
     */
    private void setLayersCollision() {
        gameObjects().layers().shouldLayersCollide(LEAF_LAYER, GROUND_LAYER, true);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, GROUND_LAYER, true);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, LEAF_LAYER, true);
        gameObjects().layers().shouldLayersCollide(ANIMAL_LAYER, GROUND_LAYER, true);
    }

    /**
     * private method that gets an object initial y location in the game
     */
    private Vector2 getAvatarLocation() {
        return new Vector2(AVATAR_INITIAL_X_COORDS, terrain.groundHeightAt(AVATAR_INITIAL_X_COORDS)
                - Avatar.AVATAR_DIMENSIONS.y());
    }

    /**
     * private method that adds the animals to the game (now its only fox)
     */
    private void addAnimalsToGame() {
        this.animalsManager = new Animals(gameObjects(), imageReader, SEED,
                terrain::groundHeightAt, ANIMAL_LAYER);
        animalsManager.createInRange(leftSideMinX, rightSideMinX);

    }

    /**
     * private method that adds the avatar object to the game
     */
    private void addAvatarToGame() {
        this.avatar = Avatar.create(gameObjects(), AVATAR_LAYER, getAvatarLocation(),
                inputListener, imageReader);
        Sound sound = soundReader.readSound(ASSETS_SOUNDS_JUMP_WAV);
        avatar.setJumpingSound(sound);

    }

    /**
     * private method that adds the night object to the game
     */
    private void addNightToGame() {
        Night.create(gameObjects(), NIGHT_LAYER, windowDimensions, CYCLE_LENGTH);
    }

    /**
     * private method that adds the ground, blocks objects,  to the game
     */
    private void addGroundToGame() {
        this.terrain = new Terrain(gameObjects(), GROUND_LAYER, windowDimensions, SEED, imageReader);
        terrain.createInRange(leftSideMinX, rightSideMinX);

    }

    /**
     * private method that adds the Trees, tree objects, to the game
     */
    private void addTreeToGame() {
        this.trees = new Tree(gameObjects(), terrain::groundHeightAt, TREE_LAYER, LEAF_LAYER,
                SEED, imageReader);
        trees.createInRange(leftSideMinX, rightSideMinX);
    }

    /**
     * private method that adds the sun object to the game
     */
    private void addSunToGame() {
        GameObject sun = Sun.create(gameObjects(), SUN_LAYER, windowDimensions, CYCLE_LENGTH);
        sun.renderer().setRenderable(imageReader.readImage(ASSETS_SUN_PNG, true));
        addSunHaloToGame(sun);
    }

    /**
     * private method that adds the sun halo object to the game
     */
    private void addSunHaloToGame(GameObject sun) {
        SunHalo.create(gameObjects(), SUN_HALO_LAYER, sun, SUN_HALO_COLOR);
    }

    /**
     * private method that adds the sky object to the game
     */
    private void addSkyToGame() {
        Sky.create(gameObjects(), windowDimensions, SKY_LAYER);
    }

    /**
     * this method updates the wald to became infinity using the infinityLoop object
     *
     * @param deltaTime used in super method
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        infinityLoop.update(avatar.getCenter());
    }


    public static void main(String[] args) {
        new PepseGameManager(PEPSE_GAME_TITLE, WINDOW_DIMENSIONS_VECTOR).run();
    }
}
