package org.galemc.gale.tick;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.LevelTicks;
import net.minecraft.world.ticks.SavedTick;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickPriority;
import org.bukkit.support.environment.Normal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@Normal
class LevelTicksTest {
    private static final String TYPE = "block";

    @Test
    void waitsUntilDueAndWakesForEarlierSchedules() {
        LevelTicks<String> ticks = new LevelTicks<>(pos -> true);
        ticks.addContainer(new ChunkPos(0, 0), new LevelChunkTicks<>());
        ticks.schedule(tick(0, 100, 0));
        assertTrue(run(ticks, 10, 100).isEmpty());
        ticks.schedule(tick(1, 20, 1));
        assertTrue(run(ticks, 19, 100).isEmpty());
        assertEquals(List.of(pos(1)), run(ticks, 20, 100));
        assertEquals(List.of(pos(0)), run(ticks, 100, 100));
    }

    @Test
    void wakesForAlreadyPopulatedContainer() {
        LevelTicks<String> ticks = new LevelTicks<>(pos -> true);
        assertTrue(run(ticks, 100, 100).isEmpty());
        LevelChunkTicks<String> container = new LevelChunkTicks<>();
        container.schedule(tick(0, 1, 0));
        ticks.addContainer(new ChunkPos(0, 0), container);
        assertEquals(List.of(pos(0)), run(ticks, 101, 100));
    }

    @Test
    void wakesWhenSavedTicksAreUnpacked() {
        LevelTicks<String> ticks = new LevelTicks<>(pos -> true);
        LevelChunkTicks<String> container = new LevelChunkTicks<>(
            List.of(new SavedTick<>(TYPE, pos(0), 3, TickPriority.NORMAL)));
        ticks.addContainer(new ChunkPos(0, 0), container);
        assertTrue(run(ticks, 5, 100).isEmpty());
        container.unpack(5);
        assertTrue(run(ticks, 7, 100).isEmpty());
        assertEquals(List.of(pos(0)), run(ticks, 8, 100));
    }

    @Test
    void retriesDueContainersWhenTickingResumes() {
        AtomicBoolean allowed = new AtomicBoolean();
        LevelTicks<String> ticks = new LevelTicks<>(pos -> allowed.get());
        ticks.addContainer(new ChunkPos(0, 0), new LevelChunkTicks<>());
        ticks.schedule(tick(0, 1, 0));
        assertTrue(run(ticks, 1, 100).isEmpty());
        assertTrue(run(ticks, 2, 100).isEmpty());
        allowed.set(true);
        assertEquals(List.of(pos(0)), run(ticks, 3, 100));
    }

    @Test
    void respectsLimitAndReschedulesAllLeftovers() {
        LevelTicks<String> ticks = new LevelTicks<>(pos -> true);
        for (int chunk = 0; chunk < 4; chunk++) {
            ticks.addContainer(new ChunkPos(chunk, 0), new LevelChunkTicks<>());
            ticks.schedule(tick(chunk * 16, 1, chunk));
        }
        assertTrue(run(ticks, 1, 0).isEmpty());
        for (int chunk = 0; chunk < 4; chunk++) {
            assertEquals(List.of(pos(chunk * 16)), run(ticks, chunk + 2, 1));
        }
        assertEquals(0, ticks.count());
    }

    @Test
    void retainsFutureHeadAfterPartiallyDrainingContainer() {
        LevelTicks<String> ticks = new LevelTicks<>(pos -> true);
        ticks.addContainer(new ChunkPos(0, 0), new LevelChunkTicks<>());
        ticks.schedule(tick(0, 1, 0));
        ticks.schedule(tick(1, 20, 1));
        assertEquals(List.of(pos(0)), run(ticks, 1, 100));
        assertTrue(run(ticks, 19, 100).isEmpty());
        assertEquals(List.of(pos(1)), run(ticks, 20, 100));
    }

    @Test
    void unloadAndReloadDoNotLoseDueTicks() {
        LevelTicks<String> ticks = new LevelTicks<>(pos -> true);
        LevelChunkTicks<String> container = new LevelChunkTicks<>();
        ticks.addContainer(new ChunkPos(0, 0), container);
        ticks.schedule(tick(0, 5, 0));
        ticks.removeContainer(new ChunkPos(0, 0));
        assertTrue(run(ticks, 10, 100).isEmpty());
        ticks.addContainer(new ChunkPos(0, 0), container);
        assertEquals(List.of(pos(0)), run(ticks, 11, 100));
    }

