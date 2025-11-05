# ReadME

Sistema de procesamiento de pedidos asíncrono con **Spring Boot** y **RabbitMQ**, simulando el flujo de una tienda de bicicletas.  
Permite enviar órdenes, procesar pagos y enviar confirmaciones por correo usando colas de mensajería.

## Tecnologías usadas
- **Java 21**
- **Spring Boot**
- **RabbitMQ**
- **Maven**
- **JSON**
- **Postman**

## Aspectos importantes
1. Tener **RabbitMQ** instalado y corriendo.  
   Panel web: [http://localhost:15672](http://localhost:15672)  
   - User: guest
   - Password: guest


## Proceso de ejecucion.
1. Iniciar RabbitMQ
2. Ejecutar la clase de Springboot (BikeStoreApiApplication)
3. Ejecutar los consumidores
   - DLQConsumer
   - EmailWorker
   - PaymentWorker
4. Utilizando postman generar una orden POST
   - Método: POST
   - URL: http://localhost:8080/ordenar
   - Body → raw → JSON:
   - JSON:
     {
  "cliente": "name@bikestore.com",
  "producto": "ProductName",
  "cantidad": x
}
  
5. Monitorear las terminales de las distintas clases ejecutadas en el IDE siendo de esta forma donde deben asomar:

- Terminal API
Orden enviada a RabbitMQ: {cliente, producto, cantidad}
- PaymentWorker
Procesando pago de orden 12345
- EmailWorker
Enviando confirmación a user@bikestore.com
- DLQConsumer (En caso de un error)
DLQConsumer capturó orden fallida

6. Chequeo
Adicionalmente, se puede revisar el envio de la orden a traves de Postman mediante su mensaje de confirmacion o sino en RabbitMQ mediante Cola orders → recibe pedidos

## Desarrollo

Intellij IDEA
05/11/2025
