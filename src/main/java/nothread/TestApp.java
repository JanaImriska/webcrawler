package nothread;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TestApp {

	public static void main(String[] args) {

		String urlString = "https://orf.at/";
//		String urlString = "https://ecosio.com/en/";
//		String urlString = "https://www.geeksforgeeks.org/";
		System.out.println(urlString);

		System.out.println("------------------------------------------------");
		lookupLinks(urlString);
	}

	public static Set<String> lookupLinks(String urlString) {

		ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
		queue.add(urlString);
		RecursiveCrawler recursiveCrawler = new RecursiveCrawler(queue,3);
		long startTime = System.currentTimeMillis();
		recursiveCrawler.compute();
		long endTime = System.currentTimeMillis();
		System.out.println("Link look up took " + (endTime - startTime) +
				" milliseconds.");

		Set<String> urls = Collections.synchronizedSet(new HashSet<>());


		return urls;
	}

}
