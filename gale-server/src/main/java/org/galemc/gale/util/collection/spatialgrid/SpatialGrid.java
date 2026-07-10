// Generated with AI

package org.galemc.gale.util.collection.spatialgrid;

import net.minecraft.core.BlockPos;
import java.util.Arrays;

public abstract class SpatialGrid implements AbstractSpatialGrid {

    // ---------------- configuration ----------------

    protected final int shiftXZ;
    protected final int shiftY;
    private final int cellSizeXZ;
    private final int cellSizeY;

    protected final double W, H, W2, H2;
    protected final double invW2, invH2;

    // robin-hood hash table arrays
    private long[] keys;
    private int tableMask;
    private int usedBuckets;

    // inline per-bucket storage (capacity 4)
    private final int INLINE_CAP = 4;
    protected int[] inlineCount;
    protected int[] inline0, inline1, inline2, inline3;
    protected int[] overflowHead;

    // per-bucket AABB and dirty flag
    protected double[] bMinX, bMinY, bMinZ, bMaxX, bMaxY, bMaxZ;
    protected boolean[] aabbDirty;

    // overflow pool
    protected int[] ovNext;
    protected int[] ovSlot;
    private int ovCapacity;
    private int ovSize;

    // per-slot SoA
    protected double[] xs, ys, zs;
    private long[] slotPackedCell;
    private int everAllocatedSlotCount;
    private int liveCount;
    private int capacity;

    private int freeHead = -1;      // head of free slot singly-linked list (-1 = none)
    private int[] freeNext;         // per-slot next pointer for free list
    private int[] slotNext; // per-slot next pointer (if used elsewhere)

    private static final long MULT = 0x9E3779B97F4A7C15L;

    // distinct sentinels
    private static final long SLOT_EMPTY = Long.MIN_VALUE;
    private static final long EMPTY_KEY = 9070971064320001200L;

    protected static final int[] OFF_ORDER = {0, 1, -1};

    // ---------------- constructor ----------------

    public SpatialGrid(int W, int H, int initialCapacity) {
        this.W = (double) W;
        this.H = (double) H;
        this.W2 = this.W * this.W;
        this.H2 = this.H * this.H;
        this.invW2 = 1.0 / W2;
        this.invH2 = 1.0 / H2;

        this.shiftXZ = ceilPow2Shift(W);
        this.shiftY = ceilPow2Shift(H);
        this.cellSizeXZ = 1 << shiftXZ;
        this.cellSizeY = 1 << shiftY;

        int tableSize = nextPow2(Math.max(16, initialCapacity * 2));
        keys = new long[tableSize];
        tableMask = tableSize - 1;
        usedBuckets = 0;
        Arrays.fill(keys, EMPTY_KEY);

        inlineCount = new int[tableSize];
        inline0 = new int[tableSize];
        Arrays.fill(inline0, -1);
        inline1 = new int[tableSize];
        Arrays.fill(inline1, -1);
        inline2 = new int[tableSize];
        Arrays.fill(inline2, -1);
        inline3 = new int[tableSize];
        Arrays.fill(inline3, -1);
        overflowHead = new int[tableSize];
        Arrays.fill(overflowHead, -1);

        bMinX = new double[tableSize];
        Arrays.fill(bMinX, Double.POSITIVE_INFINITY);
        bMinY = new double[tableSize];
        Arrays.fill(bMinY, Double.POSITIVE_INFINITY);
        bMinZ = new double[tableSize];
        Arrays.fill(bMinZ, Double.POSITIVE_INFINITY);
        bMaxX = new double[tableSize];
        Arrays.fill(bMaxX, Double.NEGATIVE_INFINITY);
        bMaxY = new double[tableSize];
        Arrays.fill(bMaxY, Double.NEGATIVE_INFINITY);
        bMaxZ = new double[tableSize];
        Arrays.fill(bMaxZ, Double.NEGATIVE_INFINITY);
        aabbDirty = new boolean[tableSize];

        ovCapacity = Math.max(64, initialCapacity / 4);
        ovNext = new int[ovCapacity];
        ovSlot = new int[ovCapacity];
        ovSize = 0;
        Arrays.fill(ovNext, -1);

        capacity = Math.max(64, initialCapacity);
        xs = new double[capacity];
        ys = new double[capacity];
        zs = new double[capacity];
        slotPackedCell = new long[capacity];
        Arrays.fill(slotPackedCell, SLOT_EMPTY);
        everAllocatedSlotCount = 0;

        freeNext = new int[capacity];
        Arrays.fill(freeNext, -1);
        slotNext = new int[capacity];
        Arrays.fill(slotNext, -1);
    }

