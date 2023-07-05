import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.MalformedURLException;
import java.net.URL;

public class WebCrawler {
    private static Map<String, List<String>> index = new HashMap<>();
    private static String targetWebsite; // Original inputted website URL

    public static void main(String[] args) {

        // == "/"
        // "http://jsoup.org/"

        Document doc = null;

        try{
            doc = Jsoup.connect("https://www.youtube.com").get();
        } catch (Exception e){
            System.out.println(e);
        }

        Element link = doc.select("a").first();
        String relHref = link.attr("href");
        String absHref = link.attr("abs:href");

        String url = absHref;  // This should be replaced with your URL
        targetWebsite = absHref;
        try {
            new URL(url);
            // If we got here, the URL is correct
            Document docu = Jsoup.connect(url).get();
            String title = docu.title();
            System.out.println("Title: " + title);
        } catch (MalformedURLException e) {
            System.err.println("The URL '" + url + "' is not valid");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //"https://en.wikipedia.org/wiki/Main_Page"

        crawl("https://www.youtube.com");

        

        // Print the indexed data
        System.out.println("Indexed Data:");
        for (Map.Entry<String, List<String>> entry : index.entrySet()) {
            String term = entry.getKey();
            List<String> urls = entry.getValue();

            System.out.println("Term: " + term);
            System.out.println("URLs:");
            for (String urlFound : urls) {
                System.out.println(urlFound);
            }
            System.out.println("-------------------");
        }
    }

    private static void crawl(String url) {
        try {
            // Fetch the web page using Jsoup
            Document doc = Jsoup.connect(url).get();

            // Get the HTML content
            String htmlContent = doc.html();
            indexContent(url, htmlContent);

            // Get all the links on the page
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String linkUrl = link.attr("href");

                // Convert relative URLs to absolute URLs
                URL absoluteUrl = new URL(new URL(url), linkUrl);
                String absoluteUrlString = absoluteUrl.toString();

                if (absoluteUrlString.startsWith(targetWebsite)) {
                    crawl(absoluteUrlString);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("An IOException Occured");
        } catch (Exception e){
            e.printStackTrace();
            System.out.println(e);
            System.out.println("Another Exception Occured");
        }
    }

    private static void indexContent(String url, String content) {
        // Split the content into terms (you may need more sophisticated tokenization)
        String[] terms = content.split("\\s+");

        // Index each term with the corresponding URL
        for (String term : terms) {
            term = term.toLowerCase();
            if (!index.containsKey(term)) {
                index.put(term, new ArrayList<>());
            }
            List<String> urls = index.get(term);
            if (!urls.contains(url)) {
                urls.add(url);
            }
        }
        
    }
}
