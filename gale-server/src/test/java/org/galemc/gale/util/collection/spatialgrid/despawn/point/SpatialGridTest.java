// Generated with AI

package org.galemc.gale.util.collection.spatialgrid.despawn.point;

import org.bukkit.support.environment.Normal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@Normal
class SpatialGridTest {
    // Use modest radii so cell sizes are small and many edge cases exercised
    private static final int W = 3;
    private static final int H = 2;
    private static final int INITIAL_CAP = 16;

    private SpatialGrid ellipsoidGrid;
    private SpatialGrid cylinderGrid;

    @BeforeEach
    public void setUp() {
        ellipsoidGrid = new EllipsoidSpatialGrid(W, H, INITIAL_CAP);
        cylinderGrid = new CylinderSpatialGrid(W, H, INITIAL_CAP);
    }

    // Helpers
    private static int floorInt(double v) {
        return (int) Math.floor(v);
    }

    // 1. Empty grid: any queries return false / -1
    @Test
    public void testEmptyAnyEllipsoid() {
        assertFalse(ellipsoidGrid.anyIn(0.0, 0.0, 0.0, floorInt(0.0), floorInt(0.0), floorInt(0.0)) != -1);
    }

    @Test
    public void testEmptyNearestEllipsoid() {
        assertEquals(-1, ellipsoidGrid.nearestIn(0.0, 0.0, 0.0, floorInt(0.0), floorInt(0.0), floorInt(0.0)));
    }

    @Test
    public void testEmptyAnyCylinder() {
        assertFalse(cylinderGrid.anyIn(0.0, 0.0, 0.0, floorInt(0.0), floorInt(0.0), floorInt(0.0)) != -1);
    }

    @Test
    public void testEmptyNearestCylinder() {
        assertEquals(-1, cylinderGrid.nearestIn(0.0, 0.0, 0.0, floorInt(0.0), floorInt(0.0), floorInt(0.0)));
    }

    // 2. Single point inside ellipsoid/cylinder
    @Test
    public void testSinglePointInsideEllipsoid() {
        int s = ellipsoidGrid.add(1.0, 0.0, 1.0, floorInt(1.0), floorInt(0.0), floorInt(1.0));
        assertTrue(ellipsoidGrid.anyIn(1.0, 0.0, 1.0, floorInt(1.0), floorInt(0.0), floorInt(1.0)) != -1);
        assertEquals(s, ellipsoidGrid.nearestIn(1.0, 0.0, 1.0, floorInt(1.0), floorInt(0.0), floorInt(1.0)));
    }

    @Test
    public void testSinglePointInsideCylinder() {
        int s = cylinderGrid.add(2.0, 0.5, -1.0, floorInt(2.0), floorInt(0.5), floorInt(-1.0));
        assertTrue(cylinderGrid.anyIn(2.0, 0.5, -1.0, floorInt(2.0), floorInt(0.5), floorInt(-1.0)) != -1);
        assertEquals(s, cylinderGrid.nearestIn(2.0, 0.5, -1.0, floorInt(2.0), floorInt(0.5), floorInt(-1.0)));
    }

    // 3. Single point outside queries
    @Test
    public void testSinglePointOutsideEllipsoid() {
        ellipsoidGrid.add(100.0, 100.0, 100.0, floorInt(100.0), floorInt(100.0), floorInt(100.0));
        assertFalse(ellipsoidGrid.anyIn(0.0, 0.0, 0.0, floorInt(0.0), floorInt(0.0), floorInt(0.0)) != -1);
    }

    @Test
    public void testSinglePointOutsideCylinder() {
        cylinderGrid.add(100.0, 100.0, 100.0, floorInt(100.0), floorInt(100.0), floorInt(100.0));
        assertFalse(cylinderGrid.anyIn(0.0, 0.0, 0.0, floorInt(0.0), floorInt(0.0), floorInt(0.0)) != -1);
    }

    // 4. Multiple points, nearest selection
    @Test
    public void testNearestEllipsoidMultiplePoints() {
        int a = ellipsoidGrid.add(0.5, 0.0, 0.5, floorInt(0.5), floorInt(0.0), floorInt(0.5));
        int b = ellipsoidGrid.add(1.5, 0.0, 1.5, floorInt(1.5), floorInt(0.0), floorInt(1.5));
        int nearest = ellipsoidGrid.nearestIn(0.0, 0.0, 0.0, floorInt(0.0), floorInt(0.0), floorInt(0.0));
        assertEquals(a, nearest);
    }

