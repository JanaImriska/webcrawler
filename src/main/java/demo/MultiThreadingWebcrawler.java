package demo;

import ch.qos.logback.core.util.TimeUtil;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

public class MultiThreadingWebcrawler {

    public static void main(String[] args) {

        String startingPoint = "https://orf.at/";
        System.out.println(startingPoint);
        System.out.println("------------------------------------------------");
        long startTime = System.currentTimeMillis();
        List<String> lookupedLinks = lookupLinks(startingPoint);
        List<String> sortedList = lookupedLinks.stream().sorted().collect(Collectors.toList());
        long endTime = System.currentTimeMillis();
        System.out.println("Link look up took " + (endTime - startTime)/1000 +
                " seconds.");

        System.out.println(sortedList);
    }

    public static List<String> lookupLinks(String startingPoint) {

        Set<String> broken = Collections.synchronizedSet(new HashSet<>());
        Map<String, Link> results = Collections.synchronizedMap(new HashMap<>());
        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println(Integer.toString(processors) + " processor"
                + (processors != 1 ? "s are " : " is ")
                + "available");
        results.put(startingPoint, new Link(startingPoint, startingPoint));

        int nThreads = 4;
        LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
        ExecutorService executorService = new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                workQueue);

        RecursiveMTWebCrawler recursiveMTWebCrawler = new RecursiveMTWebCrawler(startingPoint, results, 8, broken, executorService);
        executorService.submit(recursiveMTWebCrawler);
        try {
            TimeUnit.SECONDS.sleep(10);

            while (!workQueue.isEmpty()) {
                System.out.println("checking queue. queue size:  " + workQueue.size());
                TimeUnit.SECONDS.sleep(10);
            }

            executorService.shutdown();
        } catch (InterruptedException e ) {
            System.out.println(e);
        }

        return results.keySet().stream().toList();
    }


}