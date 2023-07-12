import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class WebIndexer {
    private static final String INDEX_FILE_PATH = "index.json";
    private static Set<Element> visitedElements = new HashSet<>();
    private static Indexer indexer = new Indexer();

    public static void main(String[] args) {
        String url = "https://www.wikipedia.org/";

        try {
            // Fetch the HTML content from the URL
            Document doc = Jsoup.connect(url).get();

            // Extract information from the layered div elements
            extractFromDivs(doc);

            // Save the index to a file
            indexer.saveIndex(INDEX_FILE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void extractFromDivs(Element element) {
        // Add the current element to the visited set
        visitedElements.add(element);

        // Process the content and update the index
        processContent(element);

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

    private static void processContent(Element element) {
        // Extract links
        Elements links = element.select("a[href]");
        for (Element link : links) {
            String href = link.attr("href");
            indexer.updateIndex(href, element.baseUri());
        }

        // Extract text
        String text = element.text();
        if (!text.isEmpty()) {
            indexer.updateIndex(text, element.baseUri());
        }

        // Extract images
        Elements images = element.select("img[src]");
        for (Element image : images) {
            String src = image.attr("src");
            String alt = image.attr("alt");
            indexer.updateIndex(src, element.baseUri());
            indexer.updateIndex(alt, element.baseUri());
        }
    }

    private static class Indexer {
        private Map<String, Set<String>> index = new HashMap<>();

        public void updateIndex(String keyword, String url) {
            // Add the URL to the index for the given keyword
            index.computeIfAbsent(keyword, k -> new HashSet<>()).add(url);
        }

        public void saveIndex(String filePath) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                // Convert the index to JSON and write to file
                Gson gson = new Gson();
                String indexJson = gson.toJson(index);
                writer.write(indexJson);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
