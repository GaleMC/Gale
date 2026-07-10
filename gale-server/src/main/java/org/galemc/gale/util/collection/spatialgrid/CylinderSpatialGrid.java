// Generated with AI

package org.galemc.gale.util.collection.spatialgrid;

public final class CylinderSpatialGrid extends SpatialGrid {

    // ---------------- constructor ----------------

    public CylinderSpatialGrid(int W, int H, int initialCapacity) {
        super(W, H, initialCapacity);
    }

    // ---------------- geometric tests ----------------

    private boolean pointInCylinderSlot(int s, double cxD, double cyD, double czD) {
        double py = ys[s];
        if (py < cyD - H || py > cyD + H) return false;
        double dx = xs[s] - cxD;
        double dz = zs[s] - czD;
        return dx * dx + dz * dz <= W2;
    }

    // ---------------- queries ----------------

    @Override
    public int anyIn(double cxD, double cyD, double czD, int fcx, int fcy, int fcz) {
        int ccx = fcx >> shiftXZ;
        int ccy = fcy >> shiftY;
        int ccz = fcz >> shiftXZ;
        for (int dx : OFF_ORDER) {
            for (int dy : OFF_ORDER) {
                for (int dz : OFF_ORDER) {
                    int idx = bucketIndexForCellCoords(ccx + dx, ccy + dy, ccz + dz);
                    if (idx == -1) continue;
                    if (aabbDirty[idx]) recomputeAABB(idx);
                    if (!aabbIntersectsCylinder(idx, cxD, cyD, czD)) continue;
                    int cnt = inlineCount[idx];
                    if (cnt > 0) {
                        if (inline0[idx] != -1 && pointInCylinderSlot(inline0[idx], cxD, cyD, czD)) return inline0[idx];
                        if (cnt >= 2 && inline1[idx] != -1 && pointInCylinderSlot(inline1[idx], cxD, cyD, czD)) return inline1[idx];
                        if (cnt >= 3 && inline2[idx] != -1 && pointInCylinderSlot(inline2[idx], cxD, cyD, czD)) return inline2[idx];
                        if (cnt >= 4 && inline3[idx] != -1 && pointInCylinderSlot(inline3[idx], cxD, cyD, czD)) return inline3[idx];
                    }
                    int h = overflowHead[idx];
                    while (h != -1) {
                        int s = ovSlot[h];
                        if (pointInCylinderSlot(s, cxD, cyD, czD)) return s;
                        h = ovNext[h];
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public int nearestIn(double cxD, double cyD, double czD, int fcx, int fcy, int fcz) {
        int ccx = fcx >> shiftXZ;
        int ccy = fcy >> shiftY;
        int ccz = fcz >> shiftXZ;
        double best = Double.POSITIVE_INFINITY;
        int bestSlot = -1;
        for (int dx : OFF_ORDER) {
            for (int dy : OFF_ORDER) {
                for (int dz : OFF_ORDER) {
                    int idx = bucketIndexForCellCoords(ccx + dx, ccy + dy, ccz + dz);
                    if (idx == -1) continue;
                    if (aabbDirty[idx]) recomputeAABB(idx);
                    if (!aabbIntersectsCylinder(idx, cxD, cyD, czD)) continue;
                    int cnt = inlineCount[idx];
                    if (cnt > 0) {
                        if (inline0[idx] != -1) {
                            int s = inline0[idx];
                            if (pointInCylinderSlot(s, cxD, cyD, czD)) {
                                double dd = dist2SlotPlanar(s, cxD, czD);
                                if (dd < best) { best = dd; bestSlot = s; }
                            }
                        }
                        if (cnt >= 2 && inline1[idx] != -1) {
                            int s = inline1[idx];
                            if (pointInCylinderSlot(s, cxD, cyD, czD)) {
                                double dd = dist2SlotPlanar(s, cxD, czD);
                                if (dd < best) { best = dd; bestSlot = s; }
                            }
                        }
                        if (cnt >= 3 && inline2[idx] != -1) {
                            int s = inline2[idx];
                            if (pointInCylinderSlot(s, cxD, cyD, czD)) {
                                double dd = dist2SlotPlanar(s, cxD, czD);
                                if (dd < best) { best = dd; bestSlot = s; }
                            }
                        }
                        if (cnt >= 4 && inline3[idx] != -1) {
                            int s = inline3[idx];
                            if (pointInCylinderSlot(s, cxD, cyD, czD)) {
                                double dd = dist2SlotPlanar(s, cxD, czD);
                                if (dd < best) { best = dd; bestSlot = s; }
                            }
                        }
                    }
                    int h = overflowHead[idx];
                    while (h != -1) {
                        int s = ovSlot[h];
                        if (pointInCylinderSlot(s, cxD, cyD, czD)) {
                            double dd = dist2SlotPlanar(s, cxD, czD);
                            if (dd < best) { best = dd; bestSlot = s; }
                        }
                        h = ovNext[h];
                    }
                }
            }
        }
        return bestSlot;
    }

    // ---------------- AABB helpers ----------------

    private boolean aabbIntersectsCylinder(int idx, double cxD, double cyD, double czD) {
        if (inlineCount[idx] == 0 && overflowHead[idx] == -1) return false;
        double minx = bMinX[idx], maxx = bMaxX[idx];
        double miny = bMinY[idx], maxy = bMaxY[idx];
        double minz = bMinZ[idx], maxz = bMaxZ[idx];
        double cxMin = cxD - W, cxMax = cxD + W;
        double cyMin = cyD - H, cyMax = cyD + H;
        double czMin = czD - W, czMax = czD + W;
        if (maxx < cxMin || minx > cxMax) return false;
        if (maxy < cyMin || miny > cyMax) return false;
        if (maxz < czMin || minz > czMax) return false;
        return true;
    }

}
