package org.galemc.gale.registry.blockstate;

import net.minecraft.core.IdMap;
import net.minecraft.core.IdMapper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A specialized {@link IdMap} for {@link Block#BLOCK_STATE_REGISTRY}.
 *
 * <p>
 * The implementation is based on {@link IdMapper}.
 * </p>
 */
public class BlockStateIdMapper implements IdMap<BlockState> {

    public static final int EXPECTED_BLOCK_STATES = 32366; // As of 26.2, TODO keep up-to-date

    private final ArrayList<BlockState> idToT;

    public BlockStateIdMapper() {
        this.idToT = new ArrayList<>(EXPECTED_BLOCK_STATES * 4 / 3); // Increase size in case we forget to update EXPECTED_BLOCK_STATES
    }

    public void add(final BlockState thing) {
        thing.indexInRegistry = this.idToT.size();
        this.idToT.add(thing);
    }

    @Override
    public int getId(final BlockState thing) {
        return thing.indexInRegistry;
    }

    @Override
    public final @Nullable BlockState byId(final int id) {
        return id >= 0 && id < this.idToT.size() ? this.idToT.get(id) : null;
    }

    @Override
    public Iterator<BlockState> iterator() {
        return this.idToT.iterator();
    }

    @Override
    public int size() {
        return this.idToT.size();
    }

    public void trimToSize() {
        this.idToT.trimToSize();
    }

}