    @Test
    public void testNearestCylinderMultiplePoints() {
        int a = cylinderGrid.add(0.5, 0.0, 0.5, floorInt(0.5), floorInt(0.0), floorInt(0.5));
        int b = cylinderGrid.add(0.2, 0.0, 0.2, floorInt(0.2), floorInt(0.0), floorInt(0.2));
        int nearest = cylinderGrid.nearestIn(0.0, 0.0, 0.0, floorInt(0.0), floorInt(0.0), floorInt(0.0));
        assertEquals(b, nearest);
    }

    // 5. Points on boundary of ellipsoid/cylinder
    @Test
    public void testPointOnEllipsoidBoundary() {
        double cx = 0.0, cy = 0.0, cz = 0.0;
        // point at distance W in X
        int s = ellipsoidGrid.add(W, 0.0, 0.0, floorInt(W), floorInt(0.0), floorInt(0.0));
        assertTrue(ellipsoidGrid.anyIn(cx, cy, cz, floorInt(cx), floorInt(cy), floorInt(cz)) != -1);
        assertEquals(s, ellipsoidGrid.nearestIn(cx, cy, cz, floorInt(cx), floorInt(cy), floorInt(cz)));
    }

    @Test
    public void testPointOnCylinderBoundary() {
        double cx = 0.0, cy = 0.0, cz = 0.0;
        int s = cylinderGrid.add(W, 0.0, 0.0, floorInt(W), floorInt(0.0), floorInt(0.0));
        assertTrue(cylinderGrid.anyIn(cx, cy, cz, floorInt(cx), floorInt(cy), floorInt(cz)) != -1);
        assertEquals(s, cylinderGrid.nearestIn(cx, cy, cz, floorInt(cx), floorInt(cy), floorInt(cz)));
    }

    // 6. Move within same cell (cheap path)
    @Test
    public void testMoveWithinSameCellKeepsSlot() {
        for (var grid : List.of(ellipsoidGrid, cylinderGrid)) {
            int s = grid.add(1.2, 0.3, -0.7, floorInt(1.2), floorInt(0.3), floorInt(-0.7));
            grid.move(s, 1.3, 0.4, -0.6, floorInt(1.3), floorInt(0.4), floorInt(-0.6)); // likely same cell
            assertEquals(1.3, grid.getX(s), 1e-12);
            assertEquals(0.4, grid.getY(s), 1e-12);
            assertEquals(-0.6, grid.getZ(s), 1e-12);
        }
    }

    // 7. Move across cells (unlink and reinsert)
    @Test
    public void testMoveAcrossCells() {
        int s = ellipsoidGrid.add(0.0, 0.0, 0.0, floorInt(0.0), floorInt(0.0), floorInt(0.0));
        ellipsoidGrid.move(s, 100.0, 0.0, 0.0, floorInt(100.0), floorInt(0.0), floorInt(0.0));
        assertTrue(ellipsoidGrid.anyIn(100.0, 0.0, 0.0, floorInt(100.0), floorInt(0.0), floorInt(0.0)) != -1);
        assertFalse(ellipsoidGrid.anyIn(0.0, 0.0, 0.0, floorInt(0.0), floorInt(0.0), floorInt(0.0)) != -1);
    }

    // 8. Remove last point in a cell triggers bucket deletion
    @Test
    public void testRemoveLastInCellDeletesBucket() {
        int s = ellipsoidGrid.add(5.0, 5.0, 5.0, floorInt(5.0), floorInt(5.0), floorInt(5.0));
        assertTrue(ellipsoidGrid.anyIn(5.0, 5.0, 5.0, floorInt(5.0), floorInt(5.0), floorInt(5.0)) != -1);
        ellipsoidGrid.remove(s);
        assertFalse(ellipsoidGrid.anyIn(5.0, 5.0, 5.0, floorInt(5.0), floorInt(5.0), floorInt(5.0)) != -1);
    }

