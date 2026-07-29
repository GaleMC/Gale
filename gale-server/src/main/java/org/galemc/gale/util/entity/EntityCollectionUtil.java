package org.galemc.gale.util.entity;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class EntityCollectionUtil {

    private static final int EXPECTED_ENTITIES = 64;
    private static final int EXPECTED_COLLISIONS = 32;

    private static final List<Entity> ENTITY_LIST = new ArrayList<>(EXPECTED_ENTITIES);
    private static final List<VoxelShape> VOXEL_SHAPE_LIST = new ArrayList<>(EXPECTED_COLLISIONS);
    private static final List<AABB> AABB_LIST = new ArrayList<>(EXPECTED_COLLISIONS);

    private EntityCollectionUtil() {}

    public static List<Entity> getEntityList() {
        ENTITY_LIST.clear();
        return ENTITY_LIST;
    }

    public static List<VoxelShape> getVoxelShapeList() {
        VOXEL_SHAPE_LIST.clear();
        return VOXEL_SHAPE_LIST;
    }

    public static List<AABB> getAABBList() {
        AABB_LIST.clear();
        return AABB_LIST;
    }
}
