package org.galemc.gale.configuration;

import io.papermc.paper.configuration.Configuration;
import io.papermc.paper.configuration.ConfigurationPart;
import org.spongepowered.configurate.objectmapping.meta.Comment;
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

    public Performance performance = new Performance();

    public static class Performance extends ConfigurationPart {

        @Comment("Enables parallel ticking of different worlds (Overworld, Nether, End, etc.) on separate CPU threads.\n"
            + "This can dramatically improve TPS on servers with multiple worlds or many dimensions by utilizing\n"
            + "additional CPU cores that would otherwise be idle. Plugin compatibility is 100% because each\n"
            + "world's tick still runs on its own dedicated \"main-like\" thread.\n"
            + "Requires at least 2 worlds loaded to have any effect.\n"
            + "Default: false (conservative - enable to benefit from multi-core CPUs)")
        @Setting("parallel-world-ticking")
        public boolean parallelWorldTicking = false;

        @Comment("Maximum number of threads to use for parallel world ticking. The actual value is also capped by\n"
            + "(available CPU cores - 1) and the number of loaded worlds. Values above 16 are ignored to prevent\n"
            + "excessive thread overhead.\n"
            + "Default: 8 (good for modern 8c/16t+ CPUs)")
        @Setting("parallel-world-ticking-max-threads")
        public int parallelWorldTickingMaxThreads = 8;
    }
}
