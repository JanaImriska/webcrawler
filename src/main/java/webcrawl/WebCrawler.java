package webcrawl;

import org.apache.logging.log4j.util.Strings;

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
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class WebCrawler implements Runnable {

    private static String baseRef;

    private final ExecutorService executorService;
    private ConcurrentHashMap<String, Link> resultURLs;

    private Set<String> brokenLinks;

    private String start;
    private int counter;


    public WebCrawler(String start, ConcurrentHashMap<String, Link> resultURLs, int counter, Set<String> brokenLinks, ExecutorService executorService) {
        this.start = start;
        this.resultURLs = resultURLs;
        this.counter = counter;
        this.brokenLinks = brokenLinks;
        this.executorService = executorService;
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
                new InputStreamReader((InputStream) connection.getContent()))) {

            HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
            HTMLDocument defaultDocument = (HTMLDocument) htmlEditorKit.createDefaultDocument();
            htmlEditorKit.read(in, defaultDocument, 0);

            HTMLDocument.Iterator it = defaultDocument.getIterator(HTML.Tag.A);
            counter--;
            while (it.isValid()) {
                SimpleAttributeSet s = (SimpleAttributeSet) it.getAttributes();

                String link = (String) s.getAttribute(HTML.Attribute.HREF);
                String title = defaultDocument.getText(it.getStartOffset(), it.getEndOffset() - it.getStartOffset());
                if (link != null) {
                    if (!link.startsWith("http")) {
                        link = urlString + link.substring(1);
                    }

                    if (link.startsWith(WebCrawler.baseRef) && !brokenLinks.contains(link) && !resultURLs.containsKey(link)) {
                        // Add the link to the result list
                        resultURLs.put(link, new Link(title, link));
                        executorService.submit(new WebCrawler(link, resultURLs, counter, brokenLinks, executorService));
                    }
                }
                it.next();
            }

        } catch (Exception e) {
            brokenLinks.add(urlString);
            return resultURLs.keySet().stream().toList();
        }

        return resultURLs.keySet().stream().toList();
    }

    @Override
    public void run() {

        if (Strings.isNotBlank(start) && counter > 0) {
            lookupLinks(start);
        }
//        System.out.println(" results: " + resultURLs.size() + " counter: " + counter + " brokenLinks: " + brokenLinks.size());
    }


    public static void setBaseRef(String baseRef) {
        if (WebCrawler.baseRef == null) {
            WebCrawler.baseRef = baseRef;
        }
    }


}