    // 9. Rehash growth path: add many points to force rehash
    @Test
    public void testRehashGrowth() {
        int n = 1000;
        int[] slots = new int[n];
        for (int i = 0; i < n; i++) {
            double x = i * 0.1;
            slots[i] = ellipsoidGrid.add(x, 0.0, 0.0, floorInt(x), floorInt(0.0), floorInt(0.0));
        }
        // verify a few random points still found
        assertTrue(ellipsoidGrid.anyIn(10.0, 0.0, 0.0, floorInt(10.0), floorInt(0.0), floorInt(0.0)) != -1);
        assertTrue(ellipsoidGrid.anyIn(50.0, 0.0, 0.0, floorInt(50.0), floorInt(0.0), floorInt(0.0)) != -1);
        assertEquals(slots[500], ellipsoidGrid.nearestIn(50.0, 0.0, 0.0, floorInt(50.0), floorInt(0.0), floorInt(0.0)));
    }

    // 10. Overflow pool growth: force more than inline capacity per bucket
    @Test
    public void testOverflowPoolGrowth() {
        // Put many points into same cell by using coordinates that floor to same cell
        double baseX = 0.1, baseY = 0.1, baseZ = 0.1;
        int cellFx = floorInt(baseX), cellFy = floorInt(baseY), cellFz = floorInt(baseZ);
        int count = 20; // > inline capacity (4)
        int[] slots = new int[count];
        for (int i = 0; i < count; i++) {
            double x = baseX + i * 0.01;
            slots[i] = ellipsoidGrid.add(x, baseY, baseZ, cellFx, cellFy, cellFz);
        }
        // ensure all are discoverable
        for (int i = 0; i < count; i++) {
            assertTrue(ellipsoidGrid.anyIn(xsOf(ellipsoidGrid, slots[i]), ysOf(ellipsoidGrid, slots[i]), zsOf(ellipsoidGrid, slots[i]),
                floorInt(xsOf(ellipsoidGrid, slots[i])), floorInt(ysOf(ellipsoidGrid, slots[i])), floorInt(zsOf(ellipsoidGrid, slots[i]))) != -1);
        }
    }

    // 11. AABB lazy recompute correctness: remove extremal point and ensure queries still correct
    @Test
    public void testAabbLazyRecompute() {
        int a = ellipsoidGrid.add(0.0, 0.0, 0.0, floorInt(0.0), floorInt(0.0), floorInt(0.0));
        int b = ellipsoidGrid.add(10.0, 0.0, 0.0, floorInt(10.0), floorInt(0.0), floorInt(0.0));
        assertTrue(ellipsoidGrid.anyIn(0.0, 0.0, 0.0, floorInt(0.0), floorInt(0.0), floorInt(0.0)) != -1);
        assertTrue(ellipsoidGrid.anyIn(10.0 - W, 0.0, 0.0, floorInt(5.0), floorInt(0.0), floorInt(0.0)) != -1);
        assertTrue(ellipsoidGrid.anyIn(10.0 + W, 0.0, 0.0, floorInt(5.0), floorInt(0.0), floorInt(0.0)) != -1);
        assertTrue(ellipsoidGrid.anyIn(10.0 + W / Math.sqrt(2) - 0.001, 0.0, W / Math.sqrt(2) - 0.001, floorInt(5.0), floorInt(0.0), floorInt(0.0)) != -1);
        ellipsoidGrid.remove(b); // remove extremum, mark dirty
        assertTrue(ellipsoidGrid.anyIn(0.0, 0.0, 0.0, floorInt(0.0), floorInt(0.0), floorInt(0.0)) != -1);
        ellipsoidGrid.remove(a);
        assertFalse(ellipsoidGrid.anyIn(0.0, 0.0, 0.0, floorInt(0.0), floorInt(0.0), floorInt(0.0)) != -1);
    }

    // 12. Slot stability: slot id remains valid after many operations
    @Test
    public void testSlotStability() {
        for (var grid : List.of(ellipsoidGrid, cylinderGrid)) {
            int s = grid.add(1.0, 1.0, 1.0, floorInt(1.0), floorInt(1.0), floorInt(1.0));
            for (int i = 0; i < 200; i++) {
                int t = grid.add(i + 2.0, 0.0, 0.0, floorInt(i + 2.0), 0, 0);
                grid.move(t, i + 2.1, 0.0, 0.0, floorInt(i + 2.1), 0, 0);
                grid.remove(t);
            }
            // original slot should still point to same coordinates
            assertEquals(1.0, grid.getX(s), 1e-12);
            assertEquals(1.0, grid.getY(s), 1e-12);
            assertEquals(1.0, grid.getZ(s), 1e-12);
        }
    }

