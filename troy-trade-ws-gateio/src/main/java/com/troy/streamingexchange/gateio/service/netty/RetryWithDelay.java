package com.troy.streamingexchange.gateio.service.netty;

import com.troy.commons.utils.ApplicationContextUtil;
import com.troy.redis.RedisUtil;
import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class RetryWithDelay implements Function<Flowable<? extends Throwable>, Publisher<?>> {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final int maxRetries;
    private final long retryDelayMillis;

    public RetryWithDelay(int maxRetries, long retryDelayMillis) {
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
    }

    @Override
    public org.reactivestreams.Publisher<?> apply(Flowable<? extends Throwable> flowable) throws Exception {
        return flowable.flatMap((Function<Throwable, Publisher<?>>) throwable -> {
            RedisUtil blockvcRedisUtil = ApplicationContextUtil.getBean(RedisUtil.class);
            long retryCount = blockvcRedisUtil.increment("RetryWithDelay_gateio");
            if(blockvcRedisUtil.ttl("RetryWithDelay_gateio") == -1){
                blockvcRedisUtil.expire("RetryWithDelay_gateio", 120, TimeUnit.SECONDS);
            }

            if (retryCount <= maxRetries) {
                LOG.warn("gate.io重连开始，第{}次连接", retryCount);
                return Flowable.timer(retryDelayMillis, TimeUnit.MILLISECONDS);
            } else {
                // Max retries hit. Just pass the error along.
                LOG.warn("gate.io重连次数已达最大.Max retries hit. Just pass the error along.");
                return flowable.error(throwable);
            }
        });
    }
}
