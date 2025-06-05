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
import java.util.HashSet;
import java.util.Set;

public class WebcrawlerApplication {

	public static void main(String[] args) {
		System.out.println("Hello World");
		try {
			URL url = URI.create("https://www.geeksforgeeks.org/").toURL();
			URLConnection connection = url.openConnection();
			connection.connect();

			BufferedReader in = new BufferedReader(
					new InputStreamReader((InputStream) connection.getContent()));
			String content = "";
			String current;
			Set<String> urls = new HashSet<>();
			HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
			HTMLDocument defaultDocument = (HTMLDocument)htmlEditorKit.createDefaultDocument();
			htmlEditorKit.read(in,defaultDocument,0);
			HTMLDocument.Iterator it = defaultDocument.getIterator(HTML.Tag.A);
			while (it.isValid()) {
				SimpleAttributeSet s = (SimpleAttributeSet)it.getAttributes();

				String link = (String)s.getAttribute(HTML.Attribute.HREF);
				if (link != null) {
					// Add the link to the result list
					System.out.println(link);
					//System.out.println("link print finished");
					urls.add(link);
				}
				//System.out.println(link);
				it.next();
			}

			while((current = in.readLine()) != null) {
				System.out.println(current);
				content += current;
			}
			//System.out.println(content);

			System.out.println("Connection Successful");
		}
		catch (Exception e) {
			System.out.println("Internet Not Connected");
		}

	}

}
