import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class HTMLParser {
    public static void main(String[] args) {
        String url = "http://example.com";

        try {
            // Fetch the HTML content from the URL
            Document doc = Jsoup.connect(url).get();

            // Extract information from the layered div elements
            extractFromDivs(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void extractFromDivs(Element element) {
        // Extract links
        Elements links = element.select("a[href]");
        for (Element link : links) {
            String href = link.attr("href");
            System.out.println("Link: " + href);
        }

        // Extract text
        String text = element.text();
        if (!text.isEmpty()) {
            System.out.println("Text: " + text);
        }

        // Extract images
        Elements images = element.select("img[src]");
        for (Element image : images) {
            String src = image.attr("src");
            String alt = image.attr("alt");
            System.out.println("Image source: " + src);
            System.out.println("Image alt: " + alt);
        }

        // Recursively process child div elements
        Elements divs = element.select("div");
        for (Element div : divs) {
            extractFromDivs(div);
        }
    }
}