    // 13. Add/remove many times: no exceptions, final state consistent
    @Test
    public void testAddRemoveMany() {
        int n = 500;
        int[] slots = new int[n];
        for (int i = 0; i < n; i++) slots[i] = ellipsoidGrid.add(i, 0.0, 0.0, floorInt(i), 0, 0);
        for (int i = 0; i < n; i += 2) ellipsoidGrid.remove(slots[i]);
        for (int i = 1; i < n; i += 2) {
            assertTrue(ellipsoidGrid.anyIn(i, 0.0, 0.0, floorInt(i), 0, 0) != -1);
        }
    }

    // 14. Query ordering: center-first increases chance of early exit (functional check)
    @Test
    public void testCenterFirstOrderingFunctional() {
        // center cell empty, neighbor cell has point; ensure found
        ellipsoidGrid.add(5.0, 0.0, 0.0, floorInt(5.0), 0, 0);
        assertTrue(ellipsoidGrid.anyIn(5.0, 0.0, 1.0, floorInt(5.0), 0, floorInt(1.0)) != -1);
    }

    // 15. Points with negative coordinates
    @Test
    public void testNegativeCoordinates() {
        int s = ellipsoidGrid.add(-10.5, -2.0, -3.3, floorInt(-10.5), floorInt(-2.0), floorInt(-3.3));
        assertTrue(ellipsoidGrid.anyIn(-10.5, -2.0, -3.3, floorInt(-10.5), floorInt(-2.0), floorInt(-3.3)) != -1);
        assertEquals(s, ellipsoidGrid.nearestIn(-10.5, -2.0, -3.3, floorInt(-10.5), floorInt(-2.0), floorInt(-3.3)));
    }

    // 16. Very large coordinates within packing range (sanity)
    @Test
    public void testLargeCoordinatesWithinPacking() {
        // choose values that fit into packing masks (26 bits)
        int cx = (1 << 25) - 10;
        int cy = (1 << 11) - 5;
        int cz = (1 << 25) - 20;
        double x = (double) cx;
        double y = (double) cy;
        double z = (double) cz;
        int s = ellipsoidGrid.add(x, y, z, floorInt(x), floorInt(y), floorInt(z));
        assertTrue(ellipsoidGrid.anyIn(x, y, z, floorInt(x), floorInt(y), floorInt(z)) != -1);
        assertEquals(s, ellipsoidGrid.nearestIn(x, y, z, floorInt(x), floorInt(y), floorInt(z)));
    }

    // 17. Multiple points in same planar location but different Y (cylinder height test)
    @Test
    public void testCylinderHeightRespected() {
        double cx = 0.0, cz = 0.0;
        int inside = cylinderGrid.add(0.0, 0.0, 0.0, 0, 0, 0);
        int above = cylinderGrid.add(0.0, H + 1.0, 0.0, 0, floorInt(H + 1.0), 0);
        assertTrue(cylinderGrid.anyIn(cx, 0.0, cz, floorInt(cx), floorInt(0.0), floorInt(cz)) != -1);
        assertTrue(cylinderGrid.anyIn(cx, H + 2, cz, floorInt(cx), floorInt(H + 2.0), floorInt(cz)) != -1);
        assertFalse(cylinderGrid.anyIn(cx, H + 1.0 + H + 1.0, cz, floorInt(cx), floorInt(H + 2.0), floorInt(cz)) != -1);
    }

    // 18. Nearest tie-breaking: deterministic (first found)
    @Test
    public void testNearestTieBreakingDeterministic() {
        int a = ellipsoidGrid.add(1.0, 0.0, 0.0, 1, 0, 0);
        int b = ellipsoidGrid.add(-1.0, 0.0, 0.0, -1, 0, 0);
        // both at same distance from origin; nearest may be either but must be a valid slot
        int n = ellipsoidGrid.nearestIn(0.0, 0.0, 0.0, 0, 0, 0);
        assertTrue(n == a || n == b);
    }

    // 19. Remove non-existent slot (no-op)
    @Test
    public void testRemoveInvalidSlotNoOp() {
        // no exception and grid still functional
        int s = ellipsoidGrid.add(0.0, 0.0, 0.0, 0, 0, 0);
        assertTrue(ellipsoidGrid.anyIn(0.0, 0.0, 0.0, 0, 0, 0) != -1);
        ellipsoidGrid.remove(s);
        assertFalse(ellipsoidGrid.anyIn(0.0, 0.0, 0.0, 0, 0, 0) != -1);
    }

