package com.yeshen.appcenter.utils;

import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronization;

/**
 * Date 2022/11/24  15:20
 * author  by HuBingKuan
 */
public class TransactionUtil {
    /**
     * 事务完成后要执行的一些方法
     * @param
     */
    public static void doAfterTraction(Runnable runnable){
        // 如果上下文中存在事务
        if(TransactionSynchronizationManager.isActualTransactionActive()){
            TransactionSynchronizationManager.registerSynchronization(new DoTransactionCompletion(runnable));
        }
    }
}
class DoTransactionCompletion implements TransactionSynchronization{
    private Runnable runnable;

    public DoTransactionCompletion(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void afterCompletion(int status) {
        if(status==TransactionSynchronization.STATUS_COMMITTED){
            runnable.run();
        }
    }
}