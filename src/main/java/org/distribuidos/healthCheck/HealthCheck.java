package org.distribuidos.healthCheck;

import org.zeromq.ZMQ;

public class HealthCheck {
    public static void main(String[] args) {
        try (ZMQ.Context context = ZMQ.context(1);
             ZMQ.Socket intervalSubscriber = context.socket(ZMQ.SUB)) {

            intervalSubscriber.connect("tcp://*:5559");  // Ajusta el puerto según tus necesidades
            intervalSubscriber.subscribe("".getBytes());

            while (true) {
                // Recibe el valor de sendInterval del Monitor
                String intervalMessage = intervalSubscriber.recvStr();
                int sendInterval = Integer.parseInt(intervalMessage);

                // Imprime el valor por consola
                System.out.println("Valor de sendInterval recibido por HealthCheck: " + sendInterval);

                // Realiza otras acciones según tus necesidades
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
