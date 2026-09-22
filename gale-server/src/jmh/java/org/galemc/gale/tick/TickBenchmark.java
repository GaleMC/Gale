package org.galemc.gale.tick;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.ReferenceGoalSelector;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.LevelTicks;
import net.minecraft.world.ticks.ReferenceLevelTicks;
import net.minecraft.world.ticks.ScheduledTick;
import org.openjdk.jmh.annotations.*;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 2, jvmArgsAppend = {"-Xms512m", "-Xmx512m"})
public class TickBenchmark {
    @State(Scope.Thread)
    public static class BlockEntityCleanup {
        @Param({"0", "1024", "16384"})
        public int count;

        private final List<Object> tickers = new ArrayList<>();

        @Setup
        public void setup() {
            for (int i = 0; i < this.count; i++) {
                this.tickers.add(new Object());
            }
        }
    }

    @State(Scope.Thread)
    public static class Scheduler {
        @Param({"1024", "8192"})
        public int containers;

        @Param({"future", "blocked", "active"})
        public String workload;

        private LevelTicks<String> optimized;
        private ReferenceLevelTicks<String> reference;
        private BiConsumer<BlockPos, String> optimizedOutput;
        private BiConsumer<BlockPos, String> referenceOutput;
        private long checksum;
        private long sequence;

        @Setup
        public void setup() {
            boolean allowed = !this.workload.equals("blocked");
            this.optimized = new LevelTicks<>(pos -> allowed);
            this.reference = new ReferenceLevelTicks<>(pos -> allowed);
            for (int i = 0; i < this.containers; i++) {
                ChunkPos chunk = new ChunkPos(i, 0);
                this.optimized.addContainer(chunk, new LevelChunkTicks<>());
                this.reference.addContainer(chunk, new LevelChunkTicks<>());
                ScheduledTick<String> tick = new ScheduledTick<>("block", new BlockPos(i * 16, 64, 0),
                    this.workload.equals("future") ? Long.MAX_VALUE : 0, i);
                this.optimized.schedule(tick);
                this.reference.schedule(tick);
            }
            this.sequence = this.containers;
            this.optimizedOutput = (pos, type) -> {
                this.checksum += pos.getX();
                this.optimized.schedule(new ScheduledTick<>(type, pos, 0, this.sequence++));
            };
            this.referenceOutput = (pos, type) -> {
                this.checksum += pos.getX();
                this.reference.schedule(new ScheduledTick<>(type, pos, 0, this.sequence++));
            };
        }
    }

    @State(Scope.Thread)
    public static class Goals {
        @Param({"0", "5"})
        public int count;

        private GoalSelector optimized;
        private ReferenceGoalSelector reference;
        private long checksum;

        @Setup
        public void setup() {
            this.optimized = new GoalSelector();
            this.reference = new ReferenceGoalSelector();
            for (int i = 0; i < this.count; i++) {
                Goal.Flag flag = Goal.Flag.values()[i];
                this.optimized.addGoal(i, this.goal(flag));
                this.reference.addGoal(i, this.goal(flag));
            }
            this.optimized.tick();
            this.reference.tick();
        }

        private Goal goal(Goal.Flag flag) {
            Goal goal = new Goal() {
                @Override
                public boolean canUse() {
                    return true;
                }

                @Override
                public void tick() {
                    checksum++;
                }
            };
            goal.setFlags(EnumSet.of(flag));
            return goal;
        }
    }

    @Benchmark
    public long scheduledOptimized(Scheduler state) {
        state.optimized.tick(0, 65536, state.optimizedOutput);
        return state.checksum;
    }

    @Benchmark
    public long scheduledReference(Scheduler state) {
        state.reference.tick(0, 65536, state.referenceOutput);
        return state.checksum;
    }

    @Benchmark
    public long goalsOptimized(Goals state) {
        state.optimized.tick();
        return state.checksum;
    }

    @Benchmark
    public long goalsReference(Goals state) {
        state.reference.tick();
        return state.checksum;
    }

    @Benchmark
    public List<Object> blockEntityCleanupReference(BlockEntityCleanup state) {
        ReferenceOpenHashSet<Object> removed = new ReferenceOpenHashSet<>();
        removed.add(null);
        state.tickers.removeAll(removed);
        return state.tickers;
    }

    @Benchmark
    public List<Object> blockEntityCleanupOptimized(BlockEntityCleanup state) {
        state.tickers.removeIf(Objects::isNull);
        return state.tickers;
    }
}
