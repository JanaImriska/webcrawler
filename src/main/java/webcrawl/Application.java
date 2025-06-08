package webcrawl;

import java.net.URI;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.*;

public class Application {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Type a valid URL to scan: ");
        String startingPoint = scanner.nextLine();
        if (!isValidURL(startingPoint)) {
            System.out.println("The URL is not valid, a default URL will be used.");
            startingPoint = "https://crawler-test.com";
        }
        System.out.println("valid URL:" + startingPoint);
        WebCrawler.setBaseRef(startingPoint);
        System.out.println("------------------------------------------------");
        long startTime = System.currentTimeMillis();
        List<Link> lookupedLinks = lookupLinks(startingPoint);
        long endTime = System.currentTimeMillis();
        System.out.println("Web Crawl took " + (endTime - startTime)/1000 + " seconds.");

        System.out.println("Sorted list by link title: ");
        lookupedLinks.stream().sorted(Comparator.comparing(o -> ((Link) o).title().toLowerCase())).forEach(System.out::println);
    }

    private static boolean isValidURL(String urlString) {
        try {
            URLConnection connection = URI.create(urlString).toURL().openConnection();
            connection.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static List<Link> lookupLinks(String startingPoint) {

        int processors = Runtime.getRuntime().availableProcessors();

        LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
        ExecutorService executorService = new ThreadPoolExecutor(processors, processors,
                0L, TimeUnit.MILLISECONDS,
                workQueue);

        ConcurrentHashMap<String, Link> results = new ConcurrentHashMap();
        Set<String> broken = ConcurrentHashMap.newKeySet();;
        results.put(startingPoint, new Link(startingPoint, startingPoint));
        WebCrawler webCrawler = new WebCrawler(startingPoint, results, 8, broken, executorService);
        executorService.submit(webCrawler);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        CountDownLatch onOff = new CountDownLatch(1);
        scheduledExecutorService.scheduleAtFixedRate(new QueueChecker(workQueue, executorService, onOff), 10, 10, TimeUnit.SECONDS);


        try {
            onOff.await();
            executorService.awaitTermination(5, TimeUnit.MINUTES);
            scheduledExecutorService.shutdown();
            scheduledExecutorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e ) {
            System.out.println(e);
        }

        return results.values().stream().toList();
    }


}