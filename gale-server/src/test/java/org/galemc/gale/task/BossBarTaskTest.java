package org.galemc.gale.task;

import java.util.UUID;
import java.util.stream.Stream;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.support.environment.Normal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Normal
class BossBarTaskTest {

    @ParameterizedTest
    @MethodSource("tasks")
    void schedulesSynchronousUpdatesEveryTwentyTicks(BossBarTask task) {
        BukkitScheduler scheduler = mock(BukkitScheduler.class);
        BukkitTask scheduled = mock(BukkitTask.class);
        when(scheduled.getTaskId()).thenReturn(1);
        when(scheduler.runTaskTimer(any(Plugin.class), same((Runnable) task), eq(20L), eq(20L)))
            .thenReturn(scheduled);

        try (var bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(Bukkit::getScheduler).thenReturn(scheduler);
            task.start();

            verify(scheduler).runTaskTimer(any(Plugin.class), same((Runnable) task), eq(20L), eq(20L));
            verify(scheduler, never()).runTaskTimerAsynchronously(any(Plugin.class), any(Runnable.class), anyLong(), anyLong());

            task.stop();
            verify(scheduler).cancelTask(1);
        }
    }

    @Test
    void samplesMemoryOnEachScheduledRun() {
        RAMBarTask task = new RAMBarTask();

        task.run();

        assertTrue(task.getAllocated() > 0);
        assertTrue(task.getUsed() > 0);
        assertTrue(task.getPercent() >= 0 && task.getPercent() <= 1);
    }

    @Test
    void updatesTpsBarOnEachScheduledRun() {
        TPSBarTask task = new TPSBarTask();
        Player player = mock(Player.class);
        UUID id = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(id);
        when(player.getPing()).thenReturn(42);
        ArgumentCaptor<BossBar> shown = ArgumentCaptor.forClass(BossBar.class);

        try (var bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(() -> Bukkit.getPlayer(id)).thenReturn(player);
            bukkit.when(Bukkit::getTPS).thenReturn(new double[] {12.5, 15, 20});
            bukkit.when(Bukkit::getAverageTickTime).thenReturn(37.5);

            task.addPlayer(player);
            verify(player).showBossBar(shown.capture());
            task.run();

            assertEquals(0.75F, shown.getValue().progress());
            assertEquals(BossBar.Color.GREEN, shown.getValue().color());
            bukkit.verify(Bukkit::getTPS);
            bukkit.verify(Bukkit::getAverageTickTime);

            assertTrue(task.removePlayer(player));
            assertFalse(task.hasPlayer(id));
            verify(player).hideBossBar(shown.getValue());
        }
    }

    static Stream<BossBarTask> tasks() {
        return Stream.of(new RAMBarTask(), new TPSBarTask());
    }
}