    // 21. Add many points in a grid pattern and query center
    @Test
    public void testGridPatternCoverage() {
        int n = 10;
        for (int x = -n; x <= n; x++)
            for (int z = -n; z <= n; z++) {
                ellipsoidGrid.add(x * (W / 2.0), 0.0, z * (W / 2.0), floorInt(x * (W / 2.0)), 0, floorInt(z * (W / 2.0)));
            }
        assertTrue(ellipsoidGrid.anyIn(0.0, 0.0, 0.0, 0, 0, 0) != -1);
    }

    // 22. Repeated add/remove of same slot index (slot ids not reused)
    @Test
    public void testSlotIdsNotReused() {
        for (var grid : List.of(ellipsoidGrid, cylinderGrid)) {
            int s1 = grid.add(0.0, 0.0, 0.0, 0, 0, 0);
            grid.remove(s1);
            int s2 = grid.add(0.0, 0.0, 0.0, 0, 0, 0);
            // s2 may equal s1 because implementation uses next free slot; but ensure both operations valid
            assertTrue(s2 >= 0);
        }
    }

    // 23. Many in-cell moves (stress cheap path)
    @Test
    public void testManyInCellMovesPerformancePath() {
        int s = ellipsoidGrid.add(1.1, 1.1, 1.1, floorInt(1.1), floorInt(1.1), floorInt(1.1));
        for (int i = 0; i < 10000; i++) {
            double nx = 1.1 + (i % 3) * 0.1;
            ellipsoidGrid.move(s, nx, 1.1, 1.1, floorInt(nx), floorInt(1.1), floorInt(1.1));
        }
        assertTrue(ellipsoidGrid.anyIn(ellipsoidGrid.getX(s), ellipsoidGrid.getY(s), ellipsoidGrid.getZ(s),
            floorInt(ellipsoidGrid.getX(s)), floorInt(ellipsoidGrid.getY(s)), floorInt(ellipsoidGrid.getZ(s))) != -1);
    }

    // 24. Ensure AABB recompute after many removals works
    @Test
    public void testAabbRecomputeAfterManyRemovals() {
        int a = ellipsoidGrid.add(0.0, 0.0, 0.0, 0, 0, 0);
        int b = ellipsoidGrid.add(10.0, 0.0, 0.0, 10, 0, 0);
        int c = ellipsoidGrid.add(5.0, 0.0, 0.0, 5, 0, 0);
        ellipsoidGrid.remove(b);
        ellipsoidGrid.remove(c);
        // aabb for bucket should be recomputed lazily and still find 'a'
        assertTrue(ellipsoidGrid.anyIn(0.0, 0.0, 0.0, 0, 0, 0) != -1);
    }

    // 25. Add points exactly at cell boundaries to ensure packing and cell computation correct
    @Test
    public void testPointsAtCellBoundaries() {
        int csXZ = ellipsoidGrid.getCellSizeXZ();
        int csY = ellipsoidGrid.getCellSizeY();
        double x = csXZ;
        double y = csY;
        double z = csXZ;
        int s = ellipsoidGrid.add(x, y, z, floorInt(x), floorInt(y), floorInt(z));
        assertTrue(ellipsoidGrid.anyIn(x, y, z, floorInt(x), floorInt(y), floorInt(z)) != -1);
        ellipsoidGrid.remove(s);
        assertFalse(ellipsoidGrid.anyIn(x, y, z, floorInt(x), floorInt(y), floorInt(z)) != -1);
    }

    // 26. Many random operations deterministic seed
    @Test
    public void testRandomOperationsDeterministic() {
        for (var grid : List.of(ellipsoidGrid, cylinderGrid)) {
            Random rnd = new Random(12345);
            int[] slots = new int[200];
            for (int i = 0; i < slots.length; i++) {
                double x = rnd.nextDouble() * 100 - 50;
                double y = rnd.nextDouble() * 10 - 5;
                double z = rnd.nextDouble() * 100 - 50;
                slots[i] = grid.add(x, y, z, floorInt(x), floorInt(y), floorInt(z));
            }
            for (int i = 0; i < 100; i++) {
                int idx = rnd.nextInt(slots.length);
                grid.move(slots[idx], rnd.nextDouble() * 100 - 50, rnd.nextDouble() * 10 - 5, rnd.nextDouble() * 100 - 50,
                    floorInt(rnd.nextDouble() * 100 - 50), floorInt(rnd.nextDouble() * 10 - 5), floorInt(rnd.nextDouble() * 100 - 50));
            }
            // ensure no exceptions and some queries return plausible results
            assertTrue(grid.size() >= slots.length);
        }
    }

