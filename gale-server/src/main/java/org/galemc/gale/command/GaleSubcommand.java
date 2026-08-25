package org.galemc.gale.command;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import java.util.Collections;
import java.util.List;

public interface GaleSubcommand {

    boolean execute(CommandSender sender, String subCommand, String[] args);

    default List<String> tabComplete(final CommandSender sender, final String subCommand, final String[] args) {
        return Collections.emptyList();
    }

    boolean testPermission(CommandSender sender);

    Permission getPermission();
}
