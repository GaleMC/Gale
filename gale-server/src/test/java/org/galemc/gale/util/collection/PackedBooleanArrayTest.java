package org.galemc.gale.util.collection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PackedBooleanArrayTest {

    @Test
    void setKeepsExistingBitsAcrossGrowth() {
        PackedBooleanArray array = PackedBooleanArray.createEmpty();

        array.set(0, 1L << 1);
        int initialLength = array.array.length;

        array.set(4, 1L << 5);

        assertTrue(array.array.length > initialLength);
        assertTrue(array.array.length >= 5);
        assertTrue(array.get(0, 1L << 1));
        assertTrue(array.get(4, 1L << 5));
    }

    @Test
    void setDoesNotGrowAgainInsideReservedCapacity() {
        PackedBooleanArray array = PackedBooleanArray.createEmpty();

        array.set(0, 1L);
        int reservedLength = array.array.length;

        for (int i = 1; i < reservedLength; i++) {
            array.set(i, 1L << (i & 63));
        }

        assertEquals(reservedLength, array.array.length);
        for (int i = 0; i < reservedLength; i++) {
            assertTrue(array.get(i, 1L << (i & 63)));
        }
    }

    @Test
    void clearZeroesAllWords() {
        PackedBooleanArray array = PackedBooleanArray.createEmpty();

        array.set(2, 0b1010L);
        array.set(5, 0b0101L);
        int length = array.array.length;
        array.clear();

        assertEquals(length, array.array.length);
        assertFalse(array.get(2, 0b1010L));
        assertFalse(array.get(5, 0b0101L));
    }
}
