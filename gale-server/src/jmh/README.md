# Pathfinding Benchmarks

## Scope

The optimization preserves the A* expansion order and changes two sources of
repeated work:

- An open node retains its heuristic when a cheaper route updates its `g` score.
  Targets and node coordinates are unchanged during a search, so computing the
  same distances again cannot improve a target's best heuristic.
- Candidate paths are compared before allocating their node lists. Only the
  selected path is constructed, using append and reverse instead of repeated
  insertion at index zero.

For `K` candidate paths of length `L`, selection and reconstruction take
`O(K * L)` work instead of `O(K * L^2)` array shifting in the worst case.
Identical candidate endpoints reuse the already counted length. A candidate
with a worse primary score does not need its chain counted.

No tick frequency, search limit, movement rule, or cross-search cache changes.
Unreached candidates still prefer node count; reached candidates prefer endpoint
distance and then node count. Equal scores retain the first candidate.

## Reference And Tests

`ReferencePathFinder` preserves the search implementation from Gale `28ae225`.
Only the class name, unused imports/comments, and unused configuration members
were removed or adjusted. Keep it independent of production optimizations.

`PathFinderTest` checks:

- 216 combinations of seeded terrain, target count, visit limit, reach range,
  and debug mode, each at three visit-limit multipliers.
- 600 generated candidate sets across reached and unreached selection.
- Tie order, score priority, empty targets, long paths, and list mutability.
- Identical expansion counts, path coordinates/costs, and debug node sets.
- Matching expansion traces on 16 additional seeded grids, and an open-grid
  reachability check.
- Fewer heuristic calculations without changing paths or expansion counts.

The seeded terrain is an eight-neighbor graph with blocked cells and movement
penalties, not a Minecraft world. It deliberately leaves block collision,
mob-specific evaluators, plugin events, and tick scheduling out of the test.

## Running

From the repository root, after applying patches:

```powershell
.\gradlew.bat :gale-server:test --tests org.bukkit.support.suite.NormalTestSuite
.\gradlew.bat -I gradle-bin/pathfinding-benchmark.init.gradle :gale-server:pathfindingBenchmark --no-configuration-cache
```

On Unix, use `./gradlew` instead. The optional init script adds JMH only for the
benchmark invocation; no JMH dependency is added to the server runtime.

Defaults: JMH 1.37, two independent JVM forks per case, three one-second warmup
iterations, five one-second measurements, and a fixed 512 MiB heap.
The GC profiler records allocated bytes per operation.
Results are written to `gale-server/build/reports/pathfinding-jmh.json`.

`-PjmhArgs="..."` replaces all JMH arguments, including the result destination.

## Interpreting Results

- `select*` uses prebuilt candidate chains and synthetic targets to isolate the
  selection/reconstruction phase. The public search entry point still performs
  its normal target setup. Candidate chains are prepared outside measurement.
- `search*` runs the complete A* algorithm on deterministic 48 by 48 graphs,
  with either open space or seeded obstacles and movement penalties. It includes
  node creation and search-local cache resets, with one or five targets.
- Returned paths are consumed by JMH. The reference and optimized variants use
  the same input parameters and separate evaluators.

Microbenchmark gains are not whole-server TPS or MSPT gains. Validate on real
mob workloads and Minecraft collision geometry before making deployment-wide
performance claims. Compare confidence intervals, not just point estimates.

## Local Measurement

2026-09-20, Windows 11, Intel Core i7-12650H, Oracle JDK 25.0.4.
All 16 default benchmark cases completed using the settings above.
Times are microseconds per operation with JMH's 99.9% error margin.
Allocation is rounded bytes per operation, including search setup.

| Case | Reference us/op | Optimized us/op | Reference B/op | Optimized B/op |
| --- | ---: | ---: | ---: | ---: |
| Open grid, 1 target | 23.242 +/- 7.998 | 20.267 +/- 12.221 | 15864 | 15464 |
| Open grid, 5 targets | 25.527 +/- 3.713 | 20.760 +/- 6.664 | 15880 | 15480 |
| Obstacles, 1 target | 333.805 +/- 25.497 | 324.719 +/- 89.843 | 71794 | 71170 |
| Obstacles, 5 targets | 402.628 +/- 42.349 | 340.926 +/- 52.211 | 76403 | 71826 |
| Selection, 16 nodes, 1 target | 1.203 +/- 0.267 | 0.153 +/- 0.027 | 584 | 424 |
| Selection, 16 nodes, 5 targets | 4.864 +/- 1.565 | 0.254 +/- 0.067 | 2200 | 824 |
| Selection, 64 nodes, 1 target | 6.040 +/- 1.720 | 0.413 +/- 0.091 | 1264 | 616 |
| Selection, 64 nodes, 5 targets | 32.921 +/- 12.009 | 0.717 +/- 0.128 | 5600 | 1016 |

Selection-phase allocation fell by 27-82% in these fixtures. Full-search
allocation fell by approximately 1-6%. The full-search timing intervals overlap;
this run does not establish a reliable end-to-end speedup percentage. Timing
variance was substantial on this shared machine, so repeat on a controlled host
before drawing CPU-throughput conclusions. These are not live-server results.