    // ---------------- utilities ----------------

    private static int ceilPow2Shift(int v) {
        if (v <= 1) return 0;
        return 32 - Integer.numberOfLeadingZeros(v - 1);
    }
    private static int nextPow2(int v) {
        if (v <= 1) return 1;
        return 1 << (32 - Integer.numberOfLeadingZeros(v - 1));
    }

    private static long packCoords(int cx, int cy, int cz) {
        return BlockPos.asLong(cx, cy, cz);
    }

    private int idealIndex(long key) {
        long h = key * MULT;
        return (int) ((h >>> 32) & 0xFFFFFFFFL) & tableMask;
    }

    // ---------------- overflow pool ----------------

    private int allocOverflowNode(int slot) {
        if (ovSize >= ovCapacity) {
            int ncap = ovCapacity + (ovCapacity >> 1);
            int[] nn = new int[ncap];
            int[] ns = new int[ncap];
            System.arraycopy(ovNext, 0, nn, 0, ovCapacity);
            System.arraycopy(ovSlot, 0, ns, 0, ovCapacity);
            for (int i = ovCapacity; i < ncap; i++) nn[i] = -1;
            ovNext = nn; ovSlot = ns; ovCapacity = ncap;
        }
        int idx = ovSize++;
        ovSlot[idx] = slot;
        ovNext[idx] = -1;
        return idx;
    }

    private int removeOverflowValue(int head, int slot) {
        int cur = head, prev = -1;
        while (cur != -1) {
            if (ovSlot[cur] == slot) {
                int next = ovNext[cur];
                if (prev == -1) head = next;
                else ovNext[prev] = next;
                return head;
            }
            prev = cur;
            cur = ovNext[cur];
        }
        return head;
    }

    // ---------------- robin-hood helpers ----------------

    private int findIndex(long key) {
        int idx = idealIndex(key);
        int tableSize = tableMask + 1;
        int dist = 0;
        while (true) {
            long k = keys[idx];
            if (k == EMPTY_KEY) return -1;
            if (k == key) return idx;
            dist++;
            idx = (idx + 1) & tableMask;
            if (dist > tableSize) return -1;
        }
    }

    private void ensureTableForNewBucket() {
        int tableSize = tableMask + 1;
        if (usedBuckets * 2 >= tableSize) {
            rehash(tableSize << 1);
        }
    }

