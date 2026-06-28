package org.galemc.gale.util.array;

import net.minecraft.world.entity.EquipmentSlot;

public final class EmptyArrays {

    private EmptyArrays() {
        throw new UnsupportedOperationException();
    }

    public static final long[] LONG = new long[0];

    public static final EquipmentSlot[] EQUIPMENT_SLOT = new EquipmentSlot[0];

}
