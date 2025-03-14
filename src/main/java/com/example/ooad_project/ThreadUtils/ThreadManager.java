package com.example.ooad_project.ThreadUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Runs a given runnable task on a new thread.
     * The thread is marked as a daemon, meaning it will not block the application shutdown.
     *
     * @param runnable the task to be executed.
     */
    public static void run(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true); // Ensure threads don't prevent the application from exiting
        thread.start();
    }

    /**
     * Submits a runnable task to a shared thread pool for execution.
     *
     * @param runnable the task to be executed.
     */
    public static void execute(Runnable runnable) {
        executorService.execute(runnable);
    }

    /**
     * Gracefully shuts down the thread pool.
     * Ensures all pending tasks are completed before the application exits.
     */
    public static void shutdown() {
        executorService.shutdown();
    }
}