    private void rehash(int newSize) {
        long[] oldKeys = keys;
        int oldSize = oldKeys.length;
        int[] oldInlineCount = inlineCount;
        int[] oldInline0 = inline0, oldInline1 = inline1, oldInline2 = inline2, oldInline3 = inline3;
        int[] oldOvHead = overflowHead;
        double[] oldbMinX = bMinX, oldbMinY = bMinY, oldbMinZ = bMinZ;
        double[] oldbMaxX = bMaxX, oldbMaxY = bMaxY, oldbMaxZ = bMaxZ;
        boolean[] oldDirty = aabbDirty;

        keys = new long[newSize];
        tableMask = newSize - 1;
        inlineCount = new int[newSize];
        inline0 = new int[newSize]; inline1 = new int[newSize]; inline2 = new int[newSize]; inline3 = new int[newSize];
        overflowHead = new int[newSize];
        bMinX = new double[newSize]; bMinY = new double[newSize]; bMinZ = new double[newSize];
        bMaxX = new double[newSize]; bMaxY = new double[newSize]; bMaxZ = new double[newSize];
        aabbDirty = new boolean[newSize];
        for (int i = 0; i < newSize; i++) {
            keys[i] = EMPTY_KEY;
            inlineCount[i] = 0;
            inline0[i] = inline1[i] = inline2[i] = inline3[i] = -1;
            overflowHead[i] = -1;
            bMinX[i] = bMinY[i] = bMinZ[i] = Double.POSITIVE_INFINITY;
            bMaxX[i] = bMaxY[i] = bMaxZ[i] = Double.NEGATIVE_INFINITY;
            aabbDirty[i] = false;
        }
        usedBuckets = 0;
        for (int i = 0; i < oldSize; i++) {
            long k = oldKeys[i];
            if (k != EMPTY_KEY) {
                int idx = idealIndex(k);
                while (keys[idx] != EMPTY_KEY) idx = (idx + 1) & tableMask;
                keys[idx] = k;
                inlineCount[idx] = oldInlineCount[i];
                inline0[idx] = oldInline0[i]; inline1[idx] = oldInline1[i];
                inline2[idx] = oldInline2[i]; inline3[idx] = oldInline3[i];
                overflowHead[idx] = oldOvHead[i];
                bMinX[idx] = oldbMinX[i]; bMinY[idx] = oldbMinY[i]; bMinZ[idx] = oldbMinZ[i];
                bMaxX[idx] = oldbMaxX[i]; bMaxY[idx] = oldbMaxY[i]; bMaxZ[idx] = oldbMaxZ[i];
                aabbDirty[idx] = oldDirty[i];
                usedBuckets++;
            }
        }
    }

    // swap-last removal helper for inline arrays
    private boolean removeInlineSlotAtBucket(int idx, int slot) {
        int cnt = inlineCount[idx];
        if (cnt == 0) return false;
        int p = -1;
        if (inline0[idx] == slot) p = 0;
        else if (cnt >= 2 && inline1[idx] == slot) p = 1;
        else if (cnt >= 3 && inline2[idx] == slot) p = 2;
        else if (cnt >= 4 && inline3[idx] == slot) p = 3;
        if (p == -1) return false;
        int lastPos = cnt - 1;
        int lastVal;
        switch (lastPos) {
            case 0: lastVal = inline0[idx]; break;
            case 1: lastVal = inline1[idx]; break;
            case 2: lastVal = inline2[idx]; break;
            case 3: lastVal = inline3[idx]; break;
            default: lastVal = -1; break;
        }
        switch (p) {
            case 0: inline0[idx] = lastVal; break;
            case 1: inline1[idx] = lastVal; break;
            case 2: inline2[idx] = lastVal; break;
            case 3: inline3[idx] = lastVal; break;
        }
        switch (lastPos) {
            case 0: inline0[idx] = -1; break;
            case 1: inline1[idx] = -1; break;
            case 2: inline2[idx] = -1; break;
            case 3: inline3[idx] = -1; break;
        }
        inlineCount[idx] = cnt - 1;
        return true;
    }

