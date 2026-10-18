package com.proyecto.rabbitadmin.service;

import com.proyecto.rabbitadmin.dto.*;
import com.proyecto.rabbitadmin.exception.RabbitAdminException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitAdminServiceTest {

    @Mock
    private AmqpAdmin amqpAdmin;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private RestTemplate restTemplate;

    private RabbitAdminServiceImpl rabbitAdminService;

    @BeforeEach
    void setUp() {
        rabbitAdminService = new RabbitAdminServiceImpl(amqpAdmin, rabbitTemplate, restTemplate);
    }

    @Test
    void testCreateQueue_Simple() {
        QueueRequestDTO request = new QueueRequestDTO();
        request.setName("test.queue");
        request.setDurable(true);

        rabbitAdminService.createQueue(request);

        ArgumentCaptor<Queue> captor = ArgumentCaptor.forClass(Queue.class);
        verify(amqpAdmin).declareQueue(captor.capture());
        Queue created = captor.getValue();
        assertEquals("test.queue", created.getName());
        assertTrue(created.isDurable());
    }

    @Test
    void testCreateQueue_WithDLQAndTTL() {
        QueueRequestDTO request = new QueueRequestDTO();
        request.setName("test.queue");
        request.setDurable(true);
        request.setDeadLetterExchange("dlx.exchange");
        request.setDeadLetterRoutingKey("test.queue.dlq");
        request.setTtl(60000);

        rabbitAdminService.createQueue(request);

        ArgumentCaptor<Queue> captor = ArgumentCaptor.forClass(Queue.class);
        verify(amqpAdmin).declareQueue(captor.capture());
        Queue created = captor.getValue();
        assertEquals("dlx.exchange", created.getArguments().get("x-dead-letter-exchange"));
        assertEquals("test.queue.dlq", created.getArguments().get("x-dead-letter-routing-key"));
        assertEquals(60000, created.getArguments().get("x-message-ttl"));
    }

    @Test
    void testDeleteQueue() {
        when(amqpAdmin.deleteQueue("test.queue")).thenReturn(true);

        boolean result = rabbitAdminService.deleteQueue("test.queue");

        assertTrue(result);
        verify(amqpAdmin).deleteQueue("test.queue");
    }

    @Test
    void testCreateExchange_Direct() {
        ExchangeRequestDTO request = new ExchangeRequestDTO();
        request.setName("test.exchange");
        request.setType("direct");
        request.setDurable(true);

        rabbitAdminService.createExchange(request);

        ArgumentCaptor<Exchange> captor = ArgumentCaptor.forClass(Exchange.class);
        verify(amqpAdmin).declareExchange(captor.capture());
        Exchange created = captor.getValue();
        assertEquals("test.exchange", created.getName());
        assertEquals("direct", created.getType());
    }

    @Test
    void testCreateExchange_InvalidType() {
        ExchangeRequestDTO request = new ExchangeRequestDTO();
        request.setName("test.exchange");
        request.setType("invalid_type");

        assertThrows(RabbitAdminException.class, () -> rabbitAdminService.createExchange(request));
    }

    @Test
    void testDeleteExchange() {
        when(amqpAdmin.deleteExchange("test.exchange")).thenReturn(true);

        boolean result = rabbitAdminService.deleteExchange("test.exchange");

        assertTrue(result);
        verify(amqpAdmin).deleteExchange("test.exchange");
    }

    @Test
    void testCreateBinding() {
        BindingRequestDTO request = new BindingRequestDTO();
        request.setDestination("test.queue");
        request.setDestinationType("QUEUE");
        request.setExchange("test.exchange");
        request.setRoutingKey("test.key");

        rabbitAdminService.createBinding(request);

        ArgumentCaptor<Binding> captor = ArgumentCaptor.forClass(Binding.class);
        verify(amqpAdmin).declareBinding(captor.capture());
        Binding created = captor.getValue();
        assertEquals("test.queue", created.getDestination());
        assertEquals("test.exchange", created.getExchange());
        assertEquals("test.key", created.getRoutingKey());
    }

    @Test
    void testDeleteBinding() {
        BindingRequestDTO request = new BindingRequestDTO();
        request.setDestination("test.queue");
        request.setDestinationType("QUEUE");
        request.setExchange("test.exchange");
        request.setRoutingKey("test.key");

        rabbitAdminService.deleteBinding(request);

        verify(amqpAdmin).removeBinding(any(Binding.class));
    }

    @Test
    void testPublishMessage() {
        PublishMessageDTO request = new PublishMessageDTO();
        request.setExchange("test.exchange");
        request.setRoutingKey("test.key");
        request.setBody("Hello World");

        rabbitAdminService.publishMessage(request);

        verify(rabbitTemplate).convertAndSend(eq("test.exchange"), eq("test.key"), eq("Hello World"), any(MessagePostProcessor.class));
    }
}