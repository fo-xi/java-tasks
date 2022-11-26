package ru.mail.polis.homework.concurrency.executor;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Executor;

/**
 * Нужно сделать свой executor с ленивой инициализацией потоков до какого-то заданного предела.
 * Ленивая инициализация означает, что если вам приходит раз в 5 секунд задача, которую вы выполняете 2 секунды,
 * то вы создаете только один поток. Если приходит сразу 2 задачи - то два потока.  То есть, если приходит задача
 * и есть свободный запущенный поток - он берет задачу, если такого нет, то создается новый поток.
 *
 * Задачи должны выполняться в порядке FIFO
 * Потоки после завершения выполнения задачи НЕ умирают, а ждут.
 *
 * Max 10 баллов
 */
public class SimpleExecutor implements Executor {

    private BlockingQueue<Runnable> commands = new LinkedBlockingDeque<>();
    private ArrayList<MyThread> threads;
    private volatile boolean isRunning = true;
    private int maxThreadCount;
    private AtomicInteger size = new AtomicInteger();


    public SimpleExecutor(int maxThreadCount) {
        this.maxThreadCount = maxThreadCount;
        threads = new ArrayList<>(maxThreadCount);
    }

    /**
     * Ставит задачу в очередь на исполнение, если надо -- создает новый поток.
     * 8 баллов
     */
    @Override
    public void execute(Runnable command) {
        if (!isRunning) {
            throw new RejectedExecutionException();
        }
        synchronized (threads) {
            if (getLiveThreadsCount() < maxThreadCount && isAllBusy()) {
                MyThread thread = new MyThread();
                threads.add(thread);
                thread.start();
                size.incrementAndGet();
            }
        }
        commands.add(command);
    }

    /**
     * Дает текущим задачам выполниться. Добавление новых - бросает RejectedExecutionException
     * 1 балл за метод
     */
    public void shutdown() {
        isRunning = false;
    }

    /**
     * Прерывает текущие задачи. При добавлении новых - бросает RejectedExecutionException
     * 1 балла за метод
     */
    public void shutdownNow() {
        isRunning = false;
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    /**
     * Должен возвращать количество созданных потоков.
     */
    public int getLiveThreadsCount() {
        return size.get();
    }

    public boolean isAllBusy() {
        for (MyThread thread : threads) {
            if (!thread.getBusy()) {
                return false;
            }
        }
        return true;
    }

    private class MyThread extends Thread {

        private volatile boolean isBusy = true;

        @Override
        public void run() {
            while (!commands.isEmpty() || isRunning) {
                try {
                    isBusy = false;
                    Runnable command = commands.take();
                    isBusy = true;
                    command.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public boolean getBusy() {
            return isBusy;
        }

    }
}
