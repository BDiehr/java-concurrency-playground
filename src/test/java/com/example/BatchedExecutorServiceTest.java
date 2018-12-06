package com.example;

import com.github.noconnor.junitperf.JUnitPerfRule;
import com.github.noconnor.junitperf.JUnitPerfTest;
import com.github.noconnor.junitperf.JUnitPerfTestRequirement;
import com.github.noconnor.junitperf.reporting.providers.ConsoleReportGenerator;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class BatchedExecutorServiceTest {
    final static int NUM_THREADS = 10;

    @Rule
    public JUnitPerfRule perfTestRule = new JUnitPerfRule(new ConsoleReportGenerator());

    private static ExecutorService executorService;

    @BeforeClass
    public static void setUp(){
        executorService = Executors.newFixedThreadPool(NUM_THREADS);
    }

    @JUnitPerfTest(durationMs = 10_000, warmUpMs = 1_000, maxExecutionsPerSecond = 5_000)
    @JUnitPerfTestRequirement(percentiles = "99:22")
    @Test
    public void submitTasks_runsOneTask() {
        int NUM_TASKS = 1;
        BatchedExecutorService<String> executor = new BatchedExecutorService<>(executorService, NUM_THREADS);
        executor.runTasks(TaskUtil.getTasks(NUM_TASKS));
    }


    @JUnitPerfTest(durationMs = 10_000, warmUpMs = 1_000, maxExecutionsPerSecond = 10_000)
    @JUnitPerfTestRequirement(percentiles = "99:130")
    @Test
    public void submitTasks_100000tasks_runs() {
        int NUM_TASKS = 1000;
        BatchedExecutorService<String> executor = new BatchedExecutorService<>(executorService, 10);
        executor.runTasks(TaskUtil.getTasks(NUM_TASKS));
    }

    @JUnitPerfTest(threads = 1, durationMs = 100_000, warmUpMs = 500, maxExecutionsPerSecond = 20_000)
    @JUnitPerfTestRequirement(percentiles = "99:580")
    @Test
    public void submitTasks_1000tasksWithRandom_runs() {
        int NUM_TASKS = 1000;
        BatchedExecutorService<String> executor = new BatchedExecutorService<>(executorService, 10);
        executor.runTasks(TaskUtil.getTasks(NUM_TASKS, 100));
    }

    @JUnitPerfTest(durationMs = 2_000, warmUpMs = 1_000, maxExecutionsPerSecond = 10_000)
    // @JUnitPerfTestRequirement(percentiles = "99:580")
    @Test
    public void submitTasks_100000tasksWithRandom_runs() {
        int NUM_TASKS = 1000;
        BatchedExecutorService<String> executor = new BatchedExecutorService<>(executorService, NUM_THREADS);
        executor.runTasks(TaskUtil.getTasks(NUM_TASKS, 10));
    }
}
