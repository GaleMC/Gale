package org.galemc.gale.pathfinding;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.ReferencePathFinder;
import org.openjdk.jmh.annotations.*;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(value = 2, jvmArgsAppend = {"-Xms512m", "-Xmx512m"})
public class PathFinderBenchmark {
    @State(Scope.Thread)
    public static class Candidates {
        @Param({"1", "5"})
        public int targetCount;

        @Param({"16", "64"})
        public int length;

        @Param({"false"})
        public boolean reached;

        private Set<BlockPos> targets;
        private ReferencePathFinder reference;
        private PathFinder optimized;

        @Setup
        public void setup() {
            Node[] endpoints = new Node[this.targetCount];
            for (int i = 0; i < endpoints.length; i++) {
                endpoints[i] = PathFinderTest.chain(this.length, this.length);
            }
            this.targets = PathFinderTest.targets(this.targetCount);
            this.reference = new ReferencePathFinder(new CandidateNodeEvaluator(endpoints, this.reached), 2);
            this.optimized = new PathFinder(new CandidateNodeEvaluator(endpoints, this.reached), 2);
        }
    }

    @State(Scope.Thread)
    public static class Search {
        @Param({"1", "5"})
        public int targetCount;

        @Param({"false", "true"})
        public boolean obstacles;

        private Set<BlockPos> targets;
        private ReferencePathFinder reference;
        private PathFinder optimized;

        @Setup
        public void setup() {
            this.targets = new LinkedHashSet<>();
            for (int i = 0; i < this.targetCount; i++) {
                this.targets.add(new BlockPos(47, 0, 47 - i));
            }
            this.reference = new ReferencePathFinder(new GridNodeEvaluator(48, 1729, this.obstacles), 1024);
            this.optimized = new PathFinder(new GridNodeEvaluator(48, 1729, this.obstacles), 1024);
        }
    }

    @Benchmark
    public Path selectReference(Candidates state) {
        return state.reference.findPath(null, null, state.targets, 1024, 0, 1);
    }

    @Benchmark
    public Path selectOptimized(Candidates state) {
        return state.optimized.findPath(null, null, state.targets, 1024, 0, 1);
    }

    @Benchmark
    public Path searchReference(Search state) {
        return state.reference.findPath(null, null, state.targets, 128, 0, 1);
    }

    @Benchmark
    public Path searchOptimized(Search state) {
        return state.optimized.findPath(null, null, state.targets, 128, 0, 1);
    }
}