    @Test
    void clearAreaAndDirectHeadRemovalLeaveOnlyRemainingTicks() {
        LevelTicks<String> ticks = new LevelTicks<>(pos -> true);
        LevelChunkTicks<String> container = new LevelChunkTicks<>();
        ticks.addContainer(new ChunkPos(0, 0), container);
        ticks.schedule(tick(0, 5, 0));
        ticks.schedule(tick(1, 10, 1));
        ticks.schedule(tick(2, 20, 2));
        ticks.clearArea(new BoundingBox(pos(0)));
        container.poll();
        assertTrue(run(ticks, 10, 100).isEmpty());
        assertEquals(List.of(pos(2)), run(ticks, 20, 100));
    }

    @Test
    void emptyContainerAfterExternalRemovalCanBeScheduledAgain() {
        LevelTicks<String> ticks = new LevelTicks<>(pos -> true);
        LevelChunkTicks<String> container = new LevelChunkTicks<>();
        ticks.addContainer(new ChunkPos(0, 0), container);
        ticks.schedule(tick(0, 5, 0));
        container.poll();
        assertTrue(run(ticks, 5, 100).isEmpty());
        ticks.schedule(tick(1, 6, 1));
        assertEquals(List.of(pos(1)), run(ticks, 6, 100));
    }

    @Test
    void schedulingDuringExecutionRunsOnNextCollection() {
        LevelTicks<String> ticks = new LevelTicks<>(pos -> true);
        ticks.addContainer(new ChunkPos(0, 0), new LevelChunkTicks<>());
        ticks.schedule(tick(0, 5, 0));
        List<BlockPos> output = new ArrayList<>();
        ticks.tick(5, 100, (pos, type) -> {
            output.add(pos);
            ticks.schedule(tick(1, 5, 1));
        });
        assertEquals(List.of(pos(0)), output);
        assertEquals(List.of(pos(1)), run(ticks, 6, 100));
    }

    @Test
    void copyAreaUpdatesDestinationDeadline() {
        LevelTicks<String> ticks = new LevelTicks<>(pos -> true);
        ticks.addContainer(new ChunkPos(0, 0), new LevelChunkTicks<>());
        ticks.addContainer(new ChunkPos(1, 0), new LevelChunkTicks<>());
        ticks.schedule(tick(0, 5, 0));
        ticks.copyArea(new BoundingBox(pos(0)), new Vec3i(16, 0, 0));
        assertEquals(List.of(pos(0), pos(16)), run(ticks, 5, 100));
    }

    @Test
    void handlesLongExtremesAndClockMovingBackwards() {
        LevelTicks<String> ticks = new LevelTicks<>(pos -> true);
        ticks.addContainer(new ChunkPos(0, 0), new LevelChunkTicks<>());
        ticks.schedule(tick(0, Long.MAX_VALUE, 0));
        assertTrue(run(ticks, Long.MAX_VALUE - 1, 100).isEmpty());
        ticks.schedule(tick(1, Long.MIN_VALUE, 1));
        assertEquals(List.of(pos(1)), run(ticks, Long.MIN_VALUE, 100));
        assertEquals(List.of(pos(0)), run(ticks, Long.MAX_VALUE, 100));
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 42, 913, 1729, 65535})
    void matchesSortedDueTicksAcrossContainers(long seed) {
        Random random = new Random(seed);
        LevelTicks<String> ticks = new LevelTicks<>(pos -> true);
        List<ScheduledTick<String>> expected = new ArrayList<>();
        for (int chunk = 0; chunk < 32; chunk++) {
            ticks.addContainer(new ChunkPos(chunk, 0), new LevelChunkTicks<>());
            for (int offset = 0; offset < 16; offset++) {
                ScheduledTick<String> tick = new ScheduledTick<>(TYPE, pos(chunk * 16 + offset),
                    random.nextInt(100), TickPriority.values()[random.nextInt(TickPriority.values().length)],
                    expected.size());
                expected.add(tick);
                ticks.schedule(tick);
            }
        }
        for (int time = 0; time < 100; time++) {
            long current = time;
            List<BlockPos> due = expected.stream().filter(t -> t.triggerTick() == current)
                .sorted(ScheduledTick.INTRA_TICK_DRAIN_ORDER).map(ScheduledTick::pos).toList();
            assertEquals(due, run(ticks, time, 1024));
        }
        assertEquals(0, ticks.count());
    }

    private static BlockPos pos(int x) {
        return new BlockPos(x, 64, 0);
    }

    private static ScheduledTick<String> tick(int x, long time, long order) {
        return new ScheduledTick<>(TYPE, pos(x), time, order);
    }

    private static List<BlockPos> run(LevelTicks<String> ticks, long time, int limit) {
        List<BlockPos> result = new ArrayList<>();
        ticks.tick(time, limit, (pos, type) -> result.add(pos));
        return result;
    }
}
