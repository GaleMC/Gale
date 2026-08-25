package org.galemc.gale.command;

import net.minecraft.server.MinecraftServer;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.util.permissions.CraftDefaultPermissions;

import java.util.HashMap;
import java.util.Map;

public final class GaleCommands {

    public static final String COMMAND_BASE_PERM = CraftDefaultPermissions.GALE_ROOT + ".command";

    private GaleCommands() {
    }

    private static final Map<String, Command> COMMANDS = new HashMap<>();

    static {
        COMMANDS.put(GaleCommand.COMMAND_LABEL, new GaleCommand());
    }

    public static void registerCommands(final MinecraftServer server) {
        COMMANDS.forEach((s, command) -> server.server.getCommandMap().register(s, "Gale", command));
    }
}
