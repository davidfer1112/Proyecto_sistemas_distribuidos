package org.distribuidos.healthCheck;

import org.zeromq.ZMQ;

public class HealthCheck {
    private static final int TIMEOUT = 5000;  // Tiempo en milisegundos
    private static final int POLL_TIMEOUT = 1000;  // Tiempo de espera para el evento de mensaje (en milisegundos)

    public static void main(String[] args) {
        try (ZMQ.Context context = ZMQ.context(1);
             ZMQ.Socket intervalSubscriber = context.socket(ZMQ.SUB)) {

            intervalSubscriber.connect("tcp://*:5559");
            intervalSubscriber.subscribe("".getBytes());

            long lastMessageTime = System.currentTimeMillis();

            while (true) {
                // Configura el conjunto de sockets para el evento de entrada
                ZMQ.Poller poller = context.poller(1);
                poller.register(intervalSubscriber, ZMQ.Poller.POLLIN);

                // Espera eventos durante el tiempo de espera del poller
                if (poller.poll(POLL_TIMEOUT) > 0) {
                    // Si hay un mensaje, lo procesa
                    String intervalMessage = intervalSubscriber.recvStr();
                    int sendInterval = Integer.parseInt(intervalMessage);
                    System.out.println("Valor de sendInterval recibido por HealthCheck: " + sendInterval);
                    lastMessageTime = System.currentTimeMillis();
                    // Realiza otras acciones según tus necesidades
                } else {
                    // Si no hay mensajes recibidos durante el tiempo de espera del poller
                    if (System.currentTimeMillis() - lastMessageTime > TIMEOUT) {
                        // Llama a la función de redireccion
                        redireccion();
                        // Actualiza el tiempo del último mensaje para evitar llamadas múltiples
                        lastMessageTime = System.currentTimeMillis();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void redireccion() {
        System.out.println("Redirigiendo...");
        // Agrega aquí la lógica de redirección que necesitas
    }
}