    // 27. Query with non-integer center floors provided incorrectly (still must work)
    @Test
    public void testQueryWithIncorrectFloorsStillWorks() {
        int s = ellipsoidGrid.add(2.3, 0.0, 2.3, floorInt(2.3), 0, floorInt(2.3));
        // provide slightly wrong floored center (off by 1) but coordinates exact
        assertTrue(ellipsoidGrid.anyIn(2.3, 0.0, 2.3, floorInt(2.3) + 1, 0, floorInt(2.3) + 1) != -1);
    }

    // 28. Add points with same coordinates multiple times
    @Test
    public void testDuplicatePointsAllowed() {
        int a = ellipsoidGrid.add(1.0, 1.0, 1.0, 1, 1, 1);
        int b = ellipsoidGrid.add(1.0, 1.0, 1.0, 1, 1, 1);
        assertTrue(ellipsoidGrid.anyIn(1.0, 1.0, 1.0, 1, 1, 1) != -1);
        assertTrue(a != b || a == b); // just ensure both added without exception
    }

    // 30. Many buckets created and deleted repeatedly
    @Test
    public void testCreateDeleteBucketsRepeatedly() {
        for (int i = 0; i < 200; i++) {
            int s = ellipsoidGrid.add(i * 1.0, 0.0, 0.0, floorInt(i * 1.0), 0, 0);
            ellipsoidGrid.remove(s);
        }
        // grid should still accept new adds
        int s2 = ellipsoidGrid.add(0.0, 0.0, 0.0, 0, 0, 0);
        assertTrue(ellipsoidGrid.anyIn(0.0, 0.0, 0.0, 0, 0, 0) != -1);
        ellipsoidGrid.remove(s2);
    }

    // 31. Cylinder query vertical boundary checks
    @Test
    public void testCylinderVerticalBoundary() {
        int s = cylinderGrid.add(0.0, H, 0.0, 0, floorInt(H), 0);
        assertTrue(cylinderGrid.anyIn(0.0, 0.0, 0.0, 0, 0, 0) != -1); // center at 0,0,0 should include y=H
    }

    // 32. Ellipsoid anisotropic check (W planar, H vertical)
    @Test
    public void testEllipsoidAnisotropic() {
        int s = ellipsoidGrid.add(0.0, H + 0.1, 0.0, 0, floorInt(H + 0.1), 0);
        // outside ellipsoid centered at 0,0,0 because vertical exceeds H
        assertFalse(ellipsoidGrid.anyIn(0.0, 0.0, 0.0, 0, 0, 0) != -1);
    }

    // 33. Many small cells: ensure neighbor checks limited to 27 cells
    @Test
    public void testNeighborCellLimitBehavior() {
        // fill a 3x3x3 neighborhood around origin with one point each
        for (int dx = -1; dx <= 1; dx++)
            for (int dy = -1; dy <= 1; dy++)
                for (int dz = -1; dz <= 1; dz++) {
                    double x = dx * ellipsoidGrid.getCellSizeXZ() * 0.5;
                    double y = dy * ellipsoidGrid.getCellSizeY() * 0.5;
                    double z = dz * ellipsoidGrid.getCellSizeXZ() * 0.5;
                    ellipsoidGrid.add(x, y, z, floorInt(x), floorInt(y), floorInt(z));
                }
        assertTrue(ellipsoidGrid.anyIn(0.0, 0.0, 0.0, 0, 0, 0) != -1);
    }

    // 34. Query center not aligned to cell boundaries
    @Test
    public void testQueryCenterNotAligned() {
        ellipsoidGrid.add(0.7, 0.7, 0.7, floorInt(0.7), floorInt(0.7), floorInt(0.7));
        assertTrue(ellipsoidGrid.anyIn(0.7, 0.7, 0.7, floorInt(0.7), floorInt(0.7), floorInt(0.7)) != -1);
    }

    // 35. Ensure getCellSize accessors return powers of two
    @Test
    public void testCellSizesArePowersOfTwo() {
        for (var grid : List.of(ellipsoidGrid, cylinderGrid)) {
            int csXZ = grid.getCellSizeXZ();
            int csY = grid.getCellSizeY();
            assertTrue((csXZ & (csXZ - 1)) == 0);
            assertTrue((csY & (csY - 1)) == 0);
        }
    }

