package org.galemc.gale.tick;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.ReferenceGoalSelector;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.LevelTicks;
import net.minecraft.world.ticks.ReferenceLevelTicks;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickPriority;

public final class TickEquivalenceCheck {
    public static void main(String[] args) {
        for (int seed = 0; seed < 64; seed++) {
            checkScheduler(seed);
            checkGoals(seed);
        }
        System.out.println("Matched 32000 scheduler cycles and 32000 goal cycles against reference implementations");
    }

    private static void checkScheduler(long seed) {
        Random random = new Random(seed);
        boolean[] allowed = new boolean[16];
        boolean[] loaded = new boolean[16];
        LevelTicks<String> actual = new LevelTicks<>(pos -> allowed[(int) pos]);
        ReferenceLevelTicks<String> expected = new ReferenceLevelTicks<>(pos -> allowed[(int) pos]);
        List<LevelChunkTicks<String>> actualContainers = new ArrayList<>();
        List<LevelChunkTicks<String>> expectedContainers = new ArrayList<>();
        for (int chunk = 0; chunk < 16; chunk++) {
            actualContainers.add(new LevelChunkTicks<>());
            expectedContainers.add(new LevelChunkTicks<>());
            actual.addContainer(new ChunkPos(chunk, 0), actualContainers.get(chunk));
            expected.addContainer(new ChunkPos(chunk, 0), expectedContainers.get(chunk));
            allowed[chunk] = loaded[chunk] = true;
        }
        long sequence = 0;
        for (int round = 0; round < 500; round++) {
            int changed = random.nextInt(16);
            allowed[changed] = random.nextBoolean();
            if (round % 11 == 0) {
                if (loaded[changed]) {
                    actual.removeContainer(new ChunkPos(changed, 0));
                    expected.removeContainer(new ChunkPos(changed, 0));
                } else {
                    actual.addContainer(new ChunkPos(changed, 0), actualContainers.get(changed));
                    expected.addContainer(new ChunkPos(changed, 0), expectedContainers.get(changed));
                }
                loaded[changed] = !loaded[changed];
            }
            for (int added = 0; added < 12; added++) {
                int chunk = random.nextInt(16);
                BlockPos pos = new BlockPos(chunk * 16 + random.nextInt(16), 64 + random.nextInt(4), 0);
                ScheduledTick<String> tick = new ScheduledTick<>("block", pos, round + random.nextInt(20) - 5,
                    TickPriority.values()[random.nextInt(TickPriority.values().length)], sequence++);
                actualContainers.get(chunk).schedule(tick);
                expectedContainers.get(chunk).schedule(tick);
            }
            if (round % 7 == 0) {
                if (!java.util.Objects.equals(actualContainers.get(changed).poll(), expectedContainers.get(changed).poll())) {
                    throw new AssertionError("External poll mismatch");
                }
            }
            if (round % 13 == 0) {
                BoundingBox area = new BoundingBox(changed * 16, 64, 0, changed * 16 + 7, 67, 0);
                actual.clearArea(area);
                expected.clearArea(area);
            }
            int budget = random.nextInt(40);
            long time = round - random.nextInt(3);
            List<BlockPos> actualOutput = new ArrayList<>();
            List<BlockPos> expectedOutput = new ArrayList<>();
            actual.tick(time, budget, (pos, type) -> actualOutput.add(pos));
            expected.tick(time, budget, (pos, type) -> expectedOutput.add(pos));
            if (!actualOutput.equals(expectedOutput) || actual.count() != expected.count()) {
                throw new AssertionError("Scheduler mismatch at seed " + seed + ", round " + round);
            }
            for (int chunk = 0; chunk < 16; chunk++) {
                if (!actualContainers.get(chunk).pack(time).equals(expectedContainers.get(chunk).pack(time))) {
                    throw new AssertionError("Pending tick mismatch at seed " + seed + ", round " + round);
                }
            }
        }
    }

    private static void checkGoals(long seed) {
        Random random = new Random(seed);
        GoalSelector actual = new GoalSelector();
        ReferenceGoalSelector expected = new ReferenceGoalSelector();
        List<String> actualEvents = new ArrayList<>();
        List<String> expectedEvents = new ArrayList<>();
        List<TestGoal> actualGoals = new ArrayList<>();
        List<TestGoal> expectedGoals = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            EnumSet<Goal.Flag> flags = EnumSet.noneOf(Goal.Flag.class);
            for (Goal.Flag flag : Goal.Flag.values()) {
                if (random.nextBoolean()) flags.add(flag);
            }
            TestGoal a = new TestGoal(i, flags, actualEvents);
            TestGoal e = new TestGoal(i, flags, expectedEvents);
            actualGoals.add(a);
            expectedGoals.add(e);
            int priority = random.nextInt(8);
            actual.addGoal(priority, a);
            expected.addGoal(priority, e);
        }
        for (int round = 0; round < 500; round++) {
            int changed = random.nextInt(12);
            boolean usable = random.nextBoolean();
            actualGoals.get(changed).usable = expectedGoals.get(changed).usable = usable;
            Goal.Flag flag = Goal.Flag.values()[random.nextInt(Goal.Flag.values().length)];
            boolean enabled = random.nextBoolean();
            actual.setControlFlag(flag, enabled);
            expected.setControlFlag(flag, enabled);
            if (round % 3 == 0) {
                actual.tickRunningGoals(false);
                expected.tickRunningGoals(false);
            } else {
                actual.tick();
                expected.tick();
            }
            if (!actualEvents.equals(expectedEvents) || actual.hasTasks() != expected.hasTasks()) {
                throw new AssertionError("Goal mismatch at seed " + seed + ", round " + round);
            }
            actualEvents.clear();
            expectedEvents.clear();
        }
    }

    private static final class TestGoal extends Goal {
        private final int id;
        private final List<String> events;
        private boolean usable;

        private TestGoal(int id, EnumSet<Goal.Flag> flags, List<String> events) {
            this.id = id;
            this.events = events;
            this.setFlags(flags);
        }

        @Override
        public boolean canUse() {
            this.events.add(this.id + " canUse");
            return this.usable;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return (this.id & 1) == 0;
        }

        @Override
        public void start() {
            this.events.add(this.id + " start");
        }

        @Override
        public void stop() {
            this.events.add(this.id + " stop");
        }

        @Override
        public void tick() {
            this.events.add(this.id + " tick");
        }
    }
}
