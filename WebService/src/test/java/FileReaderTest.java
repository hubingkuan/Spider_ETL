import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

/**
 * Date 2022/12/28  22:48
 * author  by HuBingKuan
 */
public class FileReaderTest {
    @Test
    void jsonFileParseMap() throws IOException {
        URL resource = FileReaderTest.class.getClassLoader().getResource("lock.lua");
        URI uri = null;
        try {
            uri = resource.toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        byte[] bytes = Files.readAllBytes(Paths.get(uri));
        String data = new String(bytes);
        System.out.println(data);
    }

    @Test
    void mapWriteFile() throws IOException {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("name", "张三");
        map.put("age", 15);
        String json = JSON.toJSONString(map);
        Files.write(Paths.get("a.txt"), json.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void readFileLine() throws URISyntaxException, IOException {
        List<String> allLines = Files.readAllLines(Paths.get(FileReaderTest.class.getClassLoader().getResource("lock.lua").toURI()));
        allLines.forEach(System.out::println);
    }

    @Test
    void printRecursionFile() throws IOException {
        Files.walk(Paths.get("src")).forEach(System.out::println);
    }

    @Test
    void filterFile() throws IOException {
        try (Stream<Path> pathStream = Files.walk(Paths.get("src"))) {
            // 过滤掉文件夹
            pathStream.filter(Files::isRegularFile).filter(e-> {
                try {
                    byte[] bytes = Files.readAllBytes(e);
                    String data = new String(bytes);
                    // 筛选除文件中包含defaultRegion的文件
                    return data.contains("defaultRegion");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                return false;
            }).forEach(System.out::println);
        }


    }
}