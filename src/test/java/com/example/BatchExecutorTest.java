package com.example;

import com.github.noconnor.junitperf.JUnitPerfRule;
import com.github.noconnor.junitperf.JUnitPerfTest;
import com.github.noconnor.junitperf.JUnitPerfTestRequirement;
import com.github.noconnor.junitperf.reporting.providers.ConsoleReportGenerator;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class BatchExecutorTest {
    final static int NUM_THREADS = 10;

    @Rule
    public JUnitPerfRule perfTestRule = new JUnitPerfRule(new ConsoleReportGenerator());
    private static ExecutorService executorService;

    @BeforeClass
    public static void setUp() {
        executorService = Executors.newFixedThreadPool(NUM_THREADS);
    }

    @JUnitPerfTest(threads = 1, durationMs = 100_000, warmUpMs = 500, maxExecutionsPerSecond = 20_000)
    // @JUnitPerfTestRequirement(percentiles = "99:20")
    @Test
    public void submitTasks_runsOneTask() throws InterruptedException {
        int NUM_TASKS = 1000;
        BatchExecutor<String> executor = new BatchExecutor<>(executorService, 10);
        Collection<String> results = executor.invokeAll(TaskUtil.getTasks(NUM_TASKS, 100));
        assertEquals(NUM_TASKS, results.size());
    }


    @JUnitPerfTest(threads = 1, durationMs = 10_000, warmUpMs = 1_000, maxExecutionsPerSecond = 10_000)
    @JUnitPerfTestRequirement(percentiles = "99:118")
    @Test
    public void submitTasks_1000tasks_runs() throws InterruptedException{
        int NUM_TASKS = 20;
        BatchExecutor<String> executor = new BatchExecutor<>(executorService, 5);
        Collection<String> results = executor.invokeAll(TaskUtil.getTasks(NUM_TASKS, 1));
        assertFalse(results.isEmpty());
        assertEquals(NUM_TASKS, results.size());
    }

    @JUnitPerfTest(durationMs = 10_000, warmUpMs = 1_000, maxExecutionsPerSecond = 10_000)
    @JUnitPerfTestRequirement(percentiles = "99:400,90:380")
    @Test
    public void submitTasks_1000tasksWithRandom_runs() throws InterruptedException {
        int NUM_TASKS = 100;
        BatchExecutor<String> executor = new BatchExecutor<>(executorService, 100);
        Collection<String> results = executor.invokeAll(TaskUtil.getTasks(NUM_TASKS, 100));
        assertFalse(results.isEmpty());
        assertEquals(NUM_TASKS, results.size());
    }

    @JUnitPerfTest(durationMs = 100_000, warmUpMs = 1_000, maxExecutionsPerSecond = 100_000)
    // @JUnitPerfTestRequirement(percentiles = "99:400,90:380")
    @Test
    public void submitTasks_100000tasksWithRandom_runs() throws InterruptedException {
        int NUM_TASKS = 10000;
        BatchExecutor<String> executor = new BatchExecutor<>(executorService, NUM_THREADS);
        Collection<String> results = executor.invokeAll(TaskUtil.getTasks(NUM_TASKS, 100));
        assertFalse(results.isEmpty());
        assertEquals(NUM_TASKS, results.size());
    }
}
