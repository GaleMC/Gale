package org.galemc.gale.command.subcommands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.galemc.gale.command.GaleCommand;
import org.galemc.gale.command.PermissionedGaleSubcommand;
import org.galemc.gale.task.TPSBarTask;

import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;

public final class TpsBarCommand extends PermissionedGaleSubcommand {

    public static final String LITERAL_ARGUMENT = "tpsbar";
    public static final String PERM = GaleCommand.BASE_PERM + "." + LITERAL_ARGUMENT;

    public TpsBarCommand() {
        super(PERM, PermissionDefault.OP);
    }

    @Override
    public boolean execute(final CommandSender sender, final String subCommand, final String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can toggle the TPS bar.", RED));
            return true;
        }

        boolean result = TPSBarTask.instance().togglePlayer(player);
        Component output = MiniMessage.miniMessage().deserialize(TPSBarTask.TPSBAR_COMMAND_OUTPUT,
                Placeholder.component("onoff", Component.translatable(result ? "options.on" : "options.off")
                        .color(result ? GREEN : RED)),
                Placeholder.parsed("target", player.getName()));
        sender.sendMessage(output);
        return true;
    }
}