    // insert key with slot using robin-hood; correct initialization for current bucket
    private void insertKeyWithSlot(long key, int slot) {
        ensureTableForNewBucket();

        int idx = idealIndex(key);
        int tableSize = tableMask + 1;

        long curKey = key;
        int curInlineCount = 1;
        int curInline0 = slot, curInline1 = -1, curInline2 = -1, curInline3 = -1;
        int curOverflowHead = -1;
        double curMinX = xs[slot], curMinY = ys[slot], curMinZ = zs[slot];
        double curMaxX = xs[slot], curMaxY = ys[slot], curMaxZ = zs[slot];

        int probeDist = 0;
        while (true) {
            long k = keys[idx];
            if (k == EMPTY_KEY) {
                keys[idx] = curKey;
                inlineCount[idx] = curInlineCount;
                inline0[idx] = curInline0; inline1[idx] = curInline1; inline2[idx] = curInline2; inline3[idx] = curInline3;
                overflowHead[idx] = curOverflowHead;
                bMinX[idx] = curMinX; bMinY[idx] = curMinY; bMinZ[idx] = curMinZ;
                bMaxX[idx] = curMaxX; bMaxY[idx] = curMaxY; bMaxZ[idx] = curMaxZ;
                aabbDirty[idx] = false;
                usedBuckets++;
                return;
            }
            if (k == curKey) {
                int cnt = inlineCount[idx];
                if (cnt < INLINE_CAP) {
                    switch (cnt) {
                        case 0: inline0[idx] = slot; break;
                        case 1: inline1[idx] = slot; break;
                        case 2: inline2[idx] = slot; break;
                        case 3: inline3[idx] = slot; break;
                    }
                    inlineCount[idx] = cnt + 1;
                } else {
                    int node = allocOverflowNode(slot);
                    ovNext[node] = overflowHead[idx];
                    overflowHead[idx] = node;
                }
                double sx = xs[slot], sy = ys[slot], sz = zs[slot];
                if (sx < bMinX[idx]) bMinX[idx] = sx;
                if (sx > bMaxX[idx]) bMaxX[idx] = sx;
                if (sy < bMinY[idx]) bMinY[idx] = sy;
                if (sy > bMaxY[idx]) bMaxY[idx] = sy;
                if (sz < bMinZ[idx]) bMinZ[idx] = sz;
                if (sz > bMaxZ[idx]) bMaxZ[idx] = sz;
                return;
            }
            int occIdeal = idealIndex(k);
            int occDist = (idx - occIdeal) & tableMask;
            if (occDist < probeDist) {
                long displacedKey = keys[idx];
                int dCount = inlineCount[idx];
                int d0 = inline0[idx], d1 = inline1[idx], d2 = inline2[idx], d3 = inline3[idx];
                int dOvHead = overflowHead[idx];
                double dMinX = bMinX[idx], dMinY = bMinY[idx], dMinZ = bMinZ[idx];
                double dMaxX = bMaxX[idx], dMaxY = bMaxY[idx], dMaxZ = bMaxZ[idx];
                boolean dDirty = aabbDirty[idx];

                keys[idx] = curKey;
                inlineCount[idx] = curInlineCount;
                inline0[idx] = curInline0; inline1[idx] = curInline1; inline2[idx] = curInline2; inline3[idx] = curInline3;
                overflowHead[idx] = curOverflowHead;
                bMinX[idx] = curMinX; bMinY[idx] = curMinY; bMinZ[idx] = curMinZ;
                bMaxX[idx] = curMaxX; bMaxY[idx] = curMaxY; bMaxZ[idx] = curMaxZ;
                aabbDirty[idx] = false;

                curKey = displacedKey;
                curInlineCount = dCount;
                curInline0 = d0; curInline1 = d1; curInline2 = d2; curInline3 = d3;
                curOverflowHead = dOvHead;
                curMinX = dMinX; curMinY = dMinY; curMinZ = dMinZ;
                curMaxX = dMaxX; curMaxY = dMaxY; curMaxZ = dMaxZ;
                probeDist = occDist;
            }
            idx = (idx + 1) & tableMask;
            probeDist++;
            if (probeDist > tableSize) throw new IllegalStateException("Hash table full unexpectedly");
        }
    }

    private void deleteBucketAt(int idx) {
        int tableSize = tableMask + 1;
        keys[idx] = EMPTY_KEY;
        inlineCount[idx] = 0;
        inline0[idx] = inline1[idx] = inline2[idx] = inline3[idx] = -1;
        overflowHead[idx] = -1;
        bMinX[idx] = bMinY[idx] = bMinZ[idx] = Double.POSITIVE_INFINITY;
        bMaxX[idx] = bMaxY[idx] = bMaxZ[idx] = Double.NEGATIVE_INFINITY;
        aabbDirty[idx] = false;
        usedBuckets--;
        int i = (idx + 1) & tableMask;
        while (keys[i] != EMPTY_KEY) {
            long k = keys[i];
            int cnt = inlineCount[i];
            int i0 = inline0[i], i1 = inline1[i], i2 = inline2[i], i3 = inline3[i];
            int ovh = overflowHead[i];
            double minx = bMinX[i], miny = bMinY[i], minz = bMinZ[i];
            double maxx = bMaxX[i], maxy = bMaxY[i], maxz = bMaxZ[i];
            boolean dirty = aabbDirty[i];

            keys[i] = EMPTY_KEY;
            inlineCount[i] = 0;
            inline0[i] = inline1[i] = inline2[i] = inline3[i] = -1;
            overflowHead[i] = -1;
            bMinX[i] = bMinY[i] = bMinZ[i] = Double.POSITIVE_INFINITY;
            bMaxX[i] = bMaxY[i] = bMaxZ[i] = Double.NEGATIVE_INFINITY;
            aabbDirty[i] = false;
            usedBuckets--;

            int j = idealIndex(k);
            while (keys[j] != EMPTY_KEY) j = (j + 1) & tableMask;
            keys[j] = k;
            inlineCount[j] = cnt;
            inline0[j] = i0; inline1[j] = i1; inline2[j] = i2; inline3[j] = i3;
            overflowHead[j] = ovh;
            bMinX[j] = minx; bMinY[j] = miny; bMinZ[j] = minz;
            bMaxX[j] = maxx; bMaxY[j] = maxy; bMaxZ[j] = maxz;
            aabbDirty[j] = dirty;
            usedBuckets++;

            i = (i + 1) & tableMask;
        }
    }

