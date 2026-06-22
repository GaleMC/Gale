package org.galemc.gale.registry.valuebasedmap;

import net.minecraft.core.IdMapper;

/**
 * A class that is aware of its {@linkplain IdMapper#getId index} in a registry.
 */
public interface RegistryIdAware {

    int getIdInRegistry();

    void setIdInRegistry(int index);

}
