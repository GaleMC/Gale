package org.galemc.gale.registry.valuebasedmap;

/**
 * A base implementation of {@link RegistryIdAware}
 * that holds the id in a field.
 */
public class FieldRegistryIdAware implements RegistryIdAware {

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
