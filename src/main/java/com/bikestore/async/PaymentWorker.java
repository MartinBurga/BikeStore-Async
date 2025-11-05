package com.bikestore.async;
import com.rabbitmq.client.*;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Random;
public class PaymentWorker {
    private static final String ORDERS_QUEUE = "orders";
    private static final String EMAILS_QUEUE = "emails";
    private static final String DLQ_QUEUE = "dead_letters";
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(ORDERS_QUEUE, false, false, false, null);
        channel.queueDeclare(EMAILS_QUEUE, false, false, false, null);
        channel.queueDeclare(DLQ_QUEUE, false, false, false, null);
        System.out.println("PaymentWorker waiting for messages...");
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            JSONObject order = new JSONObject(message);
            String orderId = order.getString("orderId");
            System.out.printf("[%s][Thread-%d] Procesando pago para %s%n",
                    LocalDateTime.now(), Thread.currentThread().getId(), orderId);
            boolean paid = false;
            Random rand = new Random();
            int attempts = 0;
            while (attempts < 3 && !paid) {
                attempts++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                paid = rand.nextBoolean();
                if (!paid)
                    System.out.printf("[%s][Thread-%d] Retry %d failed for %s%n",
                            LocalDateTime.now(), Thread.currentThread().getId(), attempts, orderId);
            }
            if (paid) {
                order.put("paymentStatus", "PAID");
                channel.basicPublish("", EMAILS_QUEUE, null, order.toString().getBytes(StandardCharsets.UTF_8));
                System.out.printf("[%s][Thread-%d] Pago realizado exitosamente para: %s%n",
                        LocalDateTime.now(), Thread.currentThread().getId(), orderId);
            } else {
                order.put("paymentStatus", "FAILED");
                channel.basicPublish("", DLQ_QUEUE, null, order.toString().getBytes(StandardCharsets.UTF_8));
                System.out.printf("[%s][Thread-%d] Pago fallido tras 3 reintentos → DLQ %s%n",
                        LocalDateTime.now(), Thread.currentThread().getId(), orderId);
            }
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };
        channel.basicQos(1);
        channel.basicConsume(ORDERS_QUEUE, false, deliverCallback, consumerTag -> {});
    }
}
