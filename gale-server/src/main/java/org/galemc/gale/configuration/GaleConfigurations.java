package org.galemc.gale.configuration;

import com.mojang.logging.LogUtils;
import io.papermc.paper.configuration.Configuration;
import io.papermc.paper.configuration.ConfigurationPart;
import io.papermc.paper.configuration.Configurations;
import io.papermc.paper.configuration.PaperConfigurations;
import io.papermc.paper.configuration.mapping.Definition;
import io.papermc.paper.configuration.mapping.FieldProcessor;
import io.papermc.paper.configuration.mapping.InnerClassFieldDiscoverer;
import java.lang.annotation.Annotation;
import io.papermc.paper.configuration.serializer.ComponentSerializer;
import io.papermc.paper.configuration.serializer.EnumValueSerializer;
import io.papermc.paper.configuration.serializer.collection.map.MapSerializer;
import io.papermc.paper.configuration.serializer.ServerboundPacketClassSerializer;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.spigotmc.SpigotWorldConfig;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;
import org.spongepowered.configurate.transformation.TransformAction;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import static io.leangen.geantyref.GenericTypeReflector.erase;

public class GaleConfigurations extends Configurations<GaleGlobalConfiguration, GaleWorldConfiguration> {

    private static final Logger LOGGER = LogUtils.getClassLogger();
    static final String GLOBAL_CONFIG_FILE_NAME = "gale-global.yml";
    static final String WORLD_DEFAULTS_CONFIG_FILE_NAME = "gale-world-defaults.yml";
    static final String WORLD_CONFIG_FILE_NAME = "gale-world.yml";
    public static final String CONFIG_DIR = "config";

    private static final String GLOBAL_HEADER = String.format("""
            This is the global configuration file for Gale.
            As you can see, there's a lot to configure. Some options may impact gameplay, so use
            with caution, and make sure you know what each option does before configuring.

            If you need help with the configuration or have any questions related to Gale,
            join us in our Discord, or check the GitHub Wiki pages.

            The world configuration options are inside
            their respective world folder. The files are named %s

            Wiki: https://github.com/GaleMC/Gale/wiki
            Discord: https://discord.gg/gwezNT8c24""", WORLD_CONFIG_FILE_NAME);

    private static final String WORLD_DEFAULTS_HEADER = """
            This is the world defaults configuration file for Gale.
            As you can see, there's a lot to configure. Some options may impact gameplay, so use
            with caution, and make sure you know what each option does before configuring.

            If you need help with the configuration or have any questions related to Gale,
            join us in our Discord, or check the GitHub Wiki pages.

            Configuration options here apply to all worlds, unless you specify overrides inside
            the world-specific config file inside each world folder.

            Wiki: https://github.com/GaleMC/Gale/wiki
            Discord: https://discord.gg/gwezNT8c24""";

    private static final String WORLD_HEADER = """
        This is a world configuration file for Gale.
        This file may start empty but can be filled with settings to override ones in the %s/%s

        Wiki: https://github.com/GaleMC/Gale/wiki
        Discord: https://discord.gg/gwezNT8c24""";

    public GaleConfigurations(final Path globalFolder) {
        super(globalFolder, GaleGlobalConfiguration.class, GaleWorldConfiguration.class, GLOBAL_CONFIG_FILE_NAME, WORLD_DEFAULTS_CONFIG_FILE_NAME, WORLD_CONFIG_FILE_NAME);
    }

    @Override
    protected int globalConfigVersion() {
        return GaleGlobalConfiguration.CURRENT_VERSION;
    }

    @Override
    protected int worldConfigVersion() {
        return GaleWorldConfiguration.CURRENT_VERSION;
    }

    @Override
    protected YamlConfigurationLoader.Builder createLoaderBuilder() {
        return super.createLoaderBuilder()
            .defaultOptions(GaleConfigurations::defaultOptions);
    }

    private static ConfigurationOptions defaultOptions(ConfigurationOptions options) {
        return options.serializers(builder -> builder
            .register(MapSerializer.TYPE, new MapSerializer(false))
            .register(new EnumValueSerializer())
            .register(new ComponentSerializer())
        );
    }

    @Override
    protected ObjectMapper.Factory.Builder createGlobalObjectMapperFactoryBuilder() {
        return defaultGlobalFactoryBuilder(super.createGlobalObjectMapperFactoryBuilder());
    }

    private static ObjectMapper.Factory.Builder defaultGlobalFactoryBuilder(ObjectMapper.Factory.Builder builder) {
        return builder.addDiscoverer(InnerClassFieldDiscoverer.globalConfig(Collections.emptyList()));
    }

    @Override
    protected YamlConfigurationLoader.Builder createGlobalLoaderBuilder(RegistryAccess registryAccess) {
        return super.createGlobalLoaderBuilder(registryAccess)
            .defaultOptions(GaleConfigurations::defaultGlobalOptions);
    }

    private static ConfigurationOptions defaultGlobalOptions(ConfigurationOptions options) {
        return options
            .header(GLOBAL_HEADER)
            .serializers(builder -> builder.register(new ServerboundPacketClassSerializer()));
    }

    @Override
    public GaleGlobalConfiguration initializeGlobalConfiguration(final RegistryAccess registryAccess) throws ConfigurateException {
        GaleGlobalConfiguration configuration = super.initializeGlobalConfiguration(registryAccess);
        GaleGlobalConfiguration.set(configuration);
        return configuration;
    }

