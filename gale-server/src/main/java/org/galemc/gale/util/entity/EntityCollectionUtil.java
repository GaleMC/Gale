package org.galemc.gale.util.entity;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class EntityCollectionUtil {

    private static final int EXPECTED_ENTITIES = 64;
    private static final int EXPECTED_COLLISIONS = 32;

    private static final ThreadLocal<List<Entity>> ENTITY_LIST = ThreadLocal.withInitial(() -> new ArrayList<>(EXPECTED_ENTITIES));
    private static final ThreadLocal<List<VoxelShape>> VOXEL_SHAPE_LIST = ThreadLocal.withInitial(() -> new ArrayList<>(EXPECTED_COLLISIONS));
    private static final ThreadLocal<List<AABB>> AABB_LIST = ThreadLocal.withInitial(() -> new ArrayList<>(EXPECTED_COLLISIONS));

    private EntityCollectionUtil() {}

    public static List<Entity> getEntityList() {
        List<Entity> list = ENTITY_LIST.get();
        list.clear();
        return list;
    }

    public static List<VoxelShape> getVoxelShapeList() {
        List<VoxelShape> list = VOXEL_SHAPE_LIST.get();
        list.clear();
        return list;
    }

    public static List<AABB> getAABBList() {
        List<AABB> list = AABB_LIST.get();
        list.clear();
        return list;
    }
}
