// Generated with AI

package org.galemc.gale.util.collection.spatialgrid.despawn.point;

import org.bukkit.support.environment.Normal;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@Normal
public class SpatialGridFuzzTest {
    private static final int W = 3;
    private static final int H = 2;
    private static final int INITIAL_CAP = 1024;

    private static final int ITERATIONS = 100000;

    private static final double EPS = 1e-9;

    private static int floorInt(double v) { return (int) Math.floor(v); }

    private static final class SlotPair {
        int sGrid;
        int sNaive;
        SlotPair(int g, int n) { sGrid = g; sNaive = n; }
    }

    @Test
    public void fuzzCompareWithNaiveEllipsoid() {
        SpatialGrid grid = new EllipsoidSpatialGrid(W, H, INITIAL_CAP);
        NaiveSpatialGrid naive = new EllipsoidNaiveSpatialGrid(W, H, INITIAL_CAP);
        this.fuzzCompareWithNaive(grid, naive, 123456789L);
    }

    @Test
    public void fuzzCompareWithNaiveCylinder() {
        SpatialGrid grid = new CylinderSpatialGrid(W, H, INITIAL_CAP);
        NaiveSpatialGrid naive = new CylinderNaiveSpatialGrid(W, H, INITIAL_CAP);
        this.fuzzCompareWithNaive(grid, naive, 234892983L);
    }

    private void fuzzCompareWithNaive(AbstractSpatialGrid grid, NaiveSpatialGrid naive, long seed) {
        Random rnd = new Random(seed);

        List<SlotPair> valid = new ArrayList<>();

        // coordinate ranges chosen to create many cell interactions:
        // X/Z range larger, Y range smaller
        final double XZ_MIN = -500.0, XZ_MAX = 500.0;
        final double Y_MIN = -50.0, Y_MAX = 50.0;

        for (int it = 0; it < ITERATIONS; it++) {
            // choose action: 0 = add, 1 = remove, 2 = move
            int action = rnd.nextInt(3);

            if ((action == 1 || action == 2) && valid.isEmpty()) {
                // cannot remove or move when no points; perform a query instead
                performAndAssertRandomQuery(rnd, grid, naive, XZ_MIN, XZ_MAX, Y_MIN, Y_MAX);
                assertEquals(grid.size(), naive.size());
                continue;
            }

            if (action == 0) {
                // add
                double x = XZ_MIN + rnd.nextDouble() * (XZ_MAX - XZ_MIN);
                double y = Y_MIN + rnd.nextDouble() * (Y_MAX - Y_MIN);
                double z = XZ_MIN + rnd.nextDouble() * (XZ_MAX - XZ_MIN);
                int fx = floorInt(x), fy = floorInt(y), fz = floorInt(z);
                int sg = grid.add(x, y, z, fx, fy, fz);
                int sn = naive.add(x, y, z, fx, fy, fz);
                valid.add(new SlotPair(sg, sn));
            } else if (action == 1) {
                // remove: pick random existing pair
                int idx = rnd.nextInt(valid.size());
                SlotPair p = valid.remove(idx);
                grid.remove(p.sGrid);
                naive.remove(p.sNaive);
            } else {
                // move: pick random existing pair and move both to same new coords
                int idx = rnd.nextInt(valid.size());
                SlotPair p = valid.get(idx);
                double nx = XZ_MIN + rnd.nextDouble() * (XZ_MAX - XZ_MIN);
                double ny = Y_MIN + rnd.nextDouble() * (Y_MAX - Y_MIN);
                double nz = XZ_MIN + rnd.nextDouble() * (XZ_MAX - XZ_MIN);
                int fx = floorInt(nx), fy = floorInt(ny), fz = floorInt(nz);
                grid.move(p.sGrid, nx, ny, nz, fx, fy, fz);
                naive.move(p.sNaive, nx, ny, nz, fx, fy, fz);
            }

            // after action, run a random query and assert equality/consistency
            performAndAssertRandomQuery(rnd, grid, naive, XZ_MIN, XZ_MAX, Y_MIN, Y_MAX);

            // sizes (both implementations allocate slots monotonically)
            assertEquals(grid.size(), naive.size());
        }
    }

