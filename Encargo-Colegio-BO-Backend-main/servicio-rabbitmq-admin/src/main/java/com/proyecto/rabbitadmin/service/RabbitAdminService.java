package com.proyecto.rabbitadmin.service;

import com.proyecto.rabbitadmin.dto.*;
import java.util.List;

public interface RabbitAdminService {
    void createQueue(QueueRequestDTO request);
    boolean deleteQueue(String queueName);
    List<QueueInfoDTO> getQueues();

    void createExchange(ExchangeRequestDTO request);
    boolean deleteExchange(String exchangeName);

    void createBinding(BindingRequestDTO request);
    void deleteBinding(BindingRequestDTO request);

    void publishMessage(PublishMessageDTO request);
}