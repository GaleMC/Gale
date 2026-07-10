// Generated with AI

package org.galemc.gale.util.collection.spatialgrid;

public final class CylinderNaiveSpatialGrid extends NaiveSpatialGrid {

    public CylinderNaiveSpatialGrid(int W, int H, int initialCapacity) {
        super(W, H, initialCapacity);
    }

    @Override
    public int anyIn(double cx, double cy, double cz, int fcx, int fcy, int fcz) {
        double minY = cy - Hd;
        double maxY = cy + Hd;
        for (int i = 0, n = points.size(); i < n; i++) {
            Point p = points.get(i);
            if (p == null || !p.alive) continue;
            double py = p.y;
            if (py < minY || py > maxY) continue;
            double dx = p.x - cx;
            double dz = p.z - cz;
            if (dx*dx + dz*dz <= W2) return i;
        }
        return -1;
    }

    @Override
    public int nearestIn(double cx, double cy, double cz, int fcx, int fcy, int fcz) {
        double minY = cy - Hd;
        double maxY = cy + Hd;
        double best = Double.POSITIVE_INFINITY;
        int bestSlot = -1;
        for (int i = 0, n = points.size(); i < n; i++) {
            Point p = points.get(i);
            if (p == null || !p.alive) continue;
            double py = p.y;
            if (py < minY || py > maxY) continue;
            double dx = p.x - cx;
            double dz = p.z - cz;
            double d2planar = dx*dx + dz*dz;
            if (d2planar <= W2) {
                if (d2planar < best) { best = d2planar; bestSlot = i; }
            }
        }
        return bestSlot;
    }

}