    @Override
    protected ContextMap.Builder createDefaultContextMap(final RegistryAccess registryAccess) {
        return super.createDefaultContextMap(registryAccess)
            .put(PaperConfigurations.SPIGOT_WORLD_CONFIG_CONTEXT_KEY, PaperConfigurations.SPIGOT_WORLD_DEFAULTS);
    }

    @Override
    protected ObjectMapper.Factory.Builder createWorldObjectMapperFactoryBuilder(final ContextMap contextMap) {
        return super.createWorldObjectMapperFactoryBuilder(contextMap)
            .addDiscoverer(InnerClassFieldDiscoverer.galeWorldConfig(createWorldConfigInstance(contextMap), Collections.emptyList()));
    }

    private static GaleWorldConfiguration createWorldConfigInstance(ContextMap contextMap) {
        return new GaleWorldConfiguration(
            contextMap.require(PaperConfigurations.SPIGOT_WORLD_CONFIG_CONTEXT_KEY).get(),
            contextMap.require(Configurations.WORLD_KEY)
        );
    }

    @Override
    protected YamlConfigurationLoader.Builder createWorldConfigLoaderBuilder(final ContextMap contextMap) {
        return super.createWorldConfigLoaderBuilder(contextMap)
            .defaultOptions(options -> options
                .header(contextMap.require(WORLD_KEY).equals(WORLD_DEFAULTS_KEY) ? WORLD_DEFAULTS_HEADER : WORLD_HEADER)
            );
    }

    @Override
    protected void applyWorldConfigTransformations(final ContextMap contextMap, final ConfigurationNode node, final ConfigurationNode defaultsNode) throws ConfigurateException {
        final ConfigurationNode version = node.node(Configuration.VERSION_FIELD);
        if (version.virtual()) {
            LOGGER.warn("The Gale world config file for " + contextMap.require(WORLD_KEY) + " didn't have a version set, assuming latest");
            version.raw(GaleWorldConfiguration.CURRENT_VERSION);
        }
        if (GaleRemovedConfigurations.REMOVED_WORLD_PATHS.length > 0) {
            ConfigurationTransformation.Builder builder = ConfigurationTransformation.builder();
            for (NodePath path : GaleRemovedConfigurations.REMOVED_WORLD_PATHS) {
                builder.addAction(path, TransformAction.remove());
            }
            builder.build().apply(node);
        }
    }

    @Override
    protected void applyGlobalConfigTransformations(ConfigurationNode node) throws ConfigurateException {
        if (GaleRemovedConfigurations.REMOVED_GLOBAL_PATHS.length > 0) {
            ConfigurationTransformation.Builder builder = ConfigurationTransformation.builder();
            for (NodePath path : GaleRemovedConfigurations.REMOVED_GLOBAL_PATHS) {
                builder.addAction(path, TransformAction.remove());
            }
            builder.build().apply(node);
        }
    }

    private static final List<TransformationsAware> DEFAULT_AWARE_TRANSFORMATIONS = Collections.emptyList();

    @Override
    protected void applyDefaultsAwareWorldConfigTransformations(final ContextMap contextMap, final ConfigurationNode worldNode, final ConfigurationNode defaultsNode) throws ConfigurateException {
        final ConfigurationTransformation.Builder builder = ConfigurationTransformation.builder();
        DEFAULT_AWARE_TRANSFORMATIONS.forEach(transform -> transform.apply(builder, contextMap, defaultsNode));

        ConfigurationTransformation transformation;
        try {
            transformation = builder.build();
        } catch (IllegalArgumentException ignored) {
            return;
        }
        transformation.apply(worldNode);
    }

    @Override
    public GaleWorldConfiguration createWorldConfig(final ContextMap contextMap) {
        final String levelKey = contextMap.require(WORLD_KEY).toString();
        try {
            return super.createWorldConfig(contextMap);
        } catch (IOException exception) {
            throw new RuntimeException("Could not create Gale world config for " + levelKey, exception);
        }
    }

    @Override
    protected boolean isConfigType(final Type type) {
        return ConfigurationPart.class.isAssignableFrom(erase(type));
    }

    public void reloadConfigs(MinecraftServer server) {
        try {
            this.initializeGlobalConfiguration(server.registryAccess(), reloader(this.globalConfigClass, GaleGlobalConfiguration.get()));
            this.initializeWorldDefaultsConfiguration(server.registryAccess());
            for (ServerLevel level : server.getAllLevels()) {
                Identifier worldKey = level.dimension().identifier();
                SpigotWorldConfig spigotConfig = level.spigotConfig;
                this.createWorldConfig(PaperConfigurations.createWorldContextMap(
                    server.storageSource.getDimensionPath(level.dimension()),
                    worldKey,
                    spigotConfig,
                    server.registryAccess(),
                    level.getGameRules()
                ), reloader(this.worldConfigClass, level.galeConfig));
            }
        } catch (Exception ex) {
            throw new RuntimeException("Could not reload Gale configuration files", ex);
        }
    }

    public static GaleConfigurations setup(final Path configDir) throws Exception {
        try {
            PaperConfigurations.createDirectoriesSymlinkAware(configDir);
            return new GaleConfigurations(configDir);
        } catch (final IOException ex) {
            throw new RuntimeException("Could not setup GaleConfigurations", ex);
        }
    }

    @FunctionalInterface
    public interface TransformationsAware {
        void apply(ConfigurationTransformation.Builder builder, ContextMap contextMap, ConfigurationNode defaultsNode);
    }

}