    private void performAndAssertRandomQuery(Random rnd, AbstractSpatialGrid grid, AbstractSpatialGrid naive,
                                             double XZ_MIN, double XZ_MAX, double Y_MIN, double Y_MAX) {
        // choose query type: 0 anyEllipsoid, 1 nearestEllipsoid, 2 anyCylinder, 3 nearestCylinder
        int qtype = rnd.nextInt(2);
        if (grid instanceof CylinderSpatialGrid) qtype += 2;
        double cx = XZ_MIN + rnd.nextDouble() * (XZ_MAX - XZ_MIN);
        double cy = Y_MIN + rnd.nextDouble() * (Y_MAX - Y_MIN);
        double cz = XZ_MIN + rnd.nextDouble() * (XZ_MAX - XZ_MIN);
        int fcx = floorInt(cx), fcy = floorInt(cy), fcz = floorInt(cz);

        switch (qtype) {
            case 0: {
                boolean a = grid.anyIn(cx, cy, cz, fcx, fcy, fcz) != -1;
                boolean b = naive.anyIn(cx, cy, cz, fcx, fcy, fcz) != -1;
                assertEquals(b, a);
                break;
            }
            case 1: {
                int sg = grid.nearestIn(cx, cy, cz, fcx, fcy, fcz);
                int sn = naive.nearestIn(cx, cy, cz, fcx, fcy, fcz);
                if (sn == -1) {
                    assertEquals(-1, sg);
                } else {
                    assertNotEquals(-1, sg);
                    double nx = naive.getX(sn), ny = naive.getY(sn), nz = naive.getZ(sn);
                    // ensure naive's returned slot is inside ellipsoid
                    assertTrue(pointInEllipsoid(nx, ny, nz, cx, cy, cz));
                    // ensure grid's returned slot is inside ellipsoid
                    double gx = grid.getX(sg), gy = grid.getY(sg), gz = grid.getZ(sg);
                    assertTrue(pointInEllipsoid(gx, gy, gz, cx, cy, cz));
                    double dNaive = dist2(nx, ny, nz, cx, cy, cz);
                    double dGrid = dist2(gx, gy, gz, cx, cy, cz);
                    assertEquals(dNaive, dGrid, Math.max(1e-9, Math.abs(dNaive) * 1e-9));
                }
                break;
            }
            case 2: {
                boolean a = grid.anyIn(cx, cy, cz, fcx, fcy, fcz) != -1;
                boolean b = naive.anyIn(cx, cy, cz, fcx, fcy, fcz) != -1;
                assertEquals(b, a);
                break;
            }
            default: {
                int sg = grid.nearestIn(cx, cy, cz, fcx, fcy, fcz);
                int sn = naive.nearestIn(cx, cy, cz, fcx, fcy, fcz);
                if (sn == -1) {
                    assertEquals(-1, sg);
                } else {
                    assertNotEquals(-1, sg);
                    double nx = naive.getX(sn), ny = naive.getY(sn), nz = naive.getZ(sn);
                    assertTrue(pointInCylinder(nx, ny, nz, cx, cy, cz));
                    double gx = grid.getX(sg), gy = grid.getY(sg), gz = grid.getZ(sg);
                    assertTrue(pointInCylinder(gx, gy, gz, cx, cy, cz));
                    double dNaive = dist2Planar(nx, nz, cx, cz);
                    double dGrid = dist2Planar(gx, gz, cx, cz);
                    assertEquals(dNaive, dGrid, Math.max(1e-9, Math.abs(dNaive) * 1e-9));
                }
                break;
            }
        }
    }

    private static boolean pointInEllipsoid(double px, double py, double pz, double cx, double cy, double cz) {
        double dx = px - cx, dy = py - cy, dz = pz - cz;
        double invW2 = 1.0 / (W * (double) W);
        double invH2 = 1.0 / (H * (double) H);
        return dx * dx * invW2 + dy * dy * invH2 + dz * dz * invW2 <= 1.0 + 1e-12;
    }

    private static boolean pointInCylinder(double px, double py, double pz, double cx, double cy, double cz) {
        if (py < cy - H - 1e-12 || py > cy + H + 1e-12) return false;
        double dx = px - cx, dz = pz - cz;
        return dx * dx + dz * dz <= (W * (double) W) + 1e-12;
    }

    private static double dist2(double x, double y, double z, double cx, double cy, double cz) {
        double dx = x - cx, dy = y - cy, dz = z - cz;
        return dx * dx + dy * dy + dz * dz;
    }

    private static double dist2Planar(double x, double z, double cx, double cz) {
        double dx = x - cx, dz = z - cz;
        return dx * dx + dz * dz;
    }
}
