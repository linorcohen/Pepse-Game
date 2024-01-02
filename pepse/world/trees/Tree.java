package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.GameObjectPhysics;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.RangeMutator;

import java.util.*;
import java.util.function.Function;

/**
 * The Tree class is a class for creating all trees in the game.
 */
public class Tree extends GameObject implements RangeMutator {

    /**
     * Constance representing the stump tag
     */
    private static final String TRUNK_TAG = "trunk";
    /**
     * Constance representing the tree probability to be created
     */
    private static final double TREE_PROBABILITY = 0.03;
    /**
     * Constance representing the tree height
     */
    private static final int TREE_HEIGHT = 300;
    /**
     * Constance representing the number of leaves in a row
     */
    private static final int NUM_OF_LEAF_IN_ROW = 7;
    /**
     * Constance representing the number of leaves in a column
     */
    private static final int NUM_OF_LEAF_IN_COL = 6;
    /**
     * Constance representing the leaf probability to create
     */
    private static final double LEAF_PROBABILITY = 0.7;
    /**
     * Constance representing the number 2 in float
     */
    private static final float TWO_F = 2f;
    /**
     * Constance representing the trunk image path
     */
    private static final String ASSETS_TRUNK_PNG = "assets/tiles/trunk_side.png";
    /**
     * Constance representing the leaf image path
     */
    private static final String ASSETS_LEAF_PNG = "assets/tiles/cotton_green.png";

    /**
     * map contains all trees that were created in a specific x-coordinate
     */
    private final Map<Integer, ArrayList<GameObject>> allTreesMap;
    /**
     * random object
     */
    private final Random rand;

    private final Function<Float, Float> groundHeightAtX;
    private final GameObjectCollection gameObjects;
    private final int treeLayer;
    private final int leafLayer;
    private final Renderable trunkRender;
    private final Renderable leafRender;

    /**
     * Tree Constructor.
     * responsible for creating all trees in the game.
     *
     * @param gameObjects    the game objects collection
     * @param groundHeightAt Terrain function that calculates the ground height
     * @param TreeLayer      an integer representing the layer that tree object should be added to
     * @param leafLayer      an integer representing the layer that leaf object should be added to
     * @param mySeed         an integer for the random object
     * @param imageReader    ImageReader object
     */
    public Tree(GameObjectCollection gameObjects, Function<Float, Float> groundHeightAt, int TreeLayer,
                int leafLayer, int mySeed, ImageReader imageReader) {
        super(Vector2.ZERO, Vector2.ZERO, null);
        this.gameObjects = gameObjects;
        this.groundHeightAtX = groundHeightAt;
        this.treeLayer = TreeLayer;
        this.leafLayer = leafLayer;
        this.allTreesMap = new HashMap<>();
        this.rand = new Random(mySeed);
        this.trunkRender = imageReader.readImage(ASSETS_TRUNK_PNG, false);
        this.leafRender = imageReader.readImage(ASSETS_LEAF_PNG, false);
    }

    /**
     * this method is responsible for creating trees in the range (miniX, maxX), according to the
     * probability that decide whether the tree will be created.
     *
     * @param minX minimum x-coordinate to create the trees
     * @param maxX maximum x-coordinate to create the trees
     */
    public void createInRange(int minX, int maxX) {
        int newMinX = (int) Math.ceil((float) minX / Block.SIZE) * Block.SIZE;
        int newMaxX = (int) Math.ceil((float) maxX / Block.SIZE) * Block.SIZE;

        for (int col = newMinX; col < newMaxX; col += Block.SIZE) {
            ArrayList<GameObject> singleTreeMap = new ArrayList<>();
            if (rand.nextDouble() <= TREE_PROBABILITY) {
                plantTree(col, singleTreeMap);
                allTreesMap.put(col, singleTreeMap);
            }
        }
    }

