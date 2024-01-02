package pepse.world;

/**
 * RangeMutator interface for creating and removing objects in a given range
 */
public interface RangeMutator {
    /**
     * this method creates the object in the range (start, end)
     *
     * @param minX start index of creation
     * @param maxX   end index of creation
     */
    void createInRange(int minX, int maxX);

    /**
     * this method removes the object in the range (start, end)
     *
     * @param minX start index of removing
     * @param maxX   end index of removing
     */
    void removeInRange(int minX, int maxX);
}
