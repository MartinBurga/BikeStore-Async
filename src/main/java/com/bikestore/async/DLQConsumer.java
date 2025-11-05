package com.bikestore.async;
import com.rabbitmq.client.*;
import java.nio.charset.StandardCharsets;
public class DLQConsumer {
    private static final String DLQ_QUEUE = "dead_letters";
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(DLQ_QUEUE, false, false, false, null);
        System.out.println("DLQConsumer waiting for failed messages...");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("[DLQ] " + message);
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };
        channel.basicConsume(DLQ_QUEUE, false, deliverCallback, consumerTag -> {});
    }
}
