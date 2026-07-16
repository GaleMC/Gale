package org.galemc.gale.util.collection.packed;

import java.util.Arrays;
import org.galemc.gale.util.array.EmptyArrays;

public class PackedBooleanArray {

    private static final int MIN_GROWTH_CAPACITY = 4;

    public long[] array;

    public PackedBooleanArray(long[] array) {
        this.array = array;
    }

    public boolean get(int wordIndex, long wordMask) {
        return wordIndex < this.array.length && (this.array[wordIndex] & wordMask) != 0;
    }

    public void set(int wordIndex, long wordMask) {
        if (wordIndex >= this.array.length) {
            this.grow(wordIndex + 1);
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
        long[] a = this.array;
        if (a.length > 0) {
            Arrays.fill(a, 0L);
        }
    }

    private void grow(int minLength) {
        int newLength = Math.max(MIN_GROWTH_CAPACITY, this.array.length);
        while (newLength < minLength) {
            int nextLength = newLength + (newLength >> 1);
            if (nextLength <= newLength) {
                newLength = minLength;
                break;
            }
            newLength = nextLength;
        }
        this.array = Arrays.copyOf(this.array, newLength);
    }

    public static PackedBooleanArray createEmpty() {
        return new PackedBooleanArray(EmptyArrays.LONG);
    }

    public static PackedBooleanArray createEmpty(int minCapacity) {
        return new PackedBooleanArray(new long[(minCapacity + 63) >> 6]);
    }

    public static PackedBooleanArray createWithInitial(int wordIndex, long wordMask) {
        long[] array = new long[wordIndex + 1];
        array[wordIndex] = wordMask;
        return new PackedBooleanArray(array);
    }

    public static int getWordIndex(int index) {
        return index >> 6;
    }

    public static long getWordMask(int index) {
        return 1L << index;
    }

}
