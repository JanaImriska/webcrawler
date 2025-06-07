package demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

public record QueueChecker (LinkedBlockingQueue<Runnable> workQueue, ExecutorService executorService) implements Runnable {

    @Override
    public void run() {
        System.out.println("checking queue. queue size:  " + workQueue.size());
        if (workQueue.isEmpty()) {
            executorService.shutdown();
        }
    }
}
