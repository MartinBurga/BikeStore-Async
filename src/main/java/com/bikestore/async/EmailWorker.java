package com.bikestore.async;
import com.rabbitmq.client.*;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
public class EmailWorker {
    private static final String EMAILS_QUEUE = "emails";
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(EMAILS_QUEUE, false, false, false, null);
        System.out.println("EmailWorker waiting for messages...");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            JSONObject order = new JSONObject(message);
            String orderId = order.getString("orderId");
            if ("PAID".equals(order.getString("paymentStatus"))) {
                System.out.printf("[%s][Thread-%d] Enviando correo de confirmacion: %s a %s%n",
                        LocalDateTime.now(), Thread.currentThread().getId(),
                        orderId, order.getString("customer"));
            }
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };
        channel.basicConsume(EMAILS_QUEUE, false, deliverCallback, consumerTag -> {});
    }
}
