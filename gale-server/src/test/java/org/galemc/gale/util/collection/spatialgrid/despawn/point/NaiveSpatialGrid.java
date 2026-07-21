// Generated with AI

package org.galemc.gale.util.collection.spatialgrid.despawn.point;

import java.util.ArrayList;
import java.util.List;

public abstract class NaiveSpatialGrid implements AbstractSpatialGrid {

    private final int W;
    private final int H;
    private final double Wd;
    protected final double Hd;
    protected final double W2;
    private final double H2;
    protected final double invW2;
    protected final double invH2;
    private final int cellSizeXZ;
    private final int cellSizeY;
    protected final List<Point> points;
    private int liveCount;

    protected static final class Point {
        double x, y, z;
        boolean alive;
        Point(double x, double y, double z) { this.x = x; this.y = y; this.z = z; this.alive = true; }
    }

    public NaiveSpatialGrid(int W, int H, int initialCapacity) {
        if (W <= 0 || H <= 0) throw new IllegalArgumentException();
        this.W = W; this.H = H;
        this.Wd = (double) W; this.Hd = (double) H;
        this.W2 = Wd * Wd; this.H2 = Hd * Hd;
        this.invW2 = 1.0 / W2; this.invH2 = 1.0 / H2;
        this.cellSizeXZ = 1 << ceilPow2Shift(W);
        this.cellSizeY  = 1 << ceilPow2Shift(H);
        this.points = new ArrayList<>(Math.max(16, initialCapacity));
    }

    private static int ceilPow2Shift(int v) {
        if (v <= 1) return 0;
        return 32 - Integer.numberOfLeadingZeros(v - 1);
    }

    @Override
    public int add(double x, double y, double z, int fx, int fy, int fz) {
        liveCount++;
        Point p = new Point(x, y, z);
        points.add(p);
        return points.size() - 1;
    }

    @Override
    public void remove(int slot) {
        liveCount--;
        Point p = points.get(slot);
        if (p != null) p.alive = false;
    }

    @Override
    public void move(int slot, double newX, double newY, double newZ, int fx, int fy, int fz) {
        Point p = points.get(slot);
        if (p == null || !p.alive) return;
        p.x = newX; p.y = newY; p.z = newZ;
    }

    @Override
    public int size() { return liveCount; }

    @Override
    public int getCellSizeXZ() { return cellSizeXZ; }

    @Override
    public int getCellSizeY() { return cellSizeY; }

    @Override
    public double getX(int slot) { return (slot >= 0 && slot < points.size() && points.get(slot) != null) ? points.get(slot).x : Double.NaN; }

    @Override
    public double getY(int slot) { return (slot >= 0 && slot < points.size() && points.get(slot) != null) ? points.get(slot).y : Double.NaN; }

    @Override
    public double getZ(int slot) { return (slot >= 0 && slot < points.size() && points.get(slot) != null) ? points.get(slot).z : Double.NaN; }

    @Override
    public boolean containsKey(int slot) { return slot >= 0 && slot < points.size() && points.get(slot) != null && points.get(slot).alive; }

}
