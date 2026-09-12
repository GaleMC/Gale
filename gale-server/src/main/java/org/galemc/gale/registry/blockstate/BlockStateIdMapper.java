package org.galemc.gale.registry.blockstate;

import net.minecraft.core.IdMapper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A specialized {@link IdMapper} for {@link Block#BLOCK_STATE_REGISTRY}.
 *
 * <p>
 * The field keeps the {@link IdMapper} type for plugin binary compatibility
 * (plugins such as FAWE access {@code Block.BLOCK_STATE_REGISTRY} directly).
 * </p>
 *
 * <p>
 * Writes go to both the vanilla map/list and a direct array, so inherited
 * methods stay correct while the hot paths - {@link #getId(Object)} via the
 * intrusive {@link BlockState#indexInRegistry} and {@link #byId(int)} via the
 * array - avoid map lookups and bounds-checked list access.
 * </p>
 */
public class BlockStateIdMapper extends IdMapper<BlockState> {

    public static final int EXPECTED_BLOCK_STATES = 32366; // As of 26.2, TODO keep up-to-date

    private BlockState[] idToT;
    private int size;

    public BlockStateIdMapper() {
        super();
        this.idToT = new BlockState[EXPECTED_BLOCK_STATES];
    }

    @Override
    public void add(final BlockState state) {
        super.add(state);
        this.set(state, super.getId(state));
    }

    @Override
    public void addMapping(final BlockState state, final int id) {
        super.addMapping(state, id);
        this.set(state, id);
    }

    private void set(final BlockState state, final int id) {
        if (id >= this.idToT.length) {
            this.idToT = Arrays.copyOf(this.idToT, Math.max(id + 1, this.idToT.length + (this.idToT.length >> 1)));
        }
        state.indexInRegistry = id;
        this.idToT[id] = state;
        if (id >= this.size) {
            this.size = id + 1;
        }
    }

    @Override
    public int getId(final BlockState state) {
        return state.indexInRegistry;
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