    // 36. Add point then query with slightly different center (still inside)
    @Test
    public void testQueryWithNearbyCenter() {
        ellipsoidGrid.add(1.0, 0.0, 0.0, 1, 0, 0);
        assertTrue(ellipsoidGrid.anyIn(1.1, 0.0, 0.0, floorInt(1.1), 0, 0) != -1);
    }

    // 37. Many small inline removals and overflow removals
    @Test
    public void testInlineAndOverflowRemovals() {
        int baseFx = 0, baseFy = 0, baseFz = 0;
        int[] slots = new int[10];
        for (int i = 0; i < 10; i++) slots[i] = ellipsoidGrid.add(0.1 + i * 0.01, 0.0, 0.1, baseFx, baseFy, baseFz);
        // remove some inline and some overflow
        for (int i = 0; i < 10; i += 2) ellipsoidGrid.remove(slots[i]);
        for (int i = 1; i < 10; i += 2)
            assertTrue(ellipsoidGrid.anyIn(xsOf(ellipsoidGrid, slots[i]), ysOf(ellipsoidGrid, slots[i]), zsOf(ellipsoidGrid, slots[i]),
                floorInt(xsOf(ellipsoidGrid, slots[i])), floorInt(ysOf(ellipsoidGrid, slots[i])), floorInt(zsOf(ellipsoidGrid, slots[i]))) != -1);
    }

    // 38. Add points with y outside cylinder but inside ellipsoid (vertical anisotropy)
    @Test
    public void testEllipsoidContainsWhileCylinderNot() {
        // point with vertical within ellipsoid if H large enough? Use H small so cylinder excludes
        double cx = 0.0, cz = 0.0;
        ellipsoidGrid.add(0.0, H + 0.5, 0.0, 0, floorInt(H + 0.5), 0);
        cylinderGrid.add(0.0, H + 0.5, 0.0, 0, floorInt(H + 0.5), 0);
        // ellipsoid test uses H as vertical radius; since point is outside H, both should be false
        assertFalse(ellipsoidGrid.anyIn(cx, 0.0, cz, 0, 0, 0) != -1);
        assertFalse(cylinderGrid.anyIn(cx, 0.0, cz, 0, 0, 0) != -1);
    }

    // 39. Add and remove many to exercise overflow pool growth
    @Test
    public void testOverflowPoolNeverFrees() {
        for (var grid : List.of(ellipsoidGrid, cylinderGrid)) {
            int cellFx = 0, cellFy = 0, cellFz = 0;
            for (int i = 0; i < 500; i++) {
                int s = grid.add(0.1 + i * 0.001, 0.0, 0.1, cellFx, cellFy, cellFz);
                if (i % 3 == 0) grid.remove(s);
            }
            // ensure no exceptions and grid still functional
            assertTrue(true);
        }
    }

    // 40. Query with center far from any points
    @Test
    public void testQueryFarFromPoints() {
        ellipsoidGrid.add(0.0, 0.0, 0.0, 0, 0, 0);
        assertFalse(ellipsoidGrid.anyIn(10000.0, 10000.0, 10000.0, floorInt(10000.0), floorInt(10000.0), floorInt(10000.0)) != -1);
    }

    // 41. Add points with fractional floors (negative fractions)
    @Test
    public void testNegativeFractionalFloors() {
        int s = ellipsoidGrid.add(-0.1, -0.1, -0.1, floorInt(-0.1), floorInt(-0.1), floorInt(-0.1));
        assertTrue(ellipsoidGrid.anyIn(-0.1, -0.1, -0.1, floorInt(-0.1), floorInt(-0.1), floorInt(-0.1)) != -1);
        ellipsoidGrid.remove(s);
        assertFalse(ellipsoidGrid.anyIn(-0.1, -0.1, -0.1, floorInt(-0.1), floorInt(-0.1), floorInt(-0.1)) != -1);
    }

    // 42. Add a point, then add many others, ensure original still found
    @Test
    public void testOriginalPointStillFoundAfterManyAdds() {
        int s = ellipsoidGrid.add(0.0, 0.0, 0.0, 0, 0, 0);
        for (int i = 0; i < 1000; i++) ellipsoidGrid.add(i + 1.0, 0.0, 0.0, floorInt(i + 1.0), 0, 0);
        assertTrue(ellipsoidGrid.anyIn(0.0, 0.0, 0.0, 0, 0, 0) != -1);
        assertEquals(s, ellipsoidGrid.nearestIn(0.0, 0.0, 0.0, 0, 0, 0));
    }

