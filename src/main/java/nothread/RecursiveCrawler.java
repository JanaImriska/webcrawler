package nothread;

import demo.Link;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RecursiveAction;

public class RecursiveCrawler extends RecursiveAction {

    private Map<String, Link> resultURLs = Collections.synchronizedMap(new HashMap<>());
    private ConcurrentLinkedQueue<String> queue;

    private int counter;

    public RecursiveCrawler(ConcurrentLinkedQueue<String> queue, int depth) {
        this.queue = queue;
        this.counter = depth;
    }

    @Override
    protected void compute() {
        while(!queue.isEmpty() && counter > 0) {
            lookupLinks(queue.poll());
        }
    }


    public List<String> lookupLinks(String urlString) {

        try {
            URL url = URI.create(urlString).toURL();
            URLConnection connection = url.openConnection();
            connection.connect();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader((InputStream) connection.getContent()));

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

                    if ( !resultURLs.containsKey(link)) {
                        // Add the link to the result list
                        resultURLs.put(link, new Link(link, linkText));
                        System.out.println(link + ":" + linkText);
                        queue.add(link);
                    }
                }

                it.next();
            }
            in.close();
            System.out.println("link print finished");
            counter--;
        }
        catch (Exception e) {
            System.out.println(e);
            System.out.println("Internet Not Connected");
        }

        return resultURLs.keySet().stream().toList();
    }
}
