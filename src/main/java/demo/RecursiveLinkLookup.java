package demo;

import javax.swing.text.BadLocationException;
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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RecursiveAction;

public class RecursiveLinkLookup extends RecursiveAction {
    private ConcurrentLinkedQueue<Link> toComputeUrls;
    private Set<String> results;
    private static int counter = 0;

    public RecursiveLinkLookup(ConcurrentLinkedQueue<Link> toComputeUrls, Set<String> results) {
        this.results = results;
        this.toComputeUrls = toComputeUrls;
    }

    protected void lookForLinksIn(String startingPoint) {

        URI uri = URI.create(startingPoint);
        try {
            URL url = uri.toURL();
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect(); //is this necessary?

            HTMLEditorKit editorKit = new HTMLEditorKit();
            HTMLDocument document = (HTMLDocument) editorKit.createDefaultDocument();
            BufferedReader in = new BufferedReader(new InputStreamReader((InputStream) urlConnection.getContent()));
            editorKit.read(in, document, 0);
            HTMLDocument.Iterator iterator = document.getIterator(HTML.Tag.A);

            while (iterator.isValid()) {
                SimpleAttributeSet sa = (SimpleAttributeSet) iterator.getAttributes();
                String link = (String) sa.getAttribute(HTML.Attribute.HREF);
                String linkText = (String) sa.getAttribute(HTML.Attribute.LINK);
                if (link != null && testConnection(link)) {
                    Link newLink = new Link(link, linkText);
                    if (!results.contains(link)) {
                        results.add(link);
                        toComputeUrls.add(newLink);
                        System.out.println(newLink.toString());
                    }
                }
            }


        } catch (IOException | BadLocationException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean testConnection(String link) {
//        try {
//            URI uri = URI.create(link);
//            URL url = uri.toURL();
//            URLConnection urlConnection = url.openConnection();
//            urlConnection.connect();
//            return true;
//        } catch (IOException e) {
//            return false;
//        }
        return true;
    }


    @Override
    protected void compute() {
        if (toComputeUrls.size() == 1) {
            lookForLinksIn(toComputeUrls.poll().getUrl());
        } else {
            if (counter < 3) {
                while (!toComputeUrls.isEmpty()) {
                    ConcurrentLinkedQueue<Link> toComputeQueue = new ConcurrentLinkedQueue<>();
                    toComputeQueue.add(toComputeUrls.poll());
                    invokeAll(new RecursiveLinkLookup(toComputeQueue, results));
                }
                counter++;
            }
        }
    }
}
