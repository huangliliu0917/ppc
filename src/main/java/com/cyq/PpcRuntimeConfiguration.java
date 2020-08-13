package com.cyq;

import com.cyq.ppc.constant.AppConsts;
import com.cyq.ppc.job.OrderJob;
import com.cyq.ppc.job.TaobaoBlackGroupJob;
import com.cyq.ppc.job.TbxjhbPlusJob;
import com.cyq.wsserver.server.ImServerStarter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Slf4j
@Configuration
@Conditional(PpcRuntimeConfiguration.EnableCondition.class)
public class PpcRuntimeConfiguration {
    /**
     * 是否是socket服务器
     */
    public static boolean isWsServer = false;

    @Bean
    public OrderJob orderJob() {
        return new OrderJob();
    }

    //@Bean
    public TbxjhbPlusJob tbxjhbPlusJob() {
        return new TbxjhbPlusJob();
    }

    @Bean
    public TaobaoBlackGroupJob taobaoBlackGroupJob() {
        return new TaobaoBlackGroupJob();
    }

    @Bean
    ImServerStarter imServerStarter() {
        isWsServer = true;
        return new ImServerStarter();
    }

    public static class EnableCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return Boolean.parseBoolean(context.getEnvironment().getProperty(AppConsts.WS启动参数名));
        }
    }

}
