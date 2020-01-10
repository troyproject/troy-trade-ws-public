package com.troy.trade.ws.thread;

import java.util.concurrent.*;

public class PushThreadPool {

    /**
     * 合约币对余额信息同步
     */
    private static final ThreadPoolExecutor EXECUTOR_FUTURES_BALANCE_SYNC = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2, 30, 10L,
            TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(10000), r -> {
        Thread t = new Thread(r);
        return t;
    }, new ThreadPoolExecutor.CallerRunsPolicy());

    static {
        EXECUTOR_FUTURES_BALANCE_SYNC.prestartAllCoreThreads();
    }

    public static void executeFuturesBalanceSync(FuturesBalancePushSyncExecute futuresBalancePushSyncExecute) throws Throwable {
        EXECUTOR_FUTURES_BALANCE_SYNC.submit(futuresBalancePushSyncExecute);
    }
}
