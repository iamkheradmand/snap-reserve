package com.snapreserve.snapreserve;

import com.redis.testcontainers.RedisContainer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockProvider;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@Import(BaseIT.TestConfigs.class)
@ExtendWith({SpringExtension.class})
@ActiveProfiles("test")
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class BaseIT {
    @Container
    protected static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.8.2-management-alpine");

    @Container
    static GenericContainer<?> redisContainer = new RedisContainer("redis:4-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
    }

    @RequiredArgsConstructor
    public static class TestConfigs {

        @Bean
        @Primary
        public LockProvider lockProvider() {
            return new NoOpLockProvider();
        }

        @Bean
        public CacheManager inMemoryCacheManager() {
            return new NoOpCacheManager();
        }

        @Bean
        public DirectExchange directExchange() {
            return new DirectExchange("test_exchange");
        }

        @Bean
        public Queue testQueue() {
            return new Queue("test_queue", false);
        }

        @Bean
        public Binding bindingTestQueue() {
            return BindingBuilder.bind(testQueue())
                    .to(directExchange())
                    .with("test_queue");
        }
    }

}
