package org.galemc.gale.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class CollisionShapeCache {

    private static VoxelShape[] cache = new VoxelShape[0];

    public static VoxelShape getCachedCollisionShape(BlockState state) {
        int index = state.indexInRegistry;
        return index >= 0 && index < cache.length ? cache[index] : null;
    }

    public static void setCachedCollisionShape(BlockState state, VoxelShape shape) {
        VoxelShape[] arr = cache;
        if (arr.length == 0) {
            synchronized (CollisionShapeCache.class) {
                arr = cache;
                if (arr.length == 0) {
                    int size = Block.BLOCK_STATE_REGISTRY.size();
                    arr = new VoxelShape[size];
                    cache = arr;
                }
            }
        }
        int index = state.indexInRegistry;
        if (index >= 0 && index < arr.length) {
            arr[index] = shape;
        }
    }
}