import com.yeshen.appcenter.ETLApplication;
import com.yeshen.appcenter.config.mysql.DataSourceContextHolder;
import com.yeshen.appcenter.domain.enums.SupportDatasourceEnum;
import com.yeshen.appcenter.domain.vo.response.BannerVO;
import com.yeshen.appcenter.repository.mysql.BannerDAO;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Date 2022/07/11  16:49
 * author  by HuBingKuan
 */
@SpringBootTest(classes = ETLApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class MybatisTest {

    @Resource
    private SqlSessionFactory sqlSessionFactory;

    @Test
    public void cursorQuery() {
        DataSourceContextHolder.setDatabaseHolder(SupportDatasourceEnum.DEV_TW);
        try (SqlSession sqlSession = sqlSessionFactory.openSession()){
            final Cursor<BannerVO> list2 = sqlSession.getMapper(BannerDAO.class).getBannerList2();
            list2.forEach(System.out::println);
        }
    }

    @Test
    public void selenium() {
        // get current url
        //String currentUrl = driver.getCurrentUrl();
        //  添加cookie
        //driver.manage().addCookie(new Cookie("key","value"));
        // 按下浏览器的后退按钮
        //driver.navigate().back();
        // 按下浏览器的前进按钮
        //driver.navigate().forward();
        // 刷新当前页面
        //driver.navigate().refresh();
/*        WebElement textBox = driver.findElement(By.name("my-text"));
        WebElement submitButton = driver.findElement(By.cssSelector("button"));
        textBox.sendKeys("Selenium");
        submitButton.click();
        WebElement message = driver.findElement(By.id("message"));
        String value = message.getText();*/

        /*        ChromeOptions options = new ChromeOptions();
        options.setBinary("D:/谷歌浏览器/Google/Chrome/Application/chrome.exe");
        //通过配置参数禁止data;的出现,不会弹出浏览器，默认是后台静默运行
        options.addArguments("--headless","--disable-gpu");
        //注意 第二个参数 改为你第二步下载 chromedriver.exe 所放在的路径
        System.setProperty("webdriver.chrome.driver", "D:/chromedriver/chromedriver.exe");
        //创建浏览器窗口
        WebDriver driver = new ChromeDriver(options);

        driver.get("https://www.baidu.com");
        //延迟5秒,动态网站数据填充比较慢，需要延迟才可以拿到数据
        Thread.sleep(5000);
        //拿到页面的数据
        String html=driver.getPageSource();
        System.out.println("The testing page title is: " + driver.getTitle());
        //将字符串变成document对象来获取某个节点的数据
        Document document= Jsoup.parse(html);
        System.out.println(document);
        //关闭浏览器窗口
        driver.quit();*/
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        WebDriver driver = new ChromeDriver();
        try {
            driver.get("https://www.taptap.io/top/download");
            // 等待页面加载元素
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            // 最大化窗口
            driver.manage().window().maximize();
            // get title
            String title = driver.getTitle();
            // 查找元素
            //By.cssSelector("[name='q']")
            // By.cssSelector("#fruits .tomatoes")  #代表id  .代表class
            //driver.findElement(By.className("className"));
            //WebElement message = driver.findElement(By.id("message"));
            //WebElement textBox = driver.findElement(By.name("my-text"));
            //WebElement submitButton = driver.findElement(By.cssSelector("button"));
            // 对元素执行操作
            //textBox.sendKeys("Selenium");
            //submitButton.click();
            // 获取元素信息
            //String value = message.getText();
            WebElement element = driver.findElement(By.xpath("(//p/span)[1]"));
            new Actions(driver)
                    .click(element)
                    .perform();
            //(//div/a[contains(@class,'tap-router tap-app-title__wrap')])[1]
            //driver.manage().window().setPosition();
            // 找到对应的alert弹框/confirm弹框
            //Alert alert = driver.switchTo().alert();
            // 取消弹框
            //alert.dismiss();
            // 切换窗口
            //driver.switchTo().window();
            // 关闭窗口
            //driver.close();
        }finally {
            driver.quit();
        }
    }
}