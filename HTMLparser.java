import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class HTMLParser {
    private static final String OUTPUT_FILE_PATH = "output.txt";
    private static Set<Element> visitedElements = new HashSet<>();

    public static void main(String[] args) {
        String url = "https://www.wikipedia.org/";

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
        // Add the current element to the visited set
        visitedElements.add(element);

        // Extract links
        Elements links = element.select("a[href]");
        for (Element link : links) {
            String href = link.attr("href");
            writeToFile("Link: " + href);
        }

        // Extract text
        String text = element.text();
        if (!text.isEmpty()) {
            writeToFile("Text: " + text);
        }

        // Extract images
        Elements images = element.select("img[src]");
        for (Element image : images) {
            String src = image.attr("src");
            String alt = image.attr("alt");
            writeToFile("Image source: " + src);
            writeToFile("Image alt: " + alt);
        }

        // Recursively process child div elements
        Elements divs = element.select("div");
        for (Element div : divs) {
            if (!visitedElements.contains(div)) {
                extractFromDivs(div);
            }
        }

        // Remove the current element from the visited set
        visitedElements.remove(element);
    }

    private static void writeToFile(String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH, true))) {
            writer.write(content);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
