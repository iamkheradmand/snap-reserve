package com.snapreserve.snapreserve.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;


@Configuration
public class RabbitMqConfig {

	@Bean
	public Jackson2JsonMessageConverter converter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(converter);
		return template;
	}

	@Bean
	public RetryOperationsInterceptor retryInterceptor() {
		return RetryInterceptorBuilder.stateless()
				.maxAttempts(3)
				.backOffOptions(2000, 2.0, 100000)
				.recoverer(new RejectAndDontRequeueRecoverer())
				.build();
	}

	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setMessageConverter(converter);
		factory.setAdviceChain(retryInterceptor());
		factory.setConcurrentConsumers(10); // should increase in production
		BackOff backOff = new FixedBackOff(3000, 3);
		factory.setRecoveryBackOff(backOff);
		factory.setDefaultRequeueRejected(false);
		factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
		return factory;
	}

}
