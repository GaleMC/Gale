package org.galemc.gale.configuration;

import io.papermc.paper.configuration.Configuration;
import io.papermc.paper.configuration.ConfigurationPart;
import org.spongepowered.configurate.objectmapping.meta.Setting;

public class GaleGlobalConfiguration extends ConfigurationPart {
    static final int CURRENT_VERSION = 1;
    private static GaleGlobalConfiguration instance;
    public static GaleGlobalConfiguration get() {
        return instance;
    }
    static void set(GaleGlobalConfiguration instance) {
        GaleGlobalConfiguration.instance = instance;
    }

    @Setting(Configuration.VERSION_FIELD)
    public int version = CURRENT_VERSION;
}