    // ---------------- slot helpers ----------------

    private void ensureSlotCapacity() {
        if (everAllocatedSlotCount < capacity) return;
        int ncap = capacity + (capacity >> 1);
        double[] nx = new double[ncap], ny = new double[ncap], nz = new double[ncap];
        long[] nc = new long[ncap];
        int[] fn = new int[ncap];
        int[] sn = new int[ncap]; // slotNext copy

        System.arraycopy(xs, 0, nx, 0, capacity);
        System.arraycopy(ys, 0, ny, 0, capacity);
        System.arraycopy(zs, 0, nz, 0, capacity);
        System.arraycopy(slotPackedCell, 0, nc, 0, capacity);
        System.arraycopy(slotNext, 0, sn, 0, capacity);
        System.arraycopy(freeNext, 0, fn, 0, capacity);

        for (int i = capacity; i < ncap; i++) {
            nc[i] = SLOT_EMPTY;
            fn[i] = -1;
            sn[i] = -1;
        }

        xs = nx; ys = ny; zs = nz;
        slotPackedCell = nc;
        slotNext = sn;
        freeNext = fn;
        // if you keep slotNext alias nn, ensure consistency; here nn was unused so removed
        capacity = ncap;
    }

    // ---------------- public API ----------------

    @Override
    public int add(double x, double y, double z, int fx, int fy, int fz) {
        liveCount++;
        int slot;
        // when reusing a freed slot
        if (freeHead != -1) {
            slot = freeHead;
            freeHead = freeNext[slot];
            freeNext[slot] = -1;
            slotNext[slot] = -1; // <--- ensure initialized
        } else {
            ensureSlotCapacity();
            slot = everAllocatedSlotCount++;
            slotNext[slot] = -1; // <--- ensure initialized for newly allocated slot
        }
        xs[slot] = x;
        ys[slot] = y;
        zs[slot] = z;
        slotNext[slot] = -1;
        int cx = fx >> shiftXZ;
        int cy = fy >> shiftY;
        int cz = fz >> shiftXZ;
        long packed = packCoords(cx, cy, cz);
        slotPackedCell[slot] = packed;
        insertKeyWithSlot(packed, slot);
        return slot;
    }

    @Override
    public void remove(int slot) {
        liveCount--;
        long packed = slotPackedCell[slot];
        if (packed == SLOT_EMPTY) return;
        int idx = findIndex(packed);
        if (idx == -1) {
            // mark removed and push to free list anyway
            slotPackedCell[slot] = SLOT_EMPTY;
            freeNext[slot] = freeHead;
            freeHead = slot;
            return;
        }
        boolean removed = removeInlineSlotAtBucket(idx, slot);
        if (!removed) {
            int head = overflowHead[idx];
            int newHead = removeOverflowValue(head, slot);
            if (newHead != head) {
                overflowHead[idx] = newHead;
                removed = true;
            }
        }
        // mark slot as removed and add to free list
        slotPackedCell[slot] = SLOT_EMPTY;
        freeNext[slot] = freeHead;
        freeHead = slot;
        if (!removed) return;
        double sx = xs[slot], sy = ys[slot], sz = zs[slot];
        if (sx == bMinX[idx] || sx == bMaxX[idx] || sy == bMinY[idx] || sy == bMaxY[idx] || sz == bMinZ[idx] || sz == bMaxZ[idx]) {
            aabbDirty[idx] = true;
        }
        if (inlineCount[idx] == 0 && overflowHead[idx] == -1) {
            deleteBucketAt(idx);
        }
    }

