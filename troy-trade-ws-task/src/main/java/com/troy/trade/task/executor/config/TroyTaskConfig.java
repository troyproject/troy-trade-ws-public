package com.troy.trade.task.executor.config;

import com.troy.task.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * troy-task config
 *
 * @author troy
 */
@Slf4j
@Configuration
public class TroyTaskConfig {

    @Value("${troy.task.admin.addresses}")
    private String adminAddresses;

    @Value("${troy.task.executor.appname}")
    private String appName;

    @Value("${troy.task.executor.ip}")
    private String ip;

    @Value("${troy.task.executor.port}")
    private int port;

    @Value("${troy.task.accessToken}")
    private String accessToken;

    @Value("${troy.task.executor.logpath}")
    private String logPath;

    @Value("${troy.task.executor.logretentiondays}")
    private int logRetentionDays;


    @Bean(initMethod = "start", destroyMethod = "destroy")
    public XxlJobSpringExecutor xxlJobExecutor() {
        log.info(">>>>>>>>>>> troy-task config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
        xxlJobSpringExecutor.setAppName(appName);
        xxlJobSpringExecutor.setIp(ip);
        xxlJobSpringExecutor.setPort(port);
        xxlJobSpringExecutor.setAccessToken(accessToken);
        xxlJobSpringExecutor.setLogPath(logPath);
        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);

        return xxlJobSpringExecutor;
    }
}