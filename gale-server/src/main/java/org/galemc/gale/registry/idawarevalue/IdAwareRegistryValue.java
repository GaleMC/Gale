package org.galemc.gale.registry.idawarevalue;

import net.minecraft.core.IdMapper;

/**
 * A class that is aware of its {@linkplain IdMapper#getId index} in a registry.
 */
public interface IdAwareRegistryValue {

    int getIdInRegistry();

    void setIdInRegistry(int index);

}
