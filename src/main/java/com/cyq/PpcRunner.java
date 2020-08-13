package com.cyq;

import com.cyq.framework.common.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class PpcRunner implements CommandLineRunner {

    @Autowired
    Environment env;

    @Override
    public void run(String... args) throws Exception {

        if (CommonUtils.inString(env.getProperty("spring.profiles.active"), "dev", "test")) {
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleAtFixedRate(() -> {

                //Set<String> keys = RedisHelper.keys(PayChannel.淘宝现金红包PLUS.getChannelCode() + "*");
            }, 5, 10, TimeUnit.SECONDS);
        }
    }

}
