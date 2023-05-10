package com.yeshen.appcenter.config.httpclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.HttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

@Slf4j
public class IdleConnectionMonitorThread extends Thread{
    private static final int MONITOR_INTERVAL_MS=2000;

    private static final int IDLE_ALIVE_MS=5000;


    private final HttpClientConnectionManager connMgr;
    private volatile boolean shutdown;

    public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
        setDaemon(true);
        this.connMgr = connMgr;
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(MONITOR_INTERVAL_MS);
                    connMgr.closeExpiredConnections();
                    connMgr.closeIdleConnections(IDLE_ALIVE_MS, TimeUnit.MILLISECONDS);
                }
            }
        } catch (InterruptedException ex) {
            log.warn("管理连接的线程睡眠被唤醒:{}",ex.getMessage());
        }
    }

    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }
}