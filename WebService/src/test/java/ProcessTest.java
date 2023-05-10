import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Date 2022/10/19  17:44
 * author  by HuBingKuan
 */
public class ProcessTest {
    @Test
    void name() throws Exception {
        Runtime runtime = Runtime.getRuntime();

        Process process;
        // 用法1:调用一个外部程序
        process = runtime.exec("javac");
        // 用法2:调用一个指令(可包含参数)   cmd /c :执行结束后 关闭cmd进程
        // process = runtime.exec("cmd /c dir");
        // 用法3:去D盘的test目录调用一个.bat文件
        // process = runtime.exec("cmd /c test.bat", null, new File("D:\\test"));

        // 存放要输出的行数据
        String line;

        // 输出返回的报错信息
        System.out.println("=================ErrorStream===================");
        BufferedReader inErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while ((line = inErr.readLine()) != null) {
            System.out.println(line);
        }

        // 输出正常的返回信息
        System.out.println("=================InputStream===================");
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }

        // 获取退出码
        int exitVal = process.waitFor(); // 等待进程运行结束
        System.out.println("exitVal = " + exitVal);
    }
}