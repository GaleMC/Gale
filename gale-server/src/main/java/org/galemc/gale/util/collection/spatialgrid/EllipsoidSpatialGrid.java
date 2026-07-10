// Generated with AI

package org.galemc.gale.util.collection.spatialgrid;

public final class EllipsoidSpatialGrid extends SpatialGrid {

    // ---------------- constructor ----------------

    public EllipsoidSpatialGrid(int W, int H, int initialCapacity) {
        super(W, H, initialCapacity);
    }

    // ---------------- geometric tests ----------------

    private boolean pointInEllipsoidSlot(int s, double cxD, double cyD, double czD) {
        double dx = xs[s] - cxD;
        double dy = ys[s] - cyD;
        double dz = zs[s] - czD;
        return dx * dx * invW2 + dy * dy * invH2 + dz * dz * invW2 <= 1.0;
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
                    if (!aabbIntersectsEllipsoid(idx, cxD, cyD, czD)) continue;
                    int cnt = inlineCount[idx];
                    if (cnt > 0) {
                        if (inline0[idx] != -1 && pointInEllipsoidSlot(inline0[idx], cxD, cyD, czD)) return inline0[idx];
                        if (cnt >= 2 && inline1[idx] != -1 && pointInEllipsoidSlot(inline1[idx], cxD, cyD, czD)) return inline1[idx];
                        if (cnt >= 3 && inline2[idx] != -1 && pointInEllipsoidSlot(inline2[idx], cxD, cyD, czD)) return inline2[idx];
                        if (cnt >= 4 && inline3[idx] != -1 && pointInEllipsoidSlot(inline3[idx], cxD, cyD, czD)) return inline3[idx];
                    }
                    int h = overflowHead[idx];
                    while (h != -1) {
                        int s = ovSlot[h];
                        if (pointInEllipsoidSlot(s, cxD, cyD, czD)) return s;
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
                    if (!aabbIntersectsEllipsoid(idx, cxD, cyD, czD)) continue;
                    int cnt = inlineCount[idx];
                    if (cnt > 0) {
                        if (inline0[idx] != -1) {
                            int s = inline0[idx];
                            if (pointInEllipsoidSlot(s, cxD, cyD, czD)) {
                                double dd = dist2SlotToCenter(s, cxD, cyD, czD);
                                if (dd < best) { best = dd; bestSlot = s; }
                            }
                        }
                        if (cnt >= 2 && inline1[idx] != -1) {
                            int s = inline1[idx];
                            if (pointInEllipsoidSlot(s, cxD, cyD, czD)) {
                                double dd = dist2SlotToCenter(s, cxD, cyD, czD);
                                if (dd < best) { best = dd; bestSlot = s; }
                            }
                        }
                        if (cnt >= 3 && inline2[idx] != -1) {
                            int s = inline2[idx];
                            if (pointInEllipsoidSlot(s, cxD, cyD, czD)) {
                                double dd = dist2SlotToCenter(s, cxD, cyD, czD);
                                if (dd < best) { best = dd; bestSlot = s; }
                            }
                        }
                        if (cnt >= 4 && inline3[idx] != -1) {
                            int s = inline3[idx];
                            if (pointInEllipsoidSlot(s, cxD, cyD, czD)) {
                                double dd = dist2SlotToCenter(s, cxD, cyD, czD);
                                if (dd < best) { best = dd; bestSlot = s; }
                            }
                        }
                    }
                    int h = overflowHead[idx];
                    while (h != -1) {
                        int s = ovSlot[h];
                        if (pointInEllipsoidSlot(s, cxD, cyD, czD)) {
                            double dd = dist2SlotToCenter(s, cxD, cyD, czD);
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

    private boolean aabbIntersectsEllipsoid(int idx, double cxD, double cyD, double czD) {
        if (inlineCount[idx] == 0 && overflowHead[idx] == -1) return false;
        double minx = bMinX[idx], maxx = bMaxX[idx];
        double miny = bMinY[idx], maxy = bMaxY[idx];
        double minz = bMinZ[idx], maxz = bMaxZ[idx];
        double exMin = cxD - W, exMax = cxD + W;
        double eyMin = cyD - H, eyMax = cyD + H;
        double ezMin = czD - W, ezMax = czD + W;
        if (maxx < exMin || minx > exMax) return false;
        if (maxy < eyMin || miny > eyMax) return false;
        if (maxz < ezMin || minz > ezMax) return false;
        return true;
    }

}
