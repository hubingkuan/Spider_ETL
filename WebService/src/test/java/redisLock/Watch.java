package redisLock;

import lombok.Data;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Date 2022/08/21  20:43
 * author  by HuBingKuan
 */
@Data
public class Watch implements Delayed {
    String key;
    String value;
    long originalTime;
    // 续约时间倍数
    double t=0.7;
    // 续约到期时间 watch对象提前出队 续约redis时间
    long time;

    public Watch(String key, String value, long time) {
        this.key = key;
        this.value = value;
        this.originalTime = time;
        this.time = (long) (t*time);
    }

    /**
     * 创建元素时多久才能从队列中获取当前元素
     * 只有在延迟期满时才能从队列中提取元素
     * @param unit
     * @return
     */
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(time-System.currentTimeMillis(),TimeUnit.MILLISECONDS);
    }

    /**
     * 队列中元素排队顺序
     * @param o
     * @return
     */
    @Override
    public int compareTo(Delayed o) {
        if(o == this){
            return 0;
        }
        if(o instanceof  Watch){
            Watch watch = (Watch) o;
            long watchTime = watch.getTime();
            return (int)( this.time - watchTime);
        }
        return 0;
    }
}