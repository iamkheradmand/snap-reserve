package com.snapreserve.snapreserve.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class QueueConfiguration {

    private final ConfigProvider configProvider;

    @Bean
    public Queue reservationQueue() {
        return QueueBuilder.durable(configProvider.getReservationQueue())
                .withArgument("x-dead-letter-exchange", configProvider.getReservationDeadLetterExchange())
                .withArgument("x-dead-letter-routing-key", configProvider.getReservationDeadLetterRoutingKey())
                .build();

    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(configProvider.getReservationExchange());
    }


    @Bean
    public Binding bindingQueueOne() {
        return BindingBuilder.bind(reservationQueue())
                .to(exchange())

                .with(configProvider.getReservationRoutingKey());
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(configProvider.getReservationDeadLetterQueue())
                .build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(configProvider.getReservationDeadLetterExchange());
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(configProvider.getReservationDeadLetterRoutingKey());
    }
}
