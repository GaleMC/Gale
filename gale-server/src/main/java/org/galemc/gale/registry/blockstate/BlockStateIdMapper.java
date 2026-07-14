package org.galemc.gale.registry.blockstate;

import net.minecraft.core.IdMap;
import net.minecraft.core.IdMapper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A specialized {@link IdMap} for {@link Block#BLOCK_STATE_REGISTRY}.
 *
 * <p>
 * The implementation is based on {@link IdMapper}.
 * </p>
 *
 * <p>
 * Uses a direct array instead of {@link java.util.ArrayList} to avoid
 * redundant bounds checks and virtual dispatch on the hot
 * {@link #byId(int)} path.
 * </p>
 */
public class BlockStateIdMapper implements IdMap<BlockState> {

    public static final int EXPECTED_BLOCK_STATES = 32366; // As of 26.2, TODO keep up-to-date

    private BlockState[] idToT;
    private int size;

    public BlockStateIdMapper() {
        this.idToT = new BlockState[EXPECTED_BLOCK_STATES];
    }

    public void add(final BlockState thing) {
        if (this.size >= this.idToT.length) {
            this.idToT = Arrays.copyOf(this.idToT, this.idToT.length + (this.idToT.length >> 1));
        }
        thing.indexInRegistry = this.size;
        this.idToT[this.size++] = thing;
    }

    @Override
    public int getId(final BlockState thing) {
        return thing.indexInRegistry;
    }

    @Override
    public final @Nullable BlockState byId(final int id) {
        return id >= 0 && id < this.size ? this.idToT[id] : null;
    }

    @Override
    public Iterator<BlockState> iterator() {
        return new Iterator<>() {

            private int cursor;

            @Override
            public boolean hasNext() {
                return this.cursor < BlockStateIdMapper.this.size;
            }

            @Override
            public BlockState next() {
                if (this.cursor >= BlockStateIdMapper.this.size) {
                    throw new NoSuchElementException();
                }
                return BlockStateIdMapper.this.idToT[this.cursor++];
            }

        };
    }

    @Override
    public int size() {
        return this.size;
    }

    public void trimToSize() {
        if (this.idToT.length != this.size) {
            this.idToT = Arrays.copyOf(this.idToT, this.size);
        }
    }

}
