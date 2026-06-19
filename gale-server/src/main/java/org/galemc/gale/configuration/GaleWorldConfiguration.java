package org.galemc.gale.configuration;

import io.papermc.paper.configuration.Configuration;
import io.papermc.paper.configuration.ConfigurationPart;
import net.minecraft.resources.Identifier;
import org.spigotmc.SpigotWorldConfig;
import org.spongepowered.configurate.objectmapping.meta.Setting;

public class GaleWorldConfiguration extends ConfigurationPart {
    static final int CURRENT_VERSION = 1;

    private transient final SpigotWorldConfig spigotConfig;
    private transient final Identifier worldKey;

    public GaleWorldConfiguration(SpigotWorldConfig spigotConfig, Identifier worldKey) {
        this.spigotConfig = spigotConfig;
        this.worldKey = worldKey;
    }

    @Setting(Configuration.VERSION_FIELD)
    public int version = CURRENT_VERSION;
}
