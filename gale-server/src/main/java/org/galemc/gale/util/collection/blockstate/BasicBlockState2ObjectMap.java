package org.galemc.gale.util.collection.blockstate;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * An intentionally incomplete (for performance)
 * {@link Map} implementation where the keys are {@link BlockState}s.
 */
public class BasicBlockState2ObjectMap<V> implements Map<BlockState, V> {

    private @Nullable Object @Nullable [] values;

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsKey(final Object key) { // Relies on caller only to call this after at least one put() call
        return this.values[((BlockState) key).indexInRegistry] != null;
    }

    @Override
    public boolean containsValue(final Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V get(final Object key) { // Relies on caller only to call this after at least one put() call
        return (V) this.values[((BlockState) key).indexInRegistry];
    }

    @Override
    public @Nullable V put(final BlockState key, final V value) {
        if (this.values == null) {
            this.values = new Object[Block.BLOCK_STATE_REGISTRY.size()];
        }
        V oldValue = (V) this.values[key.indexInRegistry];
        this.values[key.indexInRegistry] = value;
        return oldValue;
    }

    @Override
    public V remove(final Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(@NonNull final Map<? extends BlockState, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NonNull Set<BlockState> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NonNull Collection<V> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NonNull Set<Entry<BlockState, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

}