    /**
     * private method that create a single tree in the given x-coordinate
     *
     * @param treeLocationX the x-coordinate location of the tree
     * @param singleTreeMap the tree objects map
     */
    private void plantTree(float treeLocationX, ArrayList<GameObject> singleTreeMap) {
        float y = groundHeightAtX.apply(treeLocationX);
        addTreeTrunkObjectToTree(treeLocationX, y - TREE_HEIGHT, singleTreeMap);
        addLeafToTree(treeLocationX, y - TREE_HEIGHT, singleTreeMap);
    }

    /**
     * private method that add all leaves to the planted tree
     *
     * @param treeLocationX the x-coordinate location of the tree
     * @param treeLocationY the y-coordinate location of the tree
     * @param singleTreeMap the tree objects map
     */
    private void addLeafToTree(float treeLocationX, float treeLocationY,
                               ArrayList<GameObject> singleTreeMap) {
        float numOfLeafOnTheRightOrLeft = (float) (Math.floor(NUM_OF_LEAF_IN_ROW / TWO_F) * Leaf.SIZE);
        float minX = treeLocationX - numOfLeafOnTheRightOrLeft;
        float maxX = treeLocationX + numOfLeafOnTheRightOrLeft + Leaf.SIZE;

        float numOfLeafOnTheTopOrBottom = (float) (Math.floor(NUM_OF_LEAF_IN_COL / TWO_F) * Leaf.SIZE);
        float minY = treeLocationY - numOfLeafOnTheTopOrBottom;
        float maxY = treeLocationY + numOfLeafOnTheTopOrBottom + Leaf.SIZE;

        for (float leafX = minX; leafX < maxX; leafX += Leaf.SIZE) {
            for (float leafY = minY; leafY < maxY; leafY += Leaf.SIZE) {
                if (rand.nextDouble() < LEAF_PROBABILITY) {
                    GameObject leaf = new Leaf(
                            new Vector2(leafX, leafY), new Vector2(Leaf.SIZE, Leaf.SIZE), leafRender);
                    gameObjects.addGameObject(leaf, leafLayer);
                    singleTreeMap.add(leaf);
                }
            }
        }
    }

    /**
     * this method remove all trees in a given range (minX,maxX)
     *
     * @param minX minimum x-coordinate to remove the trees
     * @param maxX maximum x-coordinate to remove the trees
     */
    public void removeInRange(int minX, int maxX) {
        float newMinX = (float) Math.ceil((float) minX / Block.SIZE) * Block.SIZE;
        float newMaxX = (float) Math.ceil((float) maxX / Block.SIZE) * Block.SIZE;

        for (float col = newMinX; col < newMaxX; col += Block.SIZE) {
            for (Map.Entry<Integer, ArrayList<GameObject>> entry : allTreesMap.entrySet()) {
                if (entry.getKey() == col) {
                    for (GameObject treeObject : entry.getValue()) {
                        if (treeObject instanceof Leaf) {
                            gameObjects.removeGameObject(treeObject, leafLayer);
                        } else {
                            gameObjects.removeGameObject(treeObject, treeLayer);
                        }
                    }
                    allTreesMap.remove(entry.getKey());
                    break;
                }
            }
        }
    }

    /**
     * private method that adds the tree tree trunk to the game
     *
     * @param treeLocationX the x-coordinate location of the tree
     * @param treeLocationY the y-coordinate location of the tree
     * @param singleTreeMap the tree objects map
     */
    private void addTreeTrunkObjectToTree(float treeLocationX, float treeLocationY,
                                          ArrayList<GameObject> singleTreeMap) {
        GameObject trunk = new GameObject(new Vector2(treeLocationX, treeLocationY),
                new Vector2(Block.SIZE, TREE_HEIGHT),
                trunkRender);
        trunk.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        trunk.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        trunk.setTag(TRUNK_TAG);
        gameObjects.addGameObject(trunk, treeLayer);
        singleTreeMap.add(trunk);
    }
}
