package org.galemc.gale.pathfinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.ReferencePathFinder;
import net.minecraft.world.level.pathfinder.Target;
import org.bukkit.support.environment.Normal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@Normal
class PathFinderTest {
    @ParameterizedTest
    @MethodSource("searches")
    void matchesReferenceSearch(long seed, int targetCount, int visitLimit, int reachRange, boolean debug) {
        GridNodeEvaluator expectedEvaluator = new GridNodeEvaluator(32, seed, true);
        GridNodeEvaluator actualEvaluator = new GridNodeEvaluator(32, seed, true);
        ReferencePathFinder reference = new ReferencePathFinder(expectedEvaluator, visitLimit);
        PathFinder optimized = new PathFinder(actualEvaluator, visitLimit);
        reference.setCaptureDebug(() -> debug);
        optimized.setCaptureDebug(() -> debug);
        Random random = new Random(seed + 1);
        Set<BlockPos> targets = new LinkedHashSet<>();
        while (targets.size() < targetCount) {
            targets.add(new BlockPos(random.nextInt(40), 0, random.nextInt(40)));
        }
        for (float multiplier : new float[] {0, 0.5F, 1}) {
            Path expected = reference.findPath(null, null, targets, 64, reachRange, multiplier);
            Path actual = optimized.findPath(null, null, targets, 64, reachRange, multiplier);
            assertPathEquals(expected, actual);
            assertEquals(expectedEvaluator.expansions(), actualEvaluator.expansions());
        }
    }

