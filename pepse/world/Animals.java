package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.util.Vector2;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

/**
 * The Animals class. creates all the animals in the game
 */
public class Animals implements RangeMutator {

    /**
     * Constance representing the animal probability to be created
     */
    private static final double ANIMAL_PROBABILITY = 0.02;
    /**
     * Constance representing the animal tag
     */
    private static final String ANIMAL_TAG = "animal";

    /**
     * map contains all animals that were created in a specific x-coordinate
     */
    private final Map<Integer, GameObject> animalsMap;
    /**
     * random object
     */
    private final Random rand;

    private final Function<Float, Float> groundHeightAt;
    private final int animalLayer;
    private final ImageReader imageReader;
    private final int mySeed;
    private final GameObjectCollection gameObjects;

    /**
     * Animals constructor.
     *
     * @param gameObjects    the game objects collection
     * @param imageReader    ImageReader object
     * @param mySeed         an integer for the random object
     * @param groundHeightAt Terrain function that calculates the ground height
     * @param animalLayer    an integer representing the layer that animal object should be added to
     */
    public Animals(GameObjectCollection gameObjects, ImageReader imageReader, int mySeed,
                   Function<Float, Float> groundHeightAt, int animalLayer) {
        this.gameObjects = gameObjects;
        this.imageReader = imageReader;
        this.mySeed = mySeed;
        this.groundHeightAt = groundHeightAt;
        this.animalLayer = animalLayer;
        this.animalsMap = new HashMap<>();
        this.rand = new Random(mySeed);
    }

    /**
     * this method is responsible for creating animals in the range (miniX, maxX), according to the
     * probability that decide whether the animal will be created.
     *
     * @param minX minimum x-coordinate to create the animals
     * @param maxX maximum x-coordinate to create the animals
     */
    @Override
    public void createInRange(int minX, int maxX) {
        int newMinX = (int) Math.ceil((float) minX / Block.SIZE) * Block.SIZE;
        int newMaxX = (int) Math.ceil((float) maxX / Block.SIZE) * Block.SIZE;

        for (int x = newMinX; x < newMaxX; x += Block.SIZE) {
            if (rand.nextDouble() <= ANIMAL_PROBABILITY) {
                Vector2 foxPosition = new Vector2(x, groundHeightAt.apply((float) x));
                GameObject fox = new Fox(foxPosition, imageReader, mySeed + x);
                fox.physics().preventIntersectionsFromDirection(Vector2.ZERO);
                fox.setTag(ANIMAL_TAG);
                gameObjects.addGameObject(fox, animalLayer);
                animalsMap.put(x, fox);
            }
        }
    }

    /**
     * this method remove all animals in a given range (minX,maxX)
     *
     * @param minX minimum x-coordinate to remove the animals
     * @param maxX maximum x-coordinate to remove the animals
     */
    @Override
    public void removeInRange(int minX, int maxX) {
        int newMinX = (int) Math.ceil((float) minX / Block.SIZE) * Block.SIZE;
        int newMaxX = (int) Math.ceil((float) maxX / Block.SIZE) * Block.SIZE;

        for (int col = newMinX; col < newMaxX; col += Block.SIZE) {
            for (Map.Entry<Integer, GameObject> entry : animalsMap.entrySet()) {
                if (entry.getKey() == col) {
                    gameObjects.removeGameObject(entry.getValue(), animalLayer);
                }
                animalsMap.remove(entry.getKey());
                break;
            }

        }
    }
}
