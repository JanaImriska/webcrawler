package webcrawl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

public record QueueChecker (LinkedBlockingQueue<Runnable> workQueue, ExecutorService executorService, CountDownLatch countDownLatch) implements Runnable {

    @Override
    public void run() {

        if (workQueue.isEmpty()) {
            countDownLatch.countDown();
            executorService.shutdown();
        }
    }
}
