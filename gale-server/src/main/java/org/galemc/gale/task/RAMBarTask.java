package org.galemc.gale.task;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

public class RAMBarTask extends BossBarTask {
    public static final String RAMBAR_COMMAND_OUTPUT = "<green>Rambar toggled <onoff> for <target>";

    public static final String COMMAND_RAM_BAR_TITLE = "<gray>Ram<yellow>:</yellow> <used>/<xmx> (<percent>)";
    public static final BossBar.Overlay COMMAND_RAM_BAR_PROGRESS_OVERLAY = BossBar.Overlay.NOTCHED_20;
    public static final BossBar.Color COMMAND_RAM_BAR_PROGRESS_COLOR_GOOD = BossBar.Color.GREEN;
    public static final BossBar.Color COMMAND_RAM_BAR_PROGRESS_COLOR_MEDIUM = BossBar.Color.YELLOW;
    public static final BossBar.Color COMMAND_RAM_BAR_PROGRESS_COLOR_LOW = BossBar.Color.RED;
    public static final String COMMAND_RAM_BAR_TEXT_COLOR_GOOD = "<gradient:#55ff55:#00aa00><text></gradient>";
    public static final String COMMAND_RAM_BAR_TEXT_COLOR_MEDIUM = "<gradient:#ffff55:#ffaa00><text></gradient>";
    public static final String COMMAND_RAM_BAR_TEXT_COLOR_LOW = "<gradient:#ff5555:#aa0000><text></gradient>";
    public static final int COMMAND_RAM_BAR_TICK_INTERVAL = 20;

    private static RAMBarTask instance;
    private long allocated = 0L;
    private long used = 0L;
    private long xmx = 0L;
    private long xms = 0L;
    private float percent = 0F;
    private int tick = 0;

    public static RAMBarTask instance() {
        if (instance == null) {
            instance = new RAMBarTask();
        }
        return instance;
    }

    @Override
    BossBar createBossBar() {
        return BossBar.bossBar(Component.text(""), 0.0F, instance().getBossBarColor(), COMMAND_RAM_BAR_PROGRESS_OVERLAY);
    }

    @Override
    void updateBossBar(BossBar bossbar, Player player) {
        bossbar.progress(getBossBarProgress());
        bossbar.color(getBossBarColor());
        bossbar.name(MiniMessage.miniMessage().deserialize(COMMAND_RAM_BAR_TITLE,
                Placeholder.component("allocated", format(this.allocated)),
                Placeholder.component("used", format(this.used)),
                Placeholder.component("xmx", format(this.xmx)),
                Placeholder.component("xms", format(this.xms)),
                Placeholder.unparsed("percent", ((int) (this.percent * 100)) + "%")
        ));
    }

    @Override
    public void run() {
        if (++this.tick < COMMAND_RAM_BAR_TICK_INTERVAL) {
            return;
        }
        this.tick = 0;

        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

        this.allocated = heap.getCommitted();
        this.used = heap.getUsed();
        this.xmx = heap.getMax();
        this.xms = heap.getInit();
        this.percent = Math.max(Math.min((float) this.used / this.xmx, 1.0F), 0.0F);

        super.run();
    }

    private float getBossBarProgress() {
        return this.percent;
    }

    private BossBar.Color getBossBarColor() {
        if (this.percent < 0.5F) {
            return COMMAND_RAM_BAR_PROGRESS_COLOR_GOOD;
        } else if (this.percent < 0.75F) {
            return COMMAND_RAM_BAR_PROGRESS_COLOR_MEDIUM;
        } else {
            return COMMAND_RAM_BAR_PROGRESS_COLOR_LOW;
        }
    }

    public Component format(long v) {
        String color;
        if (this.percent < 0.60F) {
            color = COMMAND_RAM_BAR_TEXT_COLOR_GOOD;
        } else if (this.percent < 0.85F) {
            color = COMMAND_RAM_BAR_TEXT_COLOR_MEDIUM;
        } else {
            color = COMMAND_RAM_BAR_TEXT_COLOR_LOW;
        }
        String value;
        if (v < 1024) {
            value = v + "B";
        } else {
            int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
            value = String.format("%.1f%s", (double) v / (1L << (z * 10)), "BKMGTPE".charAt(z));
        }
        return MiniMessage.miniMessage().deserialize(color, Placeholder.unparsed("text", value));
    }

    public long getAllocated() {
        return this.allocated;
    }

    public long getUsed() {
        return this.used;
    }

    public long getXmx() {
        return this.xmx;
    }

    public long getXms() {
        return this.xms;
    }

    public float getPercent() {
        return this.percent;
    }
}