    @Override
    public void move(int slot, double newX, double newY, double newZ, int fx, int fy, int fz) {
        int ncx = fx >> shiftXZ;
        int ncy = fy >> shiftY;
        int ncz = fz >> shiftXZ;
        long newPacked = packCoords(ncx, ncy, ncz);
        long oldPacked = slotPackedCell[slot];
        int oldIdx = findIndex(oldPacked);
        if (oldPacked == newPacked) {
            double oldx = xs[slot], oldy = ys[slot], oldz = zs[slot];
            xs[slot] = newX; ys[slot] = newY; zs[slot] = newZ;
            if (oldIdx != -1) {
                if (!aabbDirty[oldIdx]) {
                    if (oldx == bMinX[oldIdx] || oldx == bMaxX[oldIdx] ||
                        oldy == bMinY[oldIdx] || oldy == bMaxY[oldIdx] ||
                        oldz == bMinZ[oldIdx] || oldz == bMaxZ[oldIdx]) {
                        aabbDirty[oldIdx] = true;
                    } else {
                        if (newX < bMinX[oldIdx]) {
                            bMinX[oldIdx] = newX;
                        } else if (newX > bMaxX[oldIdx]) {
                            bMaxX[oldIdx] = newX;
                        }
                        if (newY < bMinY[oldIdx]) {
                            bMinY[oldIdx] = newY;
                        } else if (newY > bMaxY[oldIdx]) {
                            bMaxY[oldIdx] = newY;
                        }
                        if (newZ < bMinZ[oldIdx]) {
                            bMinZ[oldIdx] = newZ;
                        } else if (newZ > bMaxZ[oldIdx]) {
                            bMaxZ[oldIdx] = newZ;
                        }
                    }
                }
            }
            return;
        }
        if (oldPacked != SLOT_EMPTY) {
            if (oldIdx != -1) {
                boolean removed = removeInlineSlotAtBucket(oldIdx, slot);
                if (!removed) {
                    int head = overflowHead[oldIdx];
                    int newHead = removeOverflowValue(head, slot);
                    if (newHead != head) {
                        overflowHead[oldIdx] = newHead;
                        removed = true;
                    }
                }
                if (removed) {
                    double sx = xs[slot], sy = ys[slot], sz = zs[slot];
                    if (!aabbDirty[oldIdx]) {
                        if (sx == bMinX[oldIdx] || sx == bMaxX[oldIdx] || sy == bMinY[oldIdx] || sy == bMaxY[oldIdx] || sz == bMinZ[oldIdx] || sz == bMaxZ[oldIdx]) {
                            aabbDirty[oldIdx] = true;
                        }
                    }
                    if (inlineCount[oldIdx] == 0 && overflowHead[oldIdx] == -1) {
                        deleteBucketAt(oldIdx);
                    }
                }
            }
        }
        xs[slot] = newX; ys[slot] = newY; zs[slot] = newZ;
        slotPackedCell[slot] = newPacked;
        insertKeyWithSlot(newPacked, slot);
    }

    // ---------------- AABB recompute ----------------