    static Stream<Arguments> searches() {
        return Stream.of(0L, 1L, 42L, 1729L, 65535L, 987654321L).flatMap(seed ->
            Stream.of(1, 5, 32).flatMap(targetCount ->
                Stream.of(1, 64, 1024).flatMap(limit ->
                    Stream.of(0, 2).flatMap(reach ->
                        Stream.of(false, true).map(debug -> Arguments.of(seed, targetCount, limit, reach, debug))))));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void matchesReferenceCandidateSelection(boolean reached) {
        Random random = new Random(913);
        for (int round = 0; round < 300; round++) {
            Node[] endpoints = new Node[1 + random.nextInt(32)];
            for (int i = 0; i < endpoints.length; i++) {
                endpoints[i] = chain(1 + random.nextInt(256), random.nextInt(40) - 20);
            }
            Set<BlockPos> targets = targets(endpoints.length);
            Path expected = new ReferencePathFinder(new CandidateNodeEvaluator(endpoints, reached), 2)
                .findPath(null, null, targets, 1024, 0, 1);
            Path actual = new PathFinder(new CandidateNodeEvaluator(endpoints, reached), 2)
                .findPath(null, null, targets, 1024, 0, 1);
            assertPathEquals(expected, actual);
        }
    }

    @Test
    void reachedTargetsPreferDistanceBeforeLength() {
        Path path = findCandidates(new Node[] {chain(1, 100), chain(10, 1)}, true, targets(2));
        assertEquals(new BlockPos(1, 0, 0), path.getTarget());
        assertEquals(10, path.getNodeCount());
    }

    @Test
    void unreachedTargetsPreferLengthRegardlessOfDistance() {
        Path path = findCandidates(new Node[] {chain(1, 100), chain(10, 1)}, false, targets(2));
        assertEquals(BlockPos.ZERO, path.getTarget());
        assertEquals(1, path.getNodeCount());
        assertFalse(path.canReach());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void equalScoresKeepFirstTarget(boolean reached) {
        Set<BlockPos> targets = new LinkedHashSet<>(List.of(new BlockPos(1, 0, 0), BlockPos.ZERO));
        Path path = findCandidates(new Node[] {chain(10, 0), chain(10, 1)}, reached, targets);
        assertEquals(new BlockPos(1, 0, 0), path.getTarget());
    }

    @Test
    void equalDistancesPreferShorterPath() {
        Path path = findCandidates(new Node[] {chain(10, 0), chain(3, 1)}, true, targets(2));
        assertEquals(new BlockPos(1, 0, 0), path.getTarget());
        assertEquals(3, path.getNodeCount());
    }

    @Test
    void longPathRetainsOrderAndMutableNodes() {
        Node end = chain(4096, 4095);
        Path path = findCandidates(new Node[] {end}, false, targets(1));
        assertEquals(4096, path.getNodeCount());
        for (int i = 0; i < path.getNodeCount(); i++) {
            assertEquals(i, path.getNode(i).x);
        }
        assertSame(end, path.getEndNode());
        path.truncateNodes(2);
        Node replacement = new Node(20, 0, 0);
        path.replaceNode(1, replacement);
        path.nodes.add(end);
        assertSame(replacement, path.getNode(1));
        assertEquals(3, path.getNodeCount());
    }

    @Test
    void emptyTargetsReturnNull() {
        assertNull(new PathFinder(new GridNodeEvaluator(4, 0, false), 8)
            .findPath(null, null, Set.of(), 16, 0, 1));
    }

    @Test
    void openGridReachesTarget() {
        BlockPos target = new BlockPos(31, 0, 31);
        Path expected = new ReferencePathFinder(new GridNodeEvaluator(32, 0, false), 1024)
            .findPath(null, null, Set.of(target), 128, 0, 1);
        Path actual = new PathFinder(new GridNodeEvaluator(32, 0, false), 1024)
            .findPath(null, null, Set.of(target), 128, 0, 1);
        assertPathEquals(expected, actual);
        assertTrue(actual.canReach());
        assertEquals(32, actual.getNodeCount());
        assertEquals(target, actual.getEndNode().asBlockPos());
    }

    @Test
    void reusesHeuristicsWithoutChangingExpansionOrder() {
        int savedCalls = 0;
        for (long seed = 0; seed < 16; seed++) {
            CountingGrid referenceGrid = new CountingGrid(seed);
            CountingGrid optimizedGrid = new CountingGrid(seed);
            Set<BlockPos> targets = Set.of(new BlockPos(47, 0, 47), new BlockPos(47, 0, 40));
            Path expected = new ReferencePathFinder(referenceGrid, 1024).findPath(null, null, targets, 128, 0, 1);
            Path actual = new PathFinder(optimizedGrid, 1024).findPath(null, null, targets, 128, 0, 1);
            assertPathEquals(expected, actual);
            assertEquals(referenceGrid.expansions(), optimizedGrid.expansions());
            assertEquals(referenceGrid.expansionOrder, optimizedGrid.expansionOrder);
            assertTrue(optimizedGrid.heuristicCalls <= referenceGrid.heuristicCalls);
            savedCalls += referenceGrid.heuristicCalls - optimizedGrid.heuristicCalls;
        }
        assertTrue(savedCalls > 0);
    }

    private static final class CountingGrid extends GridNodeEvaluator {
        private final List<BlockPos> expansionOrder = new ArrayList<>();
        private int heuristicCalls;

        private CountingGrid(long seed) {
            super(48, seed, true);
        }

        @Override
        public int getNeighbors(Node[] neighbors, Node current) {
            this.expansionOrder.add(current.asBlockPos());
            return super.getNeighbors(neighbors, current);
        }

        @Override
        protected Node getNode(int x, int y, int z) {
            return this.nodes.computeIfAbsent(Node.createHash(x, y, z), key -> new Node(x, y, z) {
                @Override
                public float distanceTo(Node to) {
                    if (to instanceof Target) {
                        heuristicCalls++;
                    }
                    return super.distanceTo(to);
                }
            });
        }
    }

    private static Path findCandidates(Node[] endpoints, boolean reached, Set<BlockPos> targets) {
        Path result = new PathFinder(new CandidateNodeEvaluator(endpoints, reached), 2)
            .findPath(null, null, targets, 1024, 0, 1);
        assertNotNull(result);
        return result;
    }

    public static Node chain(int length, int endX) {
        Node end = null;
        for (int i = 0; i < length; i++) {
            Node node = new Node(endX - length + i + 1, 0, 0);
            node.cameFrom = end;
            end = node;
        }
        return end;
    }

    public static Set<BlockPos> targets(int count) {
        Set<BlockPos> targets = new LinkedHashSet<>();
        for (int i = 0; i < count; i++) {
            targets.add(new BlockPos(i, 0, 0));
        }
        return targets;
    }

    private static void assertPathEquals(Path expected, Path actual) {
        if (expected == null) {
            assertNull(actual);
            return;
        }
        assertNotNull(actual);
        assertEquals(expected.getTarget(), actual.getTarget());
        assertEquals(expected.canReach(), actual.canReach());
        assertEquals(expected.getDistToTarget(), actual.getDistToTarget());
        assertEquals(expected.getNextNodeIndex(), actual.getNextNodeIndex());
        assertEquals(expected.nodes, actual.nodes);
        for (int i = 0; i < expected.getNodeCount(); i++) {
            assertEquals(expected.getNode(i).g, actual.getNode(i).g);
            assertEquals(expected.getNode(i).h, actual.getNode(i).h);
            assertEquals(expected.getNode(i).f, actual.getNode(i).f);
        }
        if (expected.debugData() == null) {
            assertNull(actual.debugData());
        } else {
            assertNotNull(actual.debugData());
            assertArrayEquals(expected.debugData().openSet(), actual.debugData().openSet());
            assertEquals(new HashSet<>(Arrays.asList(expected.debugData().closedSet())),
                new HashSet<>(Arrays.asList(actual.debugData().closedSet())));
            assertEquals(expected.debugData().targetNodes(), actual.debugData().targetNodes());
        }
    }
}
