package com.bikestore.async;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class OrderController {

    @Autowired
    private OrderProducer producer;

    @PostMapping("/ordenar")
    public String crearOrden() throws Exception {
        producer.enviarOrden();
        return "Orden enviada al broker";
    }
}
