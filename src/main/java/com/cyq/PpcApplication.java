package com.cyq;

import com.cyq.ppc.constant.PayChannel;
import com.cyq.ppc.strategy.ChannelStrategy;
import com.cyq.ppc.strategy.tcpay.TcPayProperties;
import lombok.extern.slf4j.Slf4j;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * tail -100f /usr/logs/ppc/spring.log
 */
@Slf4j
@EnableConfigurationProperties({TcPayProperties.class})
@EnableScheduling
@EnableRedisHttpSession
@SpringBootApplication
public class PpcApplication {

    public static void main(String[] args) {
        SpringApplication.run(PpcApplication.class, args);
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 注入bean
     */
    @Bean
    public Map<PayChannel, ChannelStrategy> channelStrategyMap(List<ChannelStrategy> strategyList) {
        Map<PayChannel, ChannelStrategy> map = new HashMap<>();
        strategyList.forEach(it -> map.put(it.getPayChannel(), it));
        return map;
    }

    /**
     * 1.默认消费者并不会消费订阅发布类型的消息，这是由于springboot默认采用的是p2p模式进行消息的监听
     * 修改配置：spring.jms.pub-sub-domain=true使其支持发布订阅模型
     * 2.@JmsListener如果不指定独立的containerFactory的话是只能消费queue消息
     * 修改订阅者container：containerFactory=“jmsListenerContainerTopic”
     * 3.需要给topic定义独立的JmsListenerContainer
     * 4.在配置文件里面，注释掉 #spring.jms.pub-sub-domain=true
     *
     * @param jmsPoolConnectionFactory
     * @return
     */

    @Bean
    public JmsListenerContainerFactory<?> jmsListenerContainerTopic(JmsPoolConnectionFactory jmsPoolConnectionFactory) {
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        bean.setPubSubDomain(true);
        bean.setConnectionFactory(jmsPoolConnectionFactory);
        return bean;
    }

}

