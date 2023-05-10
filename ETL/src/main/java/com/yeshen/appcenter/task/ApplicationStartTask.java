package com.yeshen.appcenter.task;

import com.yeshen.appcenter.domain.constants.SystemConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.logging.Level;

/**
 * Date 2022/1/19/0019
 * author by HuBingKuan
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartTask {
    @Value("${spring.profiles.active}")
    private String activeProfile;

    private final Environment environment;

    private static String driverFilePath;

    private static String extensionFilePath;


    @PostConstruct
    public void init() {
        if ("taptap".equals(environment.getProperty("spider"))) {
            if (SystemConstant.ENV_DEV.equals(activeProfile)) {
                driverFilePath = ApplicationStartTask.class.getClassLoader().getResource("windowDriver/chromedriver.exe").getPath();
                extensionFilePath = ApplicationStartTask.class.getClassLoader().getResource("modheader.crx").getPath();
            } else {
                driverFilePath = "/home/nox_rd/chromedriver";
                extensionFilePath = "/home/nox_rd/modheader.crx";
            }
        }
    }

    @Bean
    @ConditionalOnProperty(value = "spider", havingValue = "taptap")
    public ChromeOptions chromeOptions() {
        log.info("加载chrome浏览器驱动,环境:{},文件路径:{}", activeProfile, driverFilePath);
        // 设置webdriver驱动路径
        System.setProperty("webdriver.chrome.driver", driverFilePath);
        // 解决bind() failed:Cannot assign requested address(99)的问题
        System.setProperty("webdriver.chrome.whitelistedIps", "");
        ChromeOptions chromeOptions = new ChromeOptions();
        // 设置后台运行 无头模式(linux生产环境设置为true)
        if ("dev".equals(activeProfile)) {
            chromeOptions.setHeadless(false);
        } else {
            chromeOptions.setHeadless(true);
        }
        // 将 WebDriver 的浏览器实例与当前调用程序分离，使得在程序执行完毕后，浏览器仍然继续运行，直到手动关闭为止(避免出现问题自动关闭)
        chromeOptions.setExperimentalOption("detach", true);
        // 禁用缓存 gpu
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--allow-file-access-from-files");
        chromeOptions.addArguments("--disable-cache");
        chromeOptions.addArguments("--disable-application-cache");
        chromeOptions.addArguments("--disable-gpu-sandbox");
        chromeOptions.addArguments("--disable-gpu-shader-disk-cache");
        chromeOptions.addArguments("--incognito");
        // 禁用插件
        chromeOptions.addArguments("--disable-extensions");
        // 设置扩展程序插件(添加自定义请求头)
        //chromeOptions.addExtensions(new File(extensionFilePath));
        // 窗口最大化
        chromeOptions.addArguments("start-maximized");
        // 谷歌版本111 报错 Invalid Status code=403 text=Forbidden 增加此配置解决
        chromeOptions.addArguments("--remote-allow-origins=*");
        // 设置页面加载策略
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        // 设置等待页面加载元素时间(全局配置)
        chromeOptions.setImplicitWaitTimeout(Duration.ofSeconds(5));
        // 设置js执行超时时间
        chromeOptions.setScriptTimeout(Duration.ofSeconds(60));

        // ua设置
        chromeOptions.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36 Edg/105.0.1343.42");
        // 关闭日志
        chromeOptions.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36 Edg/105.0.1343.42");
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.OFF);
        logPrefs.enable(LogType.DRIVER, Level.OFF);
        logPrefs.enable(LogType.PERFORMANCE, Level.OFF);
        chromeOptions.setCapability("goog:loggingPrefs", logPrefs);

        /**
         * chrome新版111 设置日志记录方式:
         * LoggingPreferences logPrefs = new LoggingPreferences();
         * logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
         * options.setCapability("goog:loggingPrefs", logPrefs);
         *
         * 设置代理
         * Proxy proxy = new Proxy();
         * proxy.setHttpProxy("127.0.0.1:10809");
         * chromeOptions.setProxy(proxy);
         * WebDriverManager.chromedriver()
         *                 .proxy("147.185.238.169:50006")
         *                 .setup();
         *获取代理IP
         *     public String getProxyIP() {
         *         WebDriverManager.chromedriver()
         *                 .proxy("147.185.238.169:50006")
         *                 .setup();
         *         ChromeDriver driver = new ChromeDriver(chromeOptions);
         *         driver.get("http://httpbin.org/ip");
         *         String ip = driver.findElement(By.tagName("pre")).getText();
         *         driver.quit();
         *         return ip;
         *     }
         *
         * 设置浏览器下载文件目录
         * Map<String, Object> prefs = new HashMap<String, Object>();
         * prefs.put("download.default_directory", "/directory/path");
         * chromeOptions.setExperimentalOption("prefs", prefs);
         */
        return chromeOptions;
    }
}