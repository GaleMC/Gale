package org.galemc.gale.tick;

import net.minecraft.server.level.ServerLevel;

public final class GaleThreadMarkers {

    private static final ThreadLocal<ServerLevel> CURRENT_WORLD_MAIN_THREAD = new ThreadLocal<>();
    private static volatile boolean parallelTickActive = false;
    private static volatile Thread mainServerThread;

    public static void captureMainServerThread() {
        if (mainServerThread == null) {
            mainServerThread = Thread.currentThread();
        }
    }

    public static Thread getMainServerThread() {
        return mainServerThread;
    }

    public static boolean isMainServerThread(Thread t) {
        Thread main = mainServerThread;
        return main != null && main == t;
    }

    public static void markAsWorldMainThread(ServerLevel level) {
        CURRENT_WORLD_MAIN_THREAD.set(level);
    }

    public static void clearWorldMainThreadMarker() {
        CURRENT_WORLD_MAIN_THREAD.remove();
    }

    public static ServerLevel getCurrentWorldMainThread() {
        return CURRENT_WORLD_MAIN_THREAD.get();
    }

    public static boolean isWorldMainThread(ServerLevel level) {
        ServerLevel marked = CURRENT_WORLD_MAIN_THREAD.get();
        return marked != null && marked == level;
    }

    public static void setParallelTickActive(boolean active) {
        parallelTickActive = active;
    }

    public static boolean isParallelTickActive() {
        return parallelTickActive;
    }

    private GaleThreadMarkers() {
    }
}
