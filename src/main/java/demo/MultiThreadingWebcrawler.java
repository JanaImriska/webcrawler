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
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println(Integer.toString(processors) + " processor"
                + (processors != 1 ? "s are " : " is ")
                + "available");
        results.put(startingPoint, new Link(startingPoint, startingPoint));

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        RecursiveMTWebCrawler recursiveMTWebCrawler = new RecursiveMTWebCrawler(queue, startingPoint, results, 6, resultURls, executorService);
        executorService.submit(recursiveMTWebCrawler);
        try {
            executorService.awaitTermination(5, TimeUnit.MINUTES);
            System.out.println("unprocessed queue " + queue.size());
            executorService.shutdown();
        } catch (InterruptedException e) {
            System.out.println(e);
        }

        return results.keySet().stream().toList();
    }


}