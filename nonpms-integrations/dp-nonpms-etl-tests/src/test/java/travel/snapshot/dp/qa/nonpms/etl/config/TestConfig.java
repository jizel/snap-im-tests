package travel.snapshot.dp.qa.nonpms.etl.config;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

import javax.jms.ConnectionFactory;

public class TestConfig {

    @Bean
    public JmsListenerContainerFactory<?> notificationListenerFactory(
            @NonNull ConnectionFactory connectionFactory,
            @NonNull DefaultJmsListenerContainerFactoryConfigurer configurer,
            @Value("${notification.set.pub.domain}") boolean setPubDomain) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setPubSubDomain(setPubDomain);
        return factory;
    }

}
