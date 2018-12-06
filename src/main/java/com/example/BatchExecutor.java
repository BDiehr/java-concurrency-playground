package com.example;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CompletableFuture;


public class BatchExecutor<T>
{
    private final ExecutorService executorService;
    private final BlockingQueue<Future<T>> activeTasks;
    // This lock is used to pause the main worker
    // thread until it is notified of available work to do.
    private final LinkedList<Callable<T>> queuedTasks;
    private final SpinLock availableTaskLock;

    /**
     * BatchExecutor
     *
     * An efficient concurrent task manager.
     * It is not thread-safe. Do not implement as a singleton.
     *
     * @param executorService executor service to submit tasks to.
     * @param numThreads maximum number of threads to utilize at once.
     */
    public BatchExecutor(ExecutorService executorService, final int numThreads)
    {
        this.executorService = executorService;
        this.activeTasks = new LinkedBlockingQueue<>(numThreads);
        this.queuedTasks = new LinkedList<>();
        this.availableTaskLock = new SpinLock();
    }

    /**
     * Invoke all tasks and return results in a blocking manner utilizing
     * the given thread pool to invoke tasks concurrently.
     *
     * @param tasks tasks to invoke
     * @throws InterruptedException interruption exception from invoked tasks
     * @return queue of results in unsorted order
     *         Recommended to copy to a more appropriate structure.
     */
    public Queue<T> invokeAll(final List<Callable<T>> tasks) throws InterruptedException
    {
        this.queuedTasks.clear(); // clear just to be safe, in-case something wrong has occurred in the past.
        this.queuedTasks.addAll(tasks);
        final Queue<T> results = new ConcurrentLinkedQueue<>();
        // While we haven't finished computing all the tasks yet...
        while(hasMoreWork()) {
            // Invokes as many tasks as permitted by available work and capacity
            while (this.activeTasks.remainingCapacity() > 0 && !this.queuedTasks.isEmpty()) {
                invokeTask(this.queuedTasks.pop(), results);
            }
            // If there is more work, wait until a notification of capacity to do more work
            if (hasMoreWork()) {
                availableTaskLock.lock();
            }
        }
        return results;
    }

    private void invokeTask(final Callable<T> task, final Queue<T> results) {
        final CompletableFuture<T> futureTask = new CompletableFuture<>();
        activeTasks.add(futureTask);
        // Initiate our task, and hook the promise into the response.
        executorService.submit(() -> {
            try {
                futureTask.complete(task.call());
            } catch (final Exception ex) {
                futureTask.completeExceptionally(ex);
            }
        });
        futureTask.thenAccept(taskResult -> {
            // NOTE: It's important to add to the results before removing the future from the active task.
            //      with reversed order, there is a race condition between the main thread returning the result
            //      set and addition of the task result to the result set.
            results.add(taskResult);
            activeTasks.remove(futureTask);
            availableTaskLock.unlock();
        });
        // TODO(btd): do something with the exceptionally completed futures.
    }

    private boolean hasMoreWork() {
        return !(this.activeTasks.isEmpty() && this.queuedTasks.isEmpty());
    }

}
