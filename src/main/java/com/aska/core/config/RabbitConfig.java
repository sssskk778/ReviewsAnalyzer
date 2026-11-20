package com.aska.core.config;

import com.aska.core.constants.AppConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${app.rabbit.task-queue}")
    private String taskQueue;
    @Value("${app.rabbit.task-routing-key}")
    private String taskRoutingKey;
    @Value("${app.rabbit.result-queue}")
    private String resultQueue;
    @Value("${app.rabbit.result-routing-key}")
    private String resultRoutingKey;
    @Value("${app.rabbit.exchange}")
    private String exchange;

    @Bean
    public Queue taskQueue(){
        return new Queue(taskQueue,true);
    }

    @Bean
    public Queue resultQueue(){
        return new Queue(resultQueue,true);
    }

    @Bean
    public DirectExchange exchange(){
        return new DirectExchange(exchange);
    }

    @Bean
    public Binding taskBinding(Queue queue, DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(taskRoutingKey);
    }
    @Bean
    public Binding resultBinding(Queue queue, DirectExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(resultRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
