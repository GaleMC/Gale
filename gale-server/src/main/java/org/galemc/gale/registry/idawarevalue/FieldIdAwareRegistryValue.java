package org.galemc.gale.registry.idawarevalue;

/**
 * A base implementation of {@link IdAwareRegistryValue}
 * that holds the id in a field.
 */
public class FieldIdAwareRegistryValue implements IdAwareRegistryValue {

    public int idInRegistry;

    @Override
    public int getIdInRegistry() {
        return this.idInRegistry;
    }

    @Override
    public void setIdInRegistry(int index) {
        this.idInRegistry = index;
    }

    @Override
    public int hashCode() {
        return this.idInRegistry;
    }

}
