import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * Created by lisheng on 17-4-20.
 */
public class test {

    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.connect("http://shouji.baidu.com/").get();

        Element link = doc.select("a").first();
        String relHref = link.attr("href"); // == "/"
        String absHref = link.attr("abs:href"); // "http://www.open-open.com/"
    }
}
