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

        Set<String> resultURls = Collections.synchronizedSet(new HashSet<>());
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

        RecursiveMTWebCrawler recursiveMTWebCrawler = new RecursiveMTWebCrawler(startingPoint, results, 6, resultURls, executorService);
        executorService.submit(recursiveMTWebCrawler);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new QueueChecker(workQueue, executorService), 30, 30, TimeUnit.SECONDS);

        return results.keySet().stream().toList();
    }


}