package demo;


import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public class WebcrawlerApplication {

	public static void main(String[] args) {
		System.out.println("Hello World");
		try {
			URL url = URI.create("https://www.geeksforgeeks.org/").toURL();
			URLConnection connection = url.openConnection();
			connection.connect();

			System.out.println("Connection Successful");
		}
		catch (Exception e) {
			System.out.println("Internet Not Connected");
		}

	}

}
