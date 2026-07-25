package org.galemc.gale.util.collection.palette;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.world.level.chunk.MissingPaletteEntryException;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PaletteResize;

import static it.unimi.dsi.fastutil.Hash.FAST_LOAD_FACTOR;

// Gale - Chunk serialization - Based on net.caffeinemc.mods.lithium.common.world.chunk.LithiumHashPalette.
// Gale - Chunk serialization - Modified to fit the vanilla Palette contract and chunk serialization flow.
public final class SerializationPalette<T> implements Palette<T> {
    private static final int ABSENT_VALUE = -1;

    private final int bits;
    private final Reference2IntMap<T> values;
    private T[] entries;
    private int size;

    @SuppressWarnings("unchecked")
    public SerializationPalette(final int bits) {
        this.bits = bits;
        int capacity = Math.max(1, 1 << bits);
        this.entries = (T[]) new Object[capacity];
        this.values = new Reference2IntOpenHashMap<>(capacity, FAST_LOAD_FACTOR);
        this.values.defaultReturnValue(ABSENT_VALUE);
    }

    public SerializationPalette(final int bits, final List<T> paletteEntries) {
        this(bits);
        for (final T paletteEntry : paletteEntries) {
            this.addEntry(paletteEntry);
        }
    }

    @Override
    public int idFor(final T value, final PaletteResize<T> resizeHandler) {
        int id = this.values.getInt(value);
        if (id != ABSENT_VALUE) {
            return id;
        }

        if (this.size >= 1 << this.bits) {
            return resizeHandler.onResize(this.bits + 1, value);
        }

        return this.addEntry(value);
    }

    @Override
    public boolean maybeHas(final Predicate<T> predicate) {
        for (int i = 0; i < this.size; ++i) {
            if (predicate.test(this.entries[i])) {
                return true;
            }
        }

        return false;
    }

    @Override
    public T valueFor(final int index) {
        if (index >= 0 && index < this.size) {
            final T entry = this.entries[index];
            if (entry != null) {
                return entry;
            }
        }

        throw new MissingPaletteEntryException(index);
    }

    @Override
    public void read(final FriendlyByteBuf buffer, final IdMap<T> globalMap) {
        Arrays.fill(this.entries, 0, this.size, null);
        this.values.clear();
        this.size = 0;

        final int paletteSize = buffer.readVarInt();
        for (int i = 0; i < paletteSize; ++i) {
            this.addEntry(globalMap.byIdOrThrow(buffer.readVarInt()));
        }
    }

    @Override
    public void write(final FriendlyByteBuf buffer, final IdMap<T> globalMap) {
        buffer.writeVarInt(this.size);
        for (int i = 0; i < this.size; ++i) {
            buffer.writeVarInt(globalMap.getId(this.entries[i]));
        }
    }

    @Override
    public int getSerializedSize(final IdMap<T> globalMap) {
        int serializedSize = VarInt.getByteSize(this.size);
        for (int i = 0; i < this.size; ++i) {
            serializedSize += VarInt.getByteSize(globalMap.getId(this.entries[i]));
        }
        return serializedSize;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public Palette<T> copy() {
        final SerializationPalette<T> copy = new SerializationPalette<>(this.bits);
        copy.entries = this.entries.clone();
        copy.values.putAll(this.values);
        copy.size = this.size;
        return copy;
    }

    public List<T> getEntries() {
        final ArrayList<T> paletteEntries = new ArrayList<>(this.size);
        for (int i = 0; i < this.size; ++i) {
            paletteEntries.add(this.entries[i]);
        }
        return paletteEntries;
    }

    private int addEntry(final T value) {
        final int nextId = this.size;
        if (nextId >= this.entries.length) {
            this.entries = Arrays.copyOf(this.entries, HashCommon.nextPowerOfTwo(nextId + 1));
        }

        this.values.put(value, nextId);
        this.entries[nextId] = value;
        this.size = nextId + 1;
        return nextId;
    }
}
