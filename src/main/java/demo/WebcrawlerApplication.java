package demo;


import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;

public class WebcrawlerApplication {

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
		RecursiveWebCrawler recursiveWebCrawler = new RecursiveWebCrawler(queue,3);
		long startTime = System.currentTimeMillis();
		recursiveWebCrawler.compute();
		long endTime = System.currentTimeMillis();
		System.out.println("Link look up took " + (endTime - startTime) +
				" milliseconds.");

		Set<String> urls = Collections.synchronizedSet(new HashSet<>());


		return urls;
	}

}