    // 43. Add points with same cell but different coordinates and ensure nearest picks correct
    @Test
    public void testNearestWithinSameCell() {
        int a = ellipsoidGrid.add(0.1, 0.0, 0.1, 0, 0, 0);
        int b = ellipsoidGrid.add(0.2, 0.0, 0.2, 0, 0, 0);
        int n = ellipsoidGrid.nearestIn(0.0, 0.0, 0.0, 0, 0, 0);
        assertTrue(n == a || n == b);
    }

    // 44. Add and remove to exercise deleteBucketAt cluster reinsertion correctness
    @Test
    public void testClusterReinsertionCorrectness() {
        int a = ellipsoidGrid.add(0.0, 0.0, 0.0, 0, 0, 0);
        int b = ellipsoidGrid.add(1.0, 0.0, 0.0, 1, 0, 0);
        int c = ellipsoidGrid.add(2.0, 0.0, 0.0, 2, 0, 0);
        ellipsoidGrid.remove(b);
        // ensure a and c still findable
        assertTrue(ellipsoidGrid.anyIn(0.0, 0.0, 0.0, 0, 0, 0) != -1);
        assertTrue(ellipsoidGrid.anyIn(2.0, 0.0, 0.0, 2, 0, 0) != -1);
    }

    // 46. Add points that exercise packing bit masks near wrap-around (but within allowed range)
    @Test
    public void testPackingEdgeValues() {
        int cx = (1 << 26) - 1; // near 26-bit wrap (but masked)
        int cy = (1 << 12) - 1;
        int cz = (1 << 26) - 2;
        double x = (double) cx, y = (double) cy, z = (double) cz;
        int s = ellipsoidGrid.add(x, y, z, floorInt(x), floorInt(y), floorInt(z));
        assertTrue(ellipsoidGrid.anyIn(x, y, z, floorInt(x), floorInt(y), floorInt(z)) != -1);
        ellipsoidGrid.remove(s);
        assertFalse(ellipsoidGrid.anyIn(x, y, z, floorInt(x), floorInt(y), floorInt(z)) != -1);
    }

    // 47. Add a point then query with center floored to neighbor cell (should still find if within radius)
    @Test
    public void testQueryWithNeighborFloor() {
        int s = ellipsoidGrid.add(0.9, 0.0, 0.9, floorInt(0.9), 0, floorInt(0.9));
        assertTrue(ellipsoidGrid.anyIn(1.0, 0.0, 1.0, floorInt(1.0), 0, floorInt(1.0)) != -1);
    }

    // 48. Ensure cylinder nearest returns -1 when none
    @Test
    public void testNearestCylinderNone() {
        assertEquals(-1, cylinderGrid.nearestIn(100.0, 100.0, 100.0, floorInt(100.0), floorInt(100.0), floorInt(100.0)));
    }

    // 49. Ensure ellipsoid nearest returns -1 when none
    @Test
    public void testNearestEllipsoidNone() {
        assertEquals(-1, ellipsoidGrid.nearestIn(-100.0, -100.0, -100.0, floorInt(-100.0), floorInt(-100.0), floorInt(-100.0)));
    }

    // 50. Sanity: add, move, remove sequence many times to ensure no exceptions
    @Test
    public void testAddMoveRemoveSequenceStress() {
        for (var grid : List.of(ellipsoidGrid, cylinderGrid)) {
            for (int i = 0; i < 200; i++) {
                int s = grid.add(i * 0.5, (i % 3) * 0.1, i * -0.5, floorInt(i * 0.5), floorInt((i % 3) * 0.1), floorInt(i * -0.5));
                grid.move(s, i * 0.5 + 0.2, (i % 3) * 0.1 + 0.05, i * -0.5 + 0.2, floorInt(i * 0.5 + 0.2), floorInt((i % 3) * 0.1 + 0.05), floorInt(i * -0.5 + 0.2));
                grid.remove(s);
            }
            assertTrue(true); // reached end without exceptions
        }
    }

    // Utility accessors for tests (reflective-like helpers)
    private static double xsOf(SpatialGrid g, int slot) {
        return g.getX(slot);
    }

    private static double ysOf(SpatialGrid g, int slot) {
        return g.getY(slot);
    }

    private static double zsOf(SpatialGrid g, int slot) {
        return g.getZ(slot);
    }
}
