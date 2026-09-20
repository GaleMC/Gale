package org.galemc.gale.util.collection.spatialgrid.despawn.point;

import org.bukkit.support.environment.Normal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Normal
class SpatialGridRegressionTest {

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void preservesMovedBoundsWhenBucketIsDisplaced(boolean cylinder) {
        SpatialGrid grid = createGrid(cylinder);
        int moving = grid.add(0, 64, 0, 0, 64, 0);
        for (int i = 0; i < 3; i++) {
            grid.add(0, 64, 0, 0, 64, 0);
        }
        int target = grid.add(1, 64, 1792, 1, 64, 1792);

        grid.move(target, 3, 64, 1792, 3, 64, 1792);
        grid.move(moving, 256, 64, 0, 256, 64, 0);

        assertEquals(target, grid.anyIn(130, 64, 1792, 130, 64, 1792));
        assertEquals(target, grid.nearestIn(130, 64, 1792, 130, 64, 1792));
        assertEquals(5, grid.size());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void doesNotRehashWhenAddingToExistingBucket(boolean cylinder) {
        SpatialGrid grid = createGrid(cylinder);
        for (int i = 0; i < 8; i++) {
            grid.add(i * 128, 64, 0, i * 128, 64, 0);
        }
        int[] buckets = grid.inlineCount;
        assertEquals(16, buckets.length);

        for (int i = 0; i < 100; i++) {
            grid.add(1, 64, 0, 1, 64, 0);
        }

        assertSame(buckets, grid.inlineCount);
        assertEquals(108, grid.size());
        assertNotEquals(-1, grid.anyIn(1, 64, 0, 1, 64, 0));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void rehashesWhenAddingNewBucketAtCapacity(boolean cylinder) {
        SpatialGrid grid = createGrid(cylinder);
        int[] slots = new int[9];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = grid.add(i * 256, 64, 0, i * 256, 64, 0);
        }

        assertEquals(32, grid.inlineCount.length);
        for (int i = 0; i < slots.length; i++) {
            assertEquals(slots[i], grid.nearestIn(i * 256, 64, 0, i * 256, 64, 0));
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void preservesSlotsAcrossGrowthAndReuse(boolean cylinder) {
        SpatialGrid grid = createGrid(cylinder);
        int[] slots = new int[192];
        for (int i = 0; i < slots.length; i++) {
            slots[i] = grid.add(i * 256, 64, 0, i * 256, 64, 0);
        }
        for (int i = 0; i < slots.length; i += 2) {
            grid.remove(slots[i]);
        }
        for (int i = 0; i < slots.length; i += 2) {
            slots[i] = grid.add(i * 256, 64, 0, i * 256, 64, 0);
        }

        assertEquals(slots.length, grid.size());
        for (int i = 0; i < slots.length; i++) {
            assertTrue(grid.containsKey(slots[i]));
            assertEquals(slots[i], grid.nearestIn(i * 256, 64, 0, i * 256, 64, 0));
        }
    }

    private static SpatialGrid createGrid(boolean cylinder) {
        return cylinder ? new CylinderSpatialGrid(128, 128, 8) : new EllipsoidSpatialGrid(128, 128, 8);
    }
}
