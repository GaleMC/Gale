// Generated with AI

package org.galemc.gale.util.collection.spatialgrid.despawn.point;

public final class EllipsoidNaiveSpatialGrid extends NaiveSpatialGrid {

    public EllipsoidNaiveSpatialGrid(int W, int H, int initialCapacity) {
        super(W, H, initialCapacity);
    }

    @Override
    public int anyIn(double cx, double cy, double cz, int fcx, int fcy, int fcz) {
        for (int i = 0, n = points.size(); i < n; i++) {
            Point p = points.get(i);
            if (p == null || !p.alive) continue;
            double dx = p.x - cx;
            double dy = p.y - cy;
            double dz = p.z - cz;
            if (dx*dx*invW2 + dy*dy*invH2 + dz*dz*invW2 <= 1.0) return i;
        }
        return -1;
    }

    @Override
    public int nearestIn(double cx, double cy, double cz, int fcx, int fcy, int fcz) {
        double best = Double.POSITIVE_INFINITY;
        int bestSlot = -1;
        for (int i = 0, n = points.size(); i < n; i++) {
            Point p = points.get(i);
            if (p == null || !p.alive) continue;
            double dx = p.x - cx;
            double dy = p.y - cy;
            double dz = p.z - cz;
            if (dx*dx*invW2 + dy*dy*invH2 + dz*dz*invW2 <= 1.0) {
                double d2 = dx*dx + dy*dy + dz*dz;
                if (d2 < best) { best = d2; bestSlot = i; }
            }
        }
        return bestSlot;
    }

}
