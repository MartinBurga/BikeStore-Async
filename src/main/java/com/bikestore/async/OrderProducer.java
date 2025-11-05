package com.bikestore.async;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderProducer {

    private static final String QUEUE_NAME = "orders";

    public void enviarOrden() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String orderId = UUID.randomUUID().toString();

            JSONObject order = new JSONObject();
            order.put("orderId", orderId);
            order.put("customer", "cliente@bikestore.com");
            order.put("amount", 99.90);
            order.put("timestamp", LocalDateTime.now().toString());

            channel.basicPublish("", QUEUE_NAME, null, order.toString().getBytes(StandardCharsets.UTF_8));
            System.out.printf("[%s] Orden enviada: %s%n", LocalDateTime.now(), orderId);
        }
    }
}
