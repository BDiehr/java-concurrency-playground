package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class TaskUtil {
    private final static Random random = new Random();


    static List<Callable<String>> getTasks(final int numTasks)  {
        return getTasks(numTasks, 0);
    }

    static void runCPU(final int iterations) {
        String brick = "";
        for (int i = 0; i < iterations; i++) {
            brick += " - - - abc - - - - - - - - - - - - - - -";
        }
    }

    static List<Callable<String>> getTasks(final int numTasks, final int randomAmnt) {
        final List<Callable<String>> tasks = new ArrayList<>();
        for (int i = 0; i < numTasks; i++) {
            tasks.add(() -> {
                runCPU(150);
                // Fake blocking.
                Thread.sleep(10 + random.nextInt(randomAmnt));
                runCPU(150);
                return "complete";
            });
        }
        return tasks;
    }
}

