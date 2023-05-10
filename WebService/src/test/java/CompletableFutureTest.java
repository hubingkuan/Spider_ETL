


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * Date 2022/1/19/0019
 * author by HuBingKuan
 * 一元依赖:thenApply  cf5依赖cf2的结果
 * CompletableFuture<String> cf5 = cf2.thenApply(result2 -> {
 *   //result2为CF2的结果
 *   //......
 *   return "result5";
 * });
 * 二元依赖:thenCombine   cf4依赖cf1和cf2的结果
 * CompletableFuture<String> cf4 = cf1.thenCombine(cf2, (result1, result2) -> {
 *   //result1和result2分别为cf1和cf2的结果
 *   return "result4";
 * });
 * 多元依赖:allOf  anyOf
 * CompletableFuture<Void> cf6 = CompletableFuture.allOf(cf3, cf4, cf5);
 * CompletableFuture<String> result = cf6.thenApply(v -> {
 *   //这里的join并不会阻塞，因为传给thenApply的函数是在CF3、CF4、CF5全部完成时，才会执行 。
 *   result3 = cf3.join();
 *   result4 = cf4.join();
 *   result5 = cf5.join();
 *   //根据result3、result4、result5组装最终result;
 *   return "result";
 * });
 *
 *
 */
@Slf4j
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class CompletableFutureTest {
    @Autowired
    private ExecutorService executor;

    @Autowired
    private RedisTemplate redisTemplate;


    /*
     * supplyAsync:带返回值的异步查询
     * runAsync:无返回值的异步任务
     * thenApply:表示某个任务执行完之后执行的动作 也就是回调方法,将任务的执行结果传递到回调方法的参数
     * thenApply与thenApplyAsync的区别：前者是同一个线程执行  后者是将第一个任务提交到线程池异步执行 实际执行的可能是另外一个线程
     * thenAccept: thenAccept和thenApply一样，但无返回值
     * thenRun:对比thenApply没有入参也没有返回值
     * exceptionally:指定某个任务执行异常的回调方法，会将抛出的异常作为参数传递到回调方法中
     * whenComplete:当某个任务执行完成后执行的回调方法(参数1：正常执行的结果 参数2:上个任务抛出的异常) 返回值类型继承上个任务
     * handle:对比whenComplete的区别是返回值类型可以自定义
     * thenCombine:将2个future组合起来，只有当2个都正常执行完了才会执行某个任务，将2个任务的执行结果作为入参，有返回值
     * thenAcceptBoth:对比thenCombine，无返回值
     * runAfterBoth：对比thenCombine,无入参，无返回值
     * allof:组合多个future 等待所有任务完成 返回值void
     * anyof: 对比allof，只要有一个future结束就可以做接下来的事情
     * applyToEither:2个任务并行执行，谁先执行完就将结果交给function函数
     * get:等待任务完成(主线程休眠等待子任务完成 子线程执行完成后唤醒主线程) 抛出的是经过检查的异常 需要手动处理
     * join:对比get 抛出的是未经过检查的异常
     * */
    @Test
    public void name() throws ExecutionException, InterruptedException {
        // 第一种方式创建
        CompletableFuture<Object> future1 = new CompletableFuture<>();
        future1.complete("result1");
        // 第二种方式创建
        CompletableFuture<Object> future2 = CompletableFuture.completedFuture("result2");
        // 第三种方式创建
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            log.info("厨师开始做饭");
            return "饭做好了";
        }, executor).thenApplyAsync((result) -> {
            log.info("厨师传递的消息:{}", result);
            return "服务员开始上菜";
        }, executor).thenAcceptAsync((result) -> {
            log.info("服务员收到命令:{}", result);
            System.out.println(result);
        }, executor).thenRunAsync(() -> {
            System.out.println("任务执行完成");
        }, executor);
    }

    @Test
    public void name2() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
            try {
                log.info("睡3秒");
                Thread.currentThread().sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, executor);

        CompletableFuture<Void> f2 = CompletableFuture.runAsync(() -> {
            try {
                log.info("睡8秒");
                Thread.currentThread().sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, executor);

        CompletableFuture<Void> future1 = CompletableFuture.allOf(f1, f2).thenRunAsync(() -> log.info("任务1执行完毕"));

        CompletableFuture<Void> future2 = CompletableFuture.anyOf(f1, f2).thenRunAsync(() -> log.info("任务2执行完毕"));

        future1.get();
        future2.get();
    }

    @Test
    public void name3() throws ExecutionException, InterruptedException {
        CompletableFuture<String> c1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "任务1";
        });

        CompletableFuture<String> c2 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "任务2";
        });

        CompletableFuture<String> c3 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "任务3";
        });
        long start = System.currentTimeMillis();
        System.out.println(c1.get());
        System.out.println("任务1完成时间"+(System.currentTimeMillis()-start));
        System.out.println(c2.get());
        System.out.println("任务2完成时间"+(System.currentTimeMillis()-start));
        System.out.println(c3.get());
        System.out.println("任务3完成时间"+(System.currentTimeMillis()-start));

    }
}