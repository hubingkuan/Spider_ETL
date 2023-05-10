/**
 * Date 2023/02/01  17:08
 * author  by HuBingKuan
 */
public class StringUtils {
    public static void main(String[] args) {
        String currentUrl = "https://www.tiktok.comkyliejenner";
        String substring = currentUrl.substring(currentUrl.indexOf("/@") + 2);
        System.out.println(substring);
    }
}