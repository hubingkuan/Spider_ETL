import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author by HuBingKuan
 * @Date 2023/04/13  13:49
 */
public class JsoupTest {
    @Test
    void name() throws IOException {
        //连接到目标网址
        Document doc = Jsoup.connect("https://www.tiktok.com/@therock")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
                .get();
        //输出页面源代码
        System.out.println(doc.outerHtml());
    }

    @Test
    void test2() throws IOException {
        Document doc = Jsoup.connect("https://www.baidu.com").get();
        //输出页面源代码
        System.out.println(doc.outerHtml());
    }
}