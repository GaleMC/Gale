package org.galemc.gale.util.collection;

import org.galemc.gale.util.array.EmptyArrays;

public class PackedBooleanArray {

    public long[] array;

    public PackedBooleanArray(long[] array) {
        this.array = array;
    }

    public boolean get(int wordIndex, long wordMask) {
        return wordIndex < this.array.length && (this.array[wordIndex] & wordMask) != 0;
    }

    public void set(int wordIndex, long wordMask) {
        if (wordIndex >= this.array.length) {
            this.array = java.util.Arrays.copyOf(this.array, wordIndex + 1);
        }
        this.array[wordIndex] |= wordMask;
    }

    public void unset(int wordIndex, long wordMask) {
        if (wordIndex < this.array.length) {
            this.unsetUnsafe(wordIndex, wordMask);
        }
    }

    public void unsetUnsafe(int wordIndex, long wordMask) {
        this.array[wordIndex] &= ~wordMask;
    }

    public void clear() {
        this.array = EmptyArrays.LONG;
    }

    public static PackedBooleanArray createEmpty() {
        return new PackedBooleanArray(EmptyArrays.LONG);
    }

    public static PackedBooleanArray createWithInitial(int wordIndex, long wordMask) {
        long[] array = new long[wordIndex + 1];
        array[wordIndex] = wordMask;
        return new PackedBooleanArray(array);
    }

}
