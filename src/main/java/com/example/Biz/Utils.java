package com.example.Biz;

import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

public class Utils {
    private Utils() {}

    private static final int CAPACITY = 5;
    private static BlockingQueue<String> queue = new LinkedBlockingQueue<>(CAPACITY);

    public static String computeSequentially(int numObjects, int delayMs) {
       queue.offer(Integer.toString(numObjects));
       IntStream
           .range(0, numObjects)
           .forEach(i -> Utils.delay(delayMs));
       return "OK";
    }

    public static String computeWithForkJoin(int numObjects, int delayMs) {
        IntStream
            .range(0, numObjects)
            .parallel()
            .forEach(i -> Utils.delay(delayMs));
        return "OK";
    }

    public static String writeSequntially(int numObjects, int lines) {
        IntStream
            .range(0, numObjects)
            .forEach(i -> Utils.write(lines));
        return "OK";
    }


    public static String writeWithForkJoin(int numObjects, int lines) {
        IntStream
            .range(0, numObjects)
            .parallel()
            .forEach(i -> Utils.write(lines));
        return "OK";
    }


    public static String computeWithMultiThreaded(int numObjects, int delayMs) {
        throw new Error("not implemented");
    }

    private static void write(int lines) {
        try {
            FileWriter fileWriter = new FileWriter("/tmp/" + UUID.randomUUID());
            String content = "";
            for (int i = 0; i < lines; i++) {
                content += "some line of junk that no one really cares about!";
            }
            fileWriter.write(content);
            Thread.sleep(1000);
            fileWriter.close();
        }
        catch (IOException | InterruptedException ex) {
            throw new Error("Failed to write to file!");
        }
    }

    private static void delay(int delayMs) throws Error {
        try {
            Thread.sleep(delayMs);
        }
        catch (InterruptedException ex) {
            throw new Error("a thread has unexpectedly crashed");
        }
    }
}
