package pepse.world;

import danogl.util.Vector2;

/**
 * The InfinityLoop class is a class for creating a infinity world effect in a game
 */
public class InfinityLoop {

    /**
     * Vector2 of the avatar last position
     */
    private Vector2 avatarLastPosition;

    private final RangeMutator[] objects;
    private int rightSideMinX;
    private int leftSideMinX;

    /**
     * InfinityLoop Constructor.
     *
     * @param objects             objects that implements RangeMutator interface
     * @param initialRightXCoords an integer of the initial right side x-coordinate
     * @param initialLeftXCoords  an integer of the initial left side x-coordinate
     */
    public InfinityLoop(RangeMutator[] objects, int initialRightXCoords, int initialLeftXCoords) {
        this.objects = objects;
        this.rightSideMinX = initialRightXCoords;
        this.leftSideMinX = initialLeftXCoords;
    }

    /**
     * private method that updates the right side of the game world-
     * creates objects in the unseen right side of the world.
     * removes objects in the unseen left side of the world
     */
    private void updateRightSide(int distance) {
        for (RangeMutator object : objects) {
            object.createInRange(rightSideMinX, rightSideMinX + distance);
            object.removeInRange(leftSideMinX, leftSideMinX + distance);
        }
        rightSideMinX += distance;
        leftSideMinX += distance;
    }

    /**
     * private method that updates the left side of the game world-
     * creates objects in the unseen left side of the world.
     * removes objects in the unseen right side of the world
     */
    private void updateLeftSide(int distance) {
        for (RangeMutator object : objects) {
            object.createInRange(leftSideMinX - distance, leftSideMinX);
            object.removeInRange(rightSideMinX - distance, rightSideMinX);
        }
        rightSideMinX -= distance;
        leftSideMinX -= distance;
    }

    /**
     * this method updates the game visual world when the avatar went right or left
     * according to the minimum distunce that is required for update, by creating and removing
     * unseen objects from the screen.
     *
     * @param avatarPosition Vector2 of the avatar current position int he game
     */
    public void update(Vector2 avatarPosition) {
        if (avatarLastPosition != null) {
            int distance = (int) Math.abs(avatarPosition.x() - avatarLastPosition.x());
            if (avatarPosition.x() > avatarLastPosition.x()) {
                updateRightSide(distance);
            }
            if (avatarPosition.x() < avatarLastPosition.x()) {
                updateLeftSide(distance);
            }
        }
        avatarLastPosition = avatarPosition;
    }

}
