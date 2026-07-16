package org.galemc.gale.util.collection.packed;

import java.util.Arrays;

/**
 * Tracks used indices for a specific use case of {@link PackedBooleanArray}s.
 */
public class UsedPackedIndices {

    private boolean[] used;
    private int smallestPotentiallyAvailable;

    public UsedPackedIndices() {
        this(512);
    }

    public UsedPackedIndices(int initialCapacity) {
        this.used = new boolean[initialCapacity];
    }

    public int claimUnused() {
        while (true) {
            int index = this.smallestPotentiallyAvailable++;
            int length = this.used.length;
            if (index >= length) {
                this.used = Arrays.copyOf(this.used, Math.max(1, length << 1));
                this.used[index] = true;
                return index;
            } else if (!this.used[index]) {
                this.used[index] = true;
                return index;
            }
        }
    }

    public void freeUsed(int index) {
        this.used[index] = false;
        if (index < this.smallestPotentiallyAvailable) {
            this.smallestPotentiallyAvailable = index;
        }
    }

}
