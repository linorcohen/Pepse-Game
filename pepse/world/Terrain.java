package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The Terrain class is a class for creating the ground in the game.
 */
public class Terrain implements RangeMutator {

    /**
     * constance representing the ground tag
     */
    private static final String GROUND_TAG = "ground";
    /**
     * constance representing the ground color
     */
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    /**
     * constance representing the layer of the ground that does not collide with other objects
     */
    private static final int NO_COLLISION_GROUND_LAYER = Layer.BACKGROUND + 10;
    /**
     * constance representing the ground height factor (ratio to take from the ground height)
     */
    private static final float HEIGHT_FRACTION_FACTOR = 1.4f;
    /**
     * constance representing the number of ground layers that collide with other objects
     */
    private static final int NUMBER_OF_GROUND_COLLISION_LAYERS = 8;
    /**
     * constance representing the height limit factor
     */
    private static final float HEIGHT_LIMIT_FACTOR = 0.5f;
    /**
     * constance representing the terrain depth
     */
    private static final int TERRAIN_DEPTH = 20;
    /**
     * constance representing the sine function factor
     */
    private static final float SIN_FACTOR = 0.5f;
    /**
     * constance representing the minimum height gap
     */
    private static final int MIN_HEIGHT_GAP = 0;
    /**
     * constance representing the number 0
     */
    private static final int ZERO = 0;
    /**
     * constance representing the number 2
     */
    private static final int TWO = 2;


    /**
     * blocksMap is a map holding all the blocks in the ground according to their location and layer
     */
    private final Map<Integer, Map<GameObject, Integer>> blocksMap;
    private final GameObjectCollection gameObjects;
    private final Vector2 windowDimensions;
    private final int groundLayer;
    private final int seed;
    private Renderable blockRender = null;
    private Renderable topBlockRender = null;


