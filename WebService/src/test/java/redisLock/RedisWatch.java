package redisLock;

import java.util.concurrent.DelayQueue;

/**
 * Date 2022/08/21  20:51
 * author  by HuBingKuan
 */
public class RedisWatch {
    private boolean start;
    private DelayeListener listener;
    private DelayQueue<Watch> delayQueue = new DelayQueue<>();

    public interface DelayeListener {
        void delayeListener(Watch watch);
    }

    public void start(DelayeListener listener) {
        if (start) {
            return;
        }
        start = true;
        this.listener = listener;
        new Thread(() -> {
            try {
                while (true) {
                    Watch watch = delayQueue.take();
                    this.listener.delayeListener(watch);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

    public void add(Watch watch) {
        delayQueue.add(watch);
    }

    public boolean remove(Watch watch) {
        return delayQueue.remove(watch);
    }

    public boolean remove(String key) {
        Watch[] watches = delayQueue.toArray(new Watch[]{});
        Watch target = null;
        for (final Watch watch : watches) {
            if (watch.getKey().equals(key)) {
                target = watch;
            }
        }
        return target != null ? delayQueue.remove(target) : false;
    }
}