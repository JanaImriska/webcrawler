package demo;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class MultiThreadingWebcrawler {

    public static void main(String[] args) {

        String startingPoint = "https://orf.at/";
        System.out.println(startingPoint);
        System.out.println("------------------------------------------------");
        List<String> lookupedLinks = lookupLinks(startingPoint);

        List<String> sortedList = lookupedLinks.stream().sorted().collect(Collectors.toList());

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
        RecursiveMTWebCrawler recursiveMTWebCrawler = new RecursiveMTWebCrawler(queue, startingPoint, results, 8, resultURls);

        ForkJoinPool forkJoinPool = new ForkJoinPool();

        long startTime = System.currentTimeMillis();
        forkJoinPool.invoke(recursiveMTWebCrawler);
        long endTime = System.currentTimeMillis();

        System.out.println("Link look up took " + (endTime - startTime) +
                " milliseconds.");

        return results.keySet().stream().toList();
    }


}