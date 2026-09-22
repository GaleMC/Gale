# Tick Benchmarks

## Scope

This package changes tick bookkeeping, not simulation frequency:

- `LevelTicks` keeps a lower bound on the earliest container deadline.
  Block and fluid schedulers skip the container-map scan when nothing can be
  due. Adding a container, scheduling an earlier head, unpacking saved ticks,
  and rescheduling leftovers lower the bound immediately. Removing a head or
  container can leave a stale, early bound; this causes one unnecessary scan,
  never a delayed tick. Due containers denied by `tickCheck` remain eligible
  for another check on the next collection. A scan recomputes the bound.
- `Level.tickBlockEntities` creates its identity removal set only after finding
  a removed ticker. With no removals, cleanup only checks for null entries
  instead of hashing every ticker. Removal stays deferred until after iteration;
  pending additions, tick order, and mid-tick task frequency are unchanged.
- `GoalSelector` cleans stopped flag owners by enum key instead of creating
  `EnumMap` entry wrappers. Goal priorities, callbacks, and evaluation order
  are unchanged.

This does not optimize every entity/block tick, skip dormant block entities,
change random ticking, or move world operations off the main thread.

## Validation

`LevelTicksTest` covers deadlines, earlier scheduling, prepopulated containers,
saved ticks, blocked chunks, budgets, leftovers, unload/reload, area clearing,
direct head removal, callback scheduling, copying, extreme timestamps, and
seeded ordering across chunks.

`BlockEntityTickTest` calls the production tick method on a mocked level.
It covers identity-based removal, mutation during callbacks, pending additions,
frozen ticks, non-ticking chunks, null cleanup, and mid-tick task counts.
This is not a real hopper/furnace or world integration test.

`GoalSelectorTickTest` exercises every flag, priority preemption, disabling and
enabling flags, multiple locks, goal removal, and every-tick update rules.

`TickEquivalenceCheck` compares 32,000 scheduler cycles and 32,000 goal cycles
with the reference implementations using 64 deterministic seeds. Scheduler
checks include execution order and serialized remaining ticks, with changing
budgets, deadlines, priorities, chunk eligibility, load state, and head removal.
Goal checks compare callback traces and running-task state.

## Running

```powershell
.\gradlew.bat :gale-server:test --tests org.bukkit.support.suite.NormalTestSuite
.\gradlew.bat -I gradle-bin/tick-benchmark.init.gradle :gale-server:tickEquivalenceCheck --no-configuration-cache
.\gradlew.bat -I gradle-bin/tick-benchmark.init.gradle :gale-server:tickBenchmark --no-configuration-cache
```

The optional init script generates benchmark-only reference classes by undoing
these two changes in `LevelTicks` and `GoalSelector`, then renaming the classes.
These transformations restore the corresponding behavior from Gale `3954c19`.
They fail when their expected source snippets no longer match. They are not an
independent implementation oracle for unrelated existing bugs.
Generated sources live in `gale-server/build/generated/tick-benchmark`.
Neither JMH nor reference classes are included in the server runtime.

JMH defaults: two JVM forks, three one-second warmups, five one-second
measurements, fixed 512 MiB heap, and the GC profiler.
`-PjmhArgs="..."` overrides the JMH arguments.
The benchmark task runs the equivalence check before measuring.
Results go to `gale-server/build/reports/tick-jmh.json`.

## Workloads

- Scheduler: 1,024 or 8,192 populated containers, either all scheduled in the
  future, all due but denied by the tick predicate, or all due and executed.
  Active callbacks reschedule the same positions for the next collection.
  Setup is outside measurement; active-case schedule allocations are included.
- Goals: an empty selector or five running goals, one per flag.
  This measures selector overhead, not Minecraft mob AI bodies.
- Block entity cleanup: the isolated final cleanup operation with zero,
  1,024, or 16,384 live entries and no removals. This does not measure the
  full block entity loop, chunk checks, or any block entity tick body.

Microbenchmarks cannot establish overall TPS/MSPT gains. Future-only scheduler
results are an upper-bound favorable case; continually due ticks still require
the scan. Compare active and blocked workloads as well before deployment.
Live-server profiling and real-world behavior tests remain necessary.

## Local Results

2026-09-21, Windows 11, Intel Core i7-12650H, Oracle JDK 25.0.4.
Times are microseconds per operation with JMH's 99.9% error margin.
Measurements were taken on a shared machine and show substantial variance.
Final validation: 40 new regression tests; `NormalTestSuite` completed with
2,753 passed and one skipped. All 159 source patches applied successfully and
the three modified production files matched the measured sources exactly.
`createPaperclipJar` completed successfully. No live-server integration or
TPS/MSPT comparison was performed.

| Case | Reference us/op | Optimized us/op | Reference B/op | Optimized B/op |
| --- | ---: | ---: | ---: | ---: |
| Cleanup, empty list | 0.024 +/- 0.012 | 0.001 +/- 0.001 | 152 | approximately 0 |
| Cleanup, 1,024 live entries | 6.789 +/- 1.245 | 0.164 +/- 0.017 | 152 | approximately 0 |
| Cleanup, 16,384 live entries | 74.559 +/- 55.453 | 3.380 +/- 0.873 | 153 | approximately 0 |
| Empty goal selector | 0.005 +/- 0.001 | 0.006 +/- 0.002 | approximately 0 | approximately 0 |
| Five running goals | 0.072 +/- 0.063 | 0.078 +/- 0.033 | 152 | approximately 0 |

Goal-selector timings overlap; there is no established CPU speedup here.
The allocation reduction is measurable. The cleanup results apply only to
the isolated no-removal cleanup phase, not to complete block entity ticks.
See `build/reports/tick-jmh.json` for the initial full run.

The initial scheduler implementation updated its cached field on every scan
entry. At 1,024 blocked containers it measured 23.270 +/- 1.303 us/op versus
9.816 +/- 4.392 for the reference. That implementation was rejected. The
revised implementation accumulates a local minimum during the scan and merges
it with the field afterward, preserving callback scheduling updates.

Final scheduler results are in `build/reports/tick-scheduler-final-jmh.json`:

| Containers | Workload | Reference us/op | Optimized us/op | Reference B/op | Optimized B/op |
| ---: | --- | ---: | ---: | ---: | ---: |
| 1,024 | Future | 3.009 +/- 0.087 | 0.0036 +/- 0.0002 | 72 | approximately 0 |
| 1,024 | Blocked | 7.991 +/- 0.888 | 8.024 +/- 0.505 | 72 | 72 |
| 1,024 | Active | 247.934 +/- 28.205 | 233.321 +/- 27.481 | 41034 | 41034 |
| 8,192 | Future | 72.181 +/- 5.196 | 0.0036 +/- 0.0003 | 73 | approximately 0 |
| 8,192 | Blocked | 125.556 +/- 18.399 | 133.957 +/- 15.858 | 73 | 73 |
| 8,192 | Active | 3212.195 +/- 99.396 | 3015.923 +/- 209.968 | 327814 | 327813 |

Future-only collections avoid the scan entirely. Active and blocked confidence
intervals overlap, so neither an active-workload speedup nor absence of all
regressions is established. The 8,192-container blocked point estimate is
approximately 6.7% slower; repeat on a controlled host with real scheduling
patterns before drawing deployment conclusions. The initial field-update
regression is not reproduced at 1,024 blocked containers in this run.