    /**
     * Terrain Constructor.
     * creates normal ground with the block color
     *
     * @param gameObjects      the game objects collection
     * @param groundLayer      an integer representing the layer that block object should be added to
     * @param windowDimensions a Vector2 object representing the dimensions of the window
     * @param seed             an integer representing the seed for random creation
     */
    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed) {
        this.windowDimensions = windowDimensions;
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.seed = seed;
        this.blocksMap = new HashMap<>();
    }

    /**
     * Terrain Constructor.
     * this constructor gets the imageReader object, for the ability to set the ground in a nice
     * tiles with grass.
     *
     * @param gameObjects      the game objects collection
     * @param groundLayer      an integer representing the layer that block object should be added to
     * @param windowDimensions a Vector2 object representing the dimensions of the window
     * @param seed             an integer representing the seed for random creation
     * @param imageReader      ImageReader imageReader
     */
    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions,
                   int seed, ImageReader imageReader) {
        this.windowDimensions = windowDimensions;
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.seed = seed;
        this.blockRender = imageReader.readImage("assets/tiles/dirt.png", false);
        this.topBlockRender = imageReader.readImage("assets/tiles/dirt_grass.png", true);
        this.blocksMap = new HashMap<>();
    }

    /**
     * getter for the window height
     *
     * @return float of the window height
     */
    private float getHeight() {
        return windowDimensions.y();
    }

    /**
     * this method calculates the ground height at a given x-coordinate
     * if the ground is too low, it returns the right ratio
     *
     * @param x float of the x-coordinate
     * @return calculated height in the x-coordinate
     */
    public float groundHeightAt(float x) {
        float xHeight = randomHeightGenerator(x) * getHeight();
        float maxTreeHeight = TERRAIN_DEPTH * Block.SIZE;
        float heightGap = Math.max(getHeight() - maxTreeHeight - xHeight, MIN_HEIGHT_GAP);
        float xHeightWithGap = xHeight + heightGap;
        boolean isXHeightHigherThenHalfScreen = xHeightWithGap <= windowDimensions.y() * HEIGHT_LIMIT_FACTOR;
        if (isXHeightHigherThenHalfScreen) {
            // in case the ground is too low we want to have some boundaries, and make it higher
            return xHeightWithGap * HEIGHT_FRACTION_FACTOR;
        }
        return xHeightWithGap;
    }

    /**
     * private method that calculate a random height for the given x-coordinate, using the sine function
     *
     * @param x float of the x-coordinate
     * @return calculated random height of the x-coordinate
     */
    private float randomHeightGenerator(float x) {
        return (float) (SIN_FACTOR * ((Math.abs(Math.sin(x / Math.PI)) +
                Math.abs(Math.sin((TWO * x - seed) / Math.PI))) / TWO));
    }

    /**
     * this method creates the ground blocks in the given range
     *
     * @param minX minimum x-coordinate of creation
     * @param maxX maximum x-coordinate of creation
     */
    public void createInRange(int minX, int maxX) {
        int newMinX = (int) Math.ceil((float) minX / Block.SIZE) * Block.SIZE;
        int newMaxX = (int) Math.ceil((float) maxX / Block.SIZE) * Block.SIZE;

        for (int x = newMinX; x < newMaxX; x += Block.SIZE) {
            float y = groundHeightAt(x);
            Map<GameObject, Integer> blocksLayerMap = new HashMap<>();
            for (int j = ZERO; j < TERRAIN_DEPTH; j++) {
                GameObject singleBlock = new Block(new Vector2(x, (y + (j * Block.SIZE))),
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)));
                singleBlock.setTag(GROUND_TAG);
                setBlockToGameByLayer(j, singleBlock, blocksLayerMap);
                setGroundRender(j, singleBlock);
            }
            blocksMap.put(x, blocksLayerMap);
        }
    }

    /**
     * this private method adds a single block to the game.
     * in addition it adds the block to the correct column in the blocksMap
     *
     * @param blockNum       an integer of the block number in the tiles column
     * @param singleBlock    the block GameObject
     * @param blocksLayerMap the current blocks layer map
     */
    private void setBlockToGameByLayer(int blockNum, GameObject singleBlock,
                                       Map<GameObject, Integer> blocksLayerMap) {
        if (blockNum < NUMBER_OF_GROUND_COLLISION_LAYERS) {
            gameObjects.addGameObject(singleBlock, groundLayer);
            blocksLayerMap.put(singleBlock, groundLayer);
        } else {
            gameObjects.addGameObject(singleBlock, NO_COLLISION_GROUND_LAYER);
            blocksLayerMap.put(singleBlock, NO_COLLISION_GROUND_LAYER);
        }
    }

    /**
     * privat method that sets the block tiles render image.
     *
     * @param blockNum    an integer of the block number in the tiles column
     * @param singleBlock the block GameObject
     */
    private void setGroundRender(int blockNum, GameObject singleBlock) {
        if (blockRender != null) {
            if (blockNum == 0) { // first block has grass on it
                singleBlock.renderer().setRenderable(topBlockRender);
            } else {
                singleBlock.renderer().setRenderable(blockRender);
            }
        }
    }

    /**
     * this method removes the ground blocks in the given range
     *
     * @param minX minimum x-coordinate of removal
     * @param maxX maximum x-coordinate of removal
     */
    public void removeInRange(int minX, int maxX) {
        int newMinX = (int) Math.ceil((float) minX / Block.SIZE) * Block.SIZE;
        int newMaxX = (int) Math.ceil((float) maxX / Block.SIZE) * Block.SIZE;

        for (int x = newMinX; x < newMaxX; x += Block.SIZE) {
            for (Map.Entry<Integer, Map<GameObject, Integer>> entry : blocksMap.entrySet()) {
                if (entry.getKey() == x) {
                    for (Map.Entry<GameObject, Integer> objectEntry : entry.getValue().entrySet()) {
                        gameObjects.removeGameObject(objectEntry.getKey(), objectEntry.getValue());
                    }
                    blocksMap.remove(entry.getKey());
                    break;
                }
            }
        }
    }
}


