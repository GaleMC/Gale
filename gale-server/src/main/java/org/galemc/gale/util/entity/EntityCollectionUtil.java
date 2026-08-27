package org.galemc.gale.util.entity;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.Entity;

public final class EntityCollectionUtil {

    private static final int EXPECTED_ENTITIES = 64;

    private static final List<Entity> ENTITY_LIST = new ArrayList<>(EXPECTED_ENTITIES);

    private EntityCollectionUtil() {}

    public static List<Entity> getEntityList() {
        ENTITY_LIST.clear();
        return ENTITY_LIST;
    }
}
