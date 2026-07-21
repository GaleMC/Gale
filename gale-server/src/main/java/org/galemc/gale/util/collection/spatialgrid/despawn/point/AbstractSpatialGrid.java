// Generated with AI

package org.galemc.gale.util.collection.spatialgrid.despawn.point;

/**
 * Minimal interface describing the public API of the SpatialGrid implementation.
 *
 * Notes:
 * - Coordinates passed to add/move and query centers are doubles.
 * - For performance the caller must also pass precomputed floored integer coordinates
 *   (fx = (int)Math.floor(x), fy = (int)Math.floor(y), fz = (int)Math.floor(z))
 *   for the point or query center so the implementation can compute cell indices cheaply.
 * - add(...) returns a stable slot id (0..N-1). Slots are not compacted by the grid;
 *   remove(slot) marks the slot as removed. After remove(slot) the slot is considered absent.
 * - move(slot, ...) is a no-op if the slot is invalid or has been removed.
 */
public interface AbstractSpatialGrid {
    /**
     * Add a point to the grid.
     *
     * @param x  X coordinate (double)
     * @param y  Y coordinate (double)
     * @param z  Z coordinate (double)
     * @param fx floor(x) as int (precomputed)
     * @param fy floor(y) as int (precomputed)
     * @param fz floor(z) as int (precomputed)
     * @return stable slot id for the added point
     */
    int add(double x, double y, double z, int fx, int fy, int fz);

    /**
     * Remove the point stored at the given slot id.
     * If slot is invalid or already removed, this is a no-op.
     *
     * @param slot slot id returned by add
     */
    void remove(int slot);

    /**
     * Move an existing slot to new coordinates.
     * If the slot was removed or invalid, this is a no-op.
     *
     * @param slot slot id returned by add
     * @param newX new X coordinate
     * @param newY new Y coordinate
     * @param newZ new Z coordinate
     * @param fx   floor(newX) as int (precomputed)
     * @param fy   floor(newY) as int (precomputed)
     * @param fz   floor(newZ) as int (precomputed)
     */
    void move(int slot, double newX, double newY, double newZ, int fx, int fy, int fz);

    /**
     * Return a slot if any point lies inside the axis-aligned ellipsoid or upright cylinder centered at (cx,cy,cz).
     * For ellipsoid: radii are fixed for the grid (planar radius W, vertical half-height H).
     * For cylinder: with planar radius W and vertical half-height H.
     * The returned slot is not necessarily the nearest.
     * Returns -1 if no such slot exists.
     *
     * @param cx  center X
     * @param cy  center Y
     * @param cz  center Z
     * @param fcx floor(cx) as int (precomputed)
     * @param fcy floor(cy) as int (precomputed)
     * @param fcz floor(cz) as int (precomputed)
     */
    int anyIn(double cx, double cy, double cz, int fcx, int fcy, int fcz);

    /**
     * Return the slot id of the nearest point inside the axis-aligned ellipsoid or upright cylinder centered at (cx,cy,cz),
     * or -1 if none.
     *
     * @param cx  center X
     * @param cy  center Y
     * @param cz  center Z
     * @param fcx floor(cx) as int (precomputed)
     * @param fcy floor(cy) as int (precomputed)
     * @param fcz floor(cz) as int (precomputed)
     */
    int nearestIn(double cx, double cy, double cz, int fcx, int fcy, int fcz);

    /**
     * Number of allocated slots (next slot id).
     */
    int size();

    /**
     * Cell size used in X/Z (power of two).
     */
    int getCellSizeXZ();

    /**
     * Cell size used in Y (power of two).
     */
    int getCellSizeY();

    /**
     * Read coordinate of a slot. Caller must ensure slot is valid and not removed.
     */
    double getX(int slot);
    double getY(int slot);
    double getZ(int slot);

    boolean containsKey(int slot);

}
