package org.galemc.gale.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class CollisionShapeCache {

    private static volatile VoxelShape[] cache;

    public static VoxelShape getCachedCollisionShape(BlockState state) {
        VoxelShape[] arr = cache;
        if (arr == null) {
            return null;
        }
        int index = state.indexInRegistry;
        if (index < 0 || index >= arr.length) {
            return null;
        }
        return arr[index];
    }

    public static void setCachedCollisionShape(BlockState state, VoxelShape shape) {
        VoxelShape[] arr = cache;
        if (arr == null) {
            synchronized (CollisionShapeCache.class) {
                arr = cache;
                if (arr == null) {
                    int size = Block.BLOCK_STATE_REGISTRY.size();
                    cache = arr = new VoxelShape[size];
                }
            }
        }
        int index = state.indexInRegistry;
        if (index >= 0 && index < arr.length) {
            arr[index] = shape;
        }
    }
}