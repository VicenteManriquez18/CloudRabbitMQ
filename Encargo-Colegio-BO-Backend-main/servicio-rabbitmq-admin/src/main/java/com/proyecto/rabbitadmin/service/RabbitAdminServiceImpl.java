package com.proyecto.rabbitadmin.service;

import com.proyecto.rabbitadmin.dto.*;
import com.proyecto.rabbitadmin.exception.RabbitAdminException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RabbitAdminServiceImpl implements RabbitAdminService {

    private static final Logger log = LoggerFactory.getLogger(RabbitAdminServiceImpl.class);

    private final AmqpAdmin amqpAdmin;
    private final RabbitTemplate rabbitTemplate;
    private final RestTemplate restTemplate;

    @Value("${spring.rabbitmq.host:localhost}")
    private String rabbitHost;

    @Value("${rabbitmq.management.port:15672}")
    private int managementPort;

    @Value("${spring.rabbitmq.username:guest}")
    private String rabbitUser;

    @Value("${spring.rabbitmq.password:guest}")
    private String rabbitPass;

    public RabbitAdminServiceImpl(AmqpAdmin amqpAdmin, RabbitTemplate rabbitTemplate, RestTemplate restTemplate) {
        this.amqpAdmin = amqpAdmin;
        this.rabbitTemplate = rabbitTemplate;
        this.restTemplate = restTemplate;
    }

    @Override
    public void createQueue(QueueRequestDTO request) {
        log.info("Creando cola: {}", request.getName());
        try {
            QueueBuilder builder = request.isDurable() 
                    ? QueueBuilder.durable(request.getName()) 
                    : QueueBuilder.nonDurable(request.getName());

            if (request.isExclusive()) {
                builder.exclusive();
            }
            if (request.isAutoDelete()) {
                builder.autoDelete();
            }
            if (request.getDeadLetterExchange() != null && !request.getDeadLetterExchange().isBlank()) {
                builder.deadLetterExchange(request.getDeadLetterExchange());
            }
            if (request.getDeadLetterRoutingKey() != null && !request.getDeadLetterRoutingKey().isBlank()) {
                builder.deadLetterRoutingKey(request.getDeadLetterRoutingKey());
            }
            if (request.getTtl() != null && request.getTtl() > 0) {
                builder.ttl(request.getTtl());
            }

            Queue queue = builder.build();
            amqpAdmin.declareQueue(queue);
            log.info("Cola creada exitosamente: {}", request.getName());
        } catch (Exception e) {
            log.error("Error al crear la cola {}: {}", request.getName(), e.getMessage(), e);
            throw new RabbitAdminException("Error al crear la cola: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteQueue(String queueName) {
        log.info("Eliminando cola: {}", queueName);
        try {
            boolean result = amqpAdmin.deleteQueue(queueName);
            log.info("Resultado de eliminación de cola {}: {}", queueName, result);
            return result;
        } catch (Exception e) {
            log.error("Error al eliminar la cola {}: {}", queueName, e.getMessage(), e);
            throw new RabbitAdminException("Error al eliminar la cola: " + e.getMessage(), e);
        }
    }

    @Override
    public List<QueueInfoDTO> getQueues() {
        log.info("Consultando lista de colas a RabbitMQ Management API...");
        List<QueueInfoDTO> queueList = new ArrayList<>();
        try {
            String url = String.format("http://%s:%d/api/queues", rabbitHost, managementPort);
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(rabbitUser, rabbitPass);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            if (response.getBody() != null) {
                for (Map<String, Object> q : response.getBody()) {
                    String name = (String) q.get("name");
                    long messages = q.get("messages") != null ? ((Number) q.get("messages")).longValue() : 0L;
                    int consumers = q.get("consumers") != null ? ((Number) q.get("consumers")).intValue() : 0;
                    boolean durable = q.get("durable") != null && (Boolean) q.get("durable");
                    queueList.add(new QueueInfoDTO(name, messages, consumers, durable));
                }
            }
            log.info("Se encontraron {} colas en RabbitMQ", queueList.size());
        } catch (Exception e) {
            log.warn("No fue posible consultar la Management API de RabbitMQ ({}:{}): {}", rabbitHost, managementPort, e.getMessage());
        }
        return queueList;
    }

    @Override
    public void createExchange(ExchangeRequestDTO request) {
        log.info("Creando exchange: {} tipo {}", request.getName(), request.getType());
        try {
            Exchange exchange;
            String type = request.getType().toLowerCase();
            switch (type) {
                case "direct":
                    exchange = new DirectExchange(request.getName(), request.isDurable(), request.isAutoDelete());
                    break;
                case "topic":
                    exchange = new TopicExchange(request.getName(), request.isDurable(), request.isAutoDelete());
                    break;
                case "fanout":
                    exchange = new FanoutExchange(request.getName(), request.isDurable(), request.isAutoDelete());
                    break;
                case "headers":
                    exchange = new HeadersExchange(request.getName(), request.isDurable(), request.isAutoDelete());
                    break;
                default:
                    throw new RabbitAdminException("Tipo de exchange no soportado: " + request.getType());
            }

            amqpAdmin.declareExchange(exchange);
            log.info("Exchange creado exitosamente: {}", request.getName());
        } catch (RabbitAdminException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al crear exchange {}: {}", request.getName(), e.getMessage(), e);
            throw new RabbitAdminException("Error al crear exchange: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteExchange(String exchangeName) {
        log.info("Eliminando exchange: {}", exchangeName);
        try {
            boolean result = amqpAdmin.deleteExchange(exchangeName);
            log.info("Resultado de eliminación de exchange {}: {}", exchangeName, result);
            return result;
        } catch (Exception e) {
            log.error("Error al eliminar exchange {}: {}", exchangeName, e.getMessage(), e);
            throw new RabbitAdminException("Error al eliminar exchange: " + e.getMessage(), e);
        }
    }

    @Override
    public void createBinding(BindingRequestDTO request) {
        log.info("Creando enlace entre {} -> {} ({}) con routingKey '{}'",
                request.getExchange(), request.getDestination(), request.getDestinationType(), request.getRoutingKey());
        try {
            Binding.DestinationType destType = Binding.DestinationType.valueOf(request.getDestinationType().toUpperCase());
            Binding binding = new Binding(
                    request.getDestination(),
                    destType,
                    request.getExchange(),
                    request.getRoutingKey() != null ? request.getRoutingKey() : "",
                    null
            );
            amqpAdmin.declareBinding(binding);
            log.info("Enlace creado exitosamente");
        } catch (IllegalArgumentException e) {
            throw new RabbitAdminException("Tipo de destino inválido (debe ser QUEUE o EXCHANGE): " + request.getDestinationType());
        } catch (Exception e) {
            log.error("Error al crear binding: {}", e.getMessage(), e);
            throw new RabbitAdminException("Error al crear binding: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteBinding(BindingRequestDTO request) {
        log.info("Eliminando enlace entre {} -> {} con routingKey '{}'",
                request.getExchange(), request.getDestination(), request.getRoutingKey());
        try {
            Binding.DestinationType destType = Binding.DestinationType.valueOf(request.getDestinationType().toUpperCase());
            Binding binding = new Binding(
                    request.getDestination(),
                    destType,
                    request.getExchange(),
                    request.getRoutingKey() != null ? request.getRoutingKey() : "",
                    null
            );
            amqpAdmin.removeBinding(binding);
            log.info("Enlace eliminado exitosamente");
        } catch (Exception e) {
            log.error("Error al eliminar binding: {}", e.getMessage(), e);
            throw new RabbitAdminException("Error al eliminar binding: " + e.getMessage(), e);
        }
    }

    @Override
    public void publishMessage(PublishMessageDTO request) {
        log.info("Publicando mensaje en exchange '{}' con routingKey '{}'", request.getExchange(), request.getRoutingKey());
        try {
            rabbitTemplate.convertAndSend(
                    request.getExchange() != null ? request.getExchange() : "",
                    request.getRoutingKey(),
                    request.getBody(),
                    message -> {
                        if (request.getHeaders() != null) {
                            request.getHeaders().forEach((k, v) -> message.getMessageProperties().setHeader(k, v));
                        }
                        return message;
                    }
            );
            log.info("Mensaje publicado exitosamente");
        } catch (Exception e) {
            log.error("Error al publicar mensaje: {}", e.getMessage(), e);
            throw new RabbitAdminException("Error al publicar mensaje: " + e.getMessage(), e);
        }
    }
}