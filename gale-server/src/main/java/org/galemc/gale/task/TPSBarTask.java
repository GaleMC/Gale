package org.galemc.gale.task;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TPSBarTask extends BossBarTask {
    public static final String TPSBAR_COMMAND_OUTPUT = "<green>Tpsbar toggled <onoff> for <target>";

    public static final String COMMAND_TPS_BAR_TITLE = "<gray>TPS<yellow>:</yellow> <tps> MSPT<yellow>:</yellow> <mspt> Ping<yellow>:</yellow> <ping>ms";
    public static final BossBar.Overlay COMMAND_TPS_BAR_PROGRESS_OVERLAY = BossBar.Overlay.NOTCHED_20;
    public static final TPSBarTask.FillMode COMMAND_TPS_BAR_PROGRESS_FILL_MODE = TPSBarTask.FillMode.MSPT;
    public static final BossBar.Color COMMAND_TPS_BAR_PROGRESS_COLOR_GOOD = BossBar.Color.GREEN;
    public static final BossBar.Color COMMAND_TPS_BAR_PROGRESS_COLOR_MEDIUM = BossBar.Color.YELLOW;
    public static final BossBar.Color COMMAND_TPS_BAR_PROGRESS_COLOR_LOW = BossBar.Color.RED;
    public static final String COMMAND_TPS_BAR_TEXT_COLOR_GOOD = "<gradient:#55ff55:#00aa00><text></gradient>";
    public static final String COMMAND_TPS_BAR_TEXT_COLOR_MEDIUM = "<gradient:#ffff55:#ffaa00><text></gradient>";
    public static final String COMMAND_TPS_BAR_TEXT_COLOR_LOW = "<gradient:#ff5555:#aa0000><text></gradient>";
    public static final int COMMAND_TPS_BAR_TICK_INTERVAL = 20;

    private static TPSBarTask instance;
    private double tps = 20.0D;
    private double mspt = 0.0D;
    private int tick = 0;

    public static TPSBarTask instance() {
        if (instance == null) {
            instance = new TPSBarTask();
        }
        return instance;
    }

    @Override
    BossBar createBossBar() {
        return BossBar.bossBar(Component.text(""), 0.0F, instance().getBossBarColor(), COMMAND_TPS_BAR_PROGRESS_OVERLAY);
    }

    @Override
    void updateBossBar(BossBar bossbar, Player player) {
        bossbar.progress(getBossBarProgress());
        bossbar.color(getBossBarColor());
        bossbar.name(MiniMessage.miniMessage().deserialize(COMMAND_TPS_BAR_TITLE,
                Placeholder.component("tps", getTPSColor()),
                Placeholder.component("mspt", getMSPTColor()),
                Placeholder.component("ping", getPingColor(player.getPing()))
        ));
    }

    @Override
    public void run() {
        if (++tick < COMMAND_TPS_BAR_TICK_INTERVAL) {
            return;
        }
        tick = 0;

        this.tps = Math.max(Math.min(Bukkit.getTPS()[0], 20.0D), 0.0D);
        this.mspt = Bukkit.getAverageTickTime();

        super.run();
    }

    private float getBossBarProgress() {
        if (COMMAND_TPS_BAR_PROGRESS_FILL_MODE == FillMode.MSPT) {
            return Math.max(Math.min((float) mspt / 50.0F, 1.0F), 0.0F);
        } else {
            return Math.max(Math.min((float) tps / 20.0F, 1.0F), 0.0F);
        }
    }

    private BossBar.Color getBossBarColor() {
        if (isGood(COMMAND_TPS_BAR_PROGRESS_FILL_MODE)) {
            return COMMAND_TPS_BAR_PROGRESS_COLOR_GOOD;
        } else if (isMedium(COMMAND_TPS_BAR_PROGRESS_FILL_MODE)) {
            return COMMAND_TPS_BAR_PROGRESS_COLOR_MEDIUM;
        } else {
            return COMMAND_TPS_BAR_PROGRESS_COLOR_LOW;
        }
    }

    private boolean isGood(FillMode mode) {
        return isGood(mode, 0);
    }

    private boolean isGood(FillMode mode, int ping) {
        if (mode == FillMode.MSPT) {
            return mspt < 40;
        } else if (mode == FillMode.TPS) {
            return tps >= 19;
        } else if (mode == FillMode.PING) {
            return ping < 100;
        } else {
            return false;
        }
    }

    private boolean isMedium(FillMode mode) {
        return isMedium(mode, 0);
    }

    private boolean isMedium(FillMode mode, int ping) {
        if (mode == FillMode.MSPT) {
            return mspt < 50;
        } else if (mode == FillMode.TPS) {
            return tps >= 15;
        } else if (mode == FillMode.PING) {
            return ping < 200;
        } else {
            return false;
        }
    }

    private Component getTPSColor() {
        String color;
        if (isGood(FillMode.TPS)) {
            color = COMMAND_TPS_BAR_TEXT_COLOR_GOOD;
        } else if (isMedium(FillMode.TPS)) {
            color = COMMAND_TPS_BAR_TEXT_COLOR_MEDIUM;
        } else {
            color = COMMAND_TPS_BAR_TEXT_COLOR_LOW;
        }
        return MiniMessage.miniMessage().deserialize(color, Placeholder.parsed("text", String.format("%.2f", tps)));
    }

    private Component getMSPTColor() {
        String color;
        if (isGood(FillMode.MSPT)) {
            color = COMMAND_TPS_BAR_TEXT_COLOR_GOOD;
        } else if (isMedium(FillMode.MSPT)) {
            color = COMMAND_TPS_BAR_TEXT_COLOR_MEDIUM;
        } else {
            color = COMMAND_TPS_BAR_TEXT_COLOR_LOW;
        }
        return MiniMessage.miniMessage().deserialize(color, Placeholder.parsed("text", String.format("%.2f", mspt)));
    }

    private Component getPingColor(int ping) {
        String color;
        if (isGood(FillMode.PING, ping)) {
            color = COMMAND_TPS_BAR_TEXT_COLOR_GOOD;
        } else if (isMedium(FillMode.PING, ping)) {
            color = COMMAND_TPS_BAR_TEXT_COLOR_MEDIUM;
        } else {
            color = COMMAND_TPS_BAR_TEXT_COLOR_LOW;
        }
        return MiniMessage.miniMessage().deserialize(color, Placeholder.parsed("text", String.format("%s", ping)));
    }

    public enum FillMode {
        TPS, MSPT, PING
    }
}
