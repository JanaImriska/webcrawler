package demo;

import org.apache.logging.log4j.util.Strings;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RecursiveAction;

public class RecursiveMTWebCrawler extends RecursiveAction {

    private Map<String, Link> resultURLs;
    private ConcurrentLinkedQueue<String> queue;

    private Set<String> brokenLinks;

    private String start;
    private int counter;


    public RecursiveMTWebCrawler(ConcurrentLinkedQueue<String> queue, String start, Map<String, Link> resultURLs, int counter, Set<String> brokenLinks) {
        this.queue = queue;
        this.start = start;
        this.resultURLs = resultURLs;
        this.counter = counter;
        this.brokenLinks = brokenLinks;
    }

    @Override
    protected void compute() {
        if (Strings.isNotBlank(start)) {
            lookupLinks(start);
        }
        while(!queue.isEmpty() && counter > 0) {
            counter--;
            RecursiveMTWebCrawler recursiveMTWebCrawler = new RecursiveMTWebCrawler(queue, queue.poll(), resultURLs, counter, brokenLinks);
            recursiveMTWebCrawler.compute();
        }
    }


    public List<String> lookupLinks(String urlString) {

        URL url = null;
        URLConnection connection = null;
        try {
            url = URI.create(urlString).toURL();
            connection = url.openConnection();
            connection.connect();
        } catch (MalformedURLException e) {
            brokenLinks.add(urlString);
            return resultURLs.keySet().stream().toList();
        } catch (IOException e) {
            brokenLinks.add(urlString);
            return resultURLs.keySet().stream().toList();
        }

        try (BufferedReader in = new BufferedReader(
                     new InputStreamReader((InputStream) connection.getContent()))){

            String content = "";
            String current;
            HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
            HTMLDocument defaultDocument = (HTMLDocument)htmlEditorKit.createDefaultDocument();
            htmlEditorKit.read(in,defaultDocument,0);
            //find baseHref
            HTMLDocument.Iterator it = defaultDocument.getIterator(HTML.Tag.A);
            while (it.isValid()) {
                SimpleAttributeSet s = (SimpleAttributeSet)it.getAttributes();

                String link = (String)s.getAttribute(HTML.Attribute.HREF);
                String linkText = defaultDocument.getText(it.getStartOffset(), it.getEndOffset() - it.getStartOffset());
                if(link != null) {
                    if (!link.startsWith("http")) {
                        link = urlString + link.substring(1);
                    }

                    if ( !brokenLinks.contains(link) && !resultURLs.containsKey(link)) {
                        // Add the link to the result list
                        resultURLs.put(link, new Link(link, linkText));
                        queue.add(link);
                    }
                }

                it.next();
            }
            in.close();
            System.out.println("in queue: " + queue.size() + " results: " + resultURLs.size() + " counter: " + counter + " brokenLinks: " + brokenLinks.size());
        } catch (Exception e) {
            brokenLinks.add(urlString);
            return resultURLs.keySet().stream().toList();
        }


        return resultURLs.keySet().stream().toList();
    }
}
