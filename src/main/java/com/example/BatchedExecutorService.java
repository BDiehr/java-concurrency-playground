package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class BatchedExecutorService<T> {
    private final ExecutorService executorService;
    private final int numThreads;

    public BatchedExecutorService(
            final ExecutorService executorService,
            final int numThreads
    ) {
        this.executorService = executorService;
        this.numThreads = numThreads;
    }

    public Collection<T> runTasks(final List<? extends Callable<T>> tasks) {
        int tasksToExecute = tasks.size();
        while (tasksToExecute > 0) {
            List<Callable<T>> batch = new ArrayList<>();
            int tasksToExecuteInBatch = numThreads;
            while (tasksToExecute > 0 && tasksToExecuteInBatch > 0) {
                batch.add(tasks.get(tasksToExecute - 1));
                tasksToExecute -= 1;
                tasksToExecuteInBatch -= 1;
            }
            try {
                List<Future<T>> futures = executorService.invokeAll(batch);
                for (Future<T> future : futures) {
                    future.get();
                }
            } catch (ExecutionException | InterruptedException ex) {

            }
        }
        return new ArrayList<>();
    }
}
