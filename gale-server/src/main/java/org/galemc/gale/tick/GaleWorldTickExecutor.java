package org.galemc.gale.tick;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;

public final class GaleWorldTickExecutor {

    private static final long TICK_TIMEOUT_NANOS = 60L * TimeUtil.NANOSECONDS_PER_SECOND;

    private final ExecutorService worldThreadPool;
    private final int parallelism;

    public GaleWorldTickExecutor(int maxThreads) {
        int available = Runtime.getRuntime().availableProcessors();
        int desired = Math.min(maxThreads, available - 1);
        this.parallelism = Math.max(2, Math.min(desired, 16));
        this.worldThreadPool = Executors.newFixedThreadPool(this.parallelism, new WorldTickThreadFactory());
    }

    public static GaleWorldTickExecutor create(boolean enabled, int maxThreads) {
        if (!enabled) {
            return null;
        }
        return new GaleWorldTickExecutor(Math.max(2, maxThreads));
    }

    public int getParallelism() {
        return parallelism;
    }

    public void tickWorldsParallel(BooleanSupplier haveTime, List<ServerLevel> levels) {
        if (levels.size() <= 1) {
            for (int i = 0; i < levels.size(); i++) {
                tickSingleWorldSequentially(levels.get(i), haveTime);
            }
            return;
        }

        CompletableFuture<Void>[] futures = new CompletableFuture[levels.size()];
        GaleThreadMarkers.setParallelTickActive(true);
        try {
            for (int i = 0; i < levels.size(); i++) {
                final ServerLevel level = levels.get(i);
                final BooleanSupplier haveTimeCapture = haveTime;
                futures[i] = CompletableFuture.runAsync(() -> {
                    GaleThreadMarkers.markAsWorldMainThread(level);
                    try {
                        tickSingleWorldSequentially(level, haveTimeCapture);
                    } finally {
                        GaleThreadMarkers.clearWorldMainThreadMarker();
                    }
                }, worldThreadPool);
            }

            try {
                CompletableFuture.allOf(futures).get(TICK_TIMEOUT_NANOS, TimeUnit.NANOSECONDS);
            } catch (TimeoutException e) {
                cancelAllFutures(futures);
                throw new RuntimeException("Parallel world tick exceeded 60s timeout", e);
            } catch (ExecutionException e) {
                cancelAllFutures(futures);
                Throwable cause = e.getCause();
                if (cause == null) {
                    throw new RuntimeException("Parallel world tick failed with no cause", e);
                }
                if (cause instanceof ReportedException re) throw re;
                if (cause instanceof RuntimeException re) throw re;
                if (cause instanceof Error err) throw err;
                throw new RuntimeException(cause);
            } catch (InterruptedException e) {
                cancelAllFutures(futures);
                Thread.currentThread().interrupt();
                throw new RuntimeException("Parallel world tick interrupted", e);
            }
        } finally {
            GaleThreadMarkers.setParallelTickActive(false);
        }
    }

    private static void cancelAllFutures(CompletableFuture<Void>[] futures) {
        if (futures == null) return;
        for (CompletableFuture<Void> f : futures) {
            if (f != null) {
                f.cancel(false);
            }
        }
    }

    private static void tickSingleWorldSequentially(ServerLevel level, BooleanSupplier haveTime) {
        ProfilerFiller profiler = Profiler.get();
        profiler.push(() -> level + " " + level.dimension().identifier());
        profiler.push("tick");
        try {
            level.tick(haveTime);
        } catch (Throwable t) {
            CrashReport report = CrashReport.forThrowable(t, "Exception ticking world " + level.dimension().identifier());
            level.fillReportDetails(report);
            throw new ReportedException(report);
        } finally {
            profiler.pop();
            profiler.pop();
            level.explosionDensityCache.clear();
        }
    }

    public void shutdown() {
        worldThreadPool.shutdown();
        try {
            if (!worldThreadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                worldThreadPool.shutdownNow();
                if (!worldThreadPool.awaitTermination(3, TimeUnit.SECONDS)) {
                }
            }
        } catch (InterruptedException e) {
            worldThreadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static final class WorldTickThreadFactory implements ThreadFactory {
        private final AtomicInteger counter = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "Gale-World-Tick-" + counter.incrementAndGet());
            thread.setDaemon(true);
            thread.setPriority(Thread.NORM_PRIORITY);
            return thread;
        }
    }
}
