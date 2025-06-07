package demo;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.*;

public class ComputeClass {

    public static void main(String[] args) {

        String startingPoint = "https://www.geeksforgeeks.org/";
        System.out.println(startingPoint);
        System.out.println("------------------------------------------------");
        Set<String> lookupedLinks = lookupLinks(startingPoint);
        System.out.println(lookupedLinks);

//        Set<Link> links = Collections.synchronizedSet(lookupedLinks);
//        List<Link> sortedLinks = new ArrayList<>(links);
//        sortedLinks.sort(new Comparator<Object>() {
//            @Override
//            public int compare(Object o1, Object o2) {
//                return ((Link) o1).getTitle().compareTo(((Link) o2).getTitle());
//            }
//        });
//        System.out.println(sortedLinks);
    }

    public static Set<String> lookupLinks(String startingPoint) {

        Set<String> resultURls = Collections.synchronizedSet(new HashSet<>());
        ConcurrentLinkedQueue<Link> toComputeQueue = new ConcurrentLinkedQueue<>();
        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println(Integer.toString(processors) + " processor"
                + (processors != 1 ? "s are " : " is ")
                + "available");
        toComputeQueue.add(new Link(startingPoint, startingPoint));
        RecursiveLinkLookup computeClass = new RecursiveLinkLookup(toComputeQueue, resultURls);

        computeClass.lookForLinksIn(startingPoint);
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        long startTime = System.currentTimeMillis();
        forkJoinPool.invoke(computeClass);
        long endTime = System.currentTimeMillis();

        System.out.println("Link look up took " + (endTime - startTime) +
                " milliseconds.");

        return resultURls;
    }


}