    protected void recomputeAABB(int idx) {
        double minx = Double.POSITIVE_INFINITY, miny = Double.POSITIVE_INFINITY, minz = Double.POSITIVE_INFINITY;
        double maxx = Double.NEGATIVE_INFINITY, maxy = Double.NEGATIVE_INFINITY, maxz = Double.NEGATIVE_INFINITY;
        int cnt = inlineCount[idx];
        if (cnt > 0) {
            if (inline0[idx] != -1) {
                double sx = xs[inline0[idx]], sy = ys[inline0[idx]], sz = zs[inline0[idx]];
                if (sx < minx) minx = sx; if (sx > maxx) maxx = sx;
                if (sy < miny) miny = sy; if (sy > maxy) maxy = sy;
                if (sz < minz) minz = sz; if (sz > maxz) maxz = sz;
            }
            if (cnt >= 2 && inline1[idx] != -1) {
                double sx = xs[inline1[idx]], sy = ys[inline1[idx]], sz = zs[inline1[idx]];
                if (sx < minx) minx = sx; if (sx > maxx) maxx = sx;
                if (sy < miny) miny = sy; if (sy > maxy) maxy = sy;
                if (sz < minz) minz = sz; if (sz > maxz) maxz = sz;
            }
            if (cnt >= 3 && inline2[idx] != -1) {
                double sx = xs[inline2[idx]], sy = ys[inline2[idx]], sz = zs[inline2[idx]];
                if (sx < minx) minx = sx; if (sx > maxx) maxx = sx;
                if (sy < miny) miny = sy; if (sy > maxy) maxy = sy;
                if (sz < minz) minz = sz; if (sz > maxz) maxz = sz;
            }
            if (cnt >= 4 && inline3[idx] != -1) {
                double sx = xs[inline3[idx]], sy = ys[inline3[idx]], sz = zs[inline3[idx]];
                if (sx < minx) minx = sx; if (sx > maxx) maxx = sx;
                if (sy < miny) miny = sy; if (sy > maxy) maxy = sy;
                if (sz < minz) minz = sz; if (sz > maxz) maxz = sz;
            }
        }
        int h = overflowHead[idx];
        while (h != -1) {
            int s = ovSlot[h];
            double sx = xs[s], sy = ys[s], sz = zs[s];
            if (sx < minx) minx = sx; if (sx > maxx) maxx = sx;
            if (sy < miny) miny = sy; if (sy > maxy) maxy = sy;
            if (sz < minz) minz = sz; if (sz > maxz) maxz = sz;
            h = ovNext[h];
        }
        if (minx == Double.POSITIVE_INFINITY) {
            bMinX[idx] = bMinY[idx] = bMinZ[idx] = Double.POSITIVE_INFINITY;
            bMaxX[idx] = bMaxY[idx] = bMaxZ[idx] = Double.NEGATIVE_INFINITY;
        } else {
            bMinX[idx] = minx; bMinY[idx] = miny; bMinZ[idx] = minz;
            bMaxX[idx] = maxx; bMaxY[idx] = maxy; bMaxZ[idx] = maxz;
        }
        aabbDirty[idx] = false;
    }

    // ---------------- AABB helpers ----------------

    protected double dist2SlotToCenter(int s, double cxD, double cyD, double czD) {
        double dx = xs[s] - cxD;
        double dy = ys[s] - cyD;
        double dz = zs[s] - czD;
        return dx * dx + dy * dy + dz * dz;
    }
    protected double dist2SlotPlanar(int s, double cxD, double czD) {
        double dx = xs[s] - cxD;
        double dz = zs[s] - czD;
        return dx * dx + dz * dz;
    }

    // ---------------- bucket lookup ----------------

    protected int bucketIndexForCellCoords(int cx, int cy, int cz) {
        long packed = packCoords(cx, cy, cz);
        int idx = idealIndex(packed);
        int tableSize = tableMask + 1;
        int dist = 0;
        while (true) {
            long k = keys[idx];
            if (k == EMPTY_KEY) return -1;
            if (k == packed) return idx;
            idx = (idx + 1) & tableMask;
            dist++;
            if (dist > tableSize) return -1;
        }
    }

    // ---------------- accessors ----------------

    @Override
    public int size() { return liveCount; }
    @Override
    public int getCellSizeXZ() { return cellSizeXZ; }
    @Override
    public int getCellSizeY() { return cellSizeY; }
    public double getW() { return W; }
    public double getH() { return H; }
    @Override
    public double getX(int slot) { return xs[slot]; }
    @Override
    public double getY(int slot) { return ys[slot]; }
    @Override
    public double getZ(int slot) { return zs[slot]; }
    @Override
    public boolean containsKey(int slot) { return slot < this.slotPackedCell.length && this.slotPackedCell[slot] != SLOT_EMPTY; }

}
