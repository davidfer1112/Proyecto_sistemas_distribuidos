package org.distribuidos.Monitor;


import org.distribuidos.Mensaje.Mensaje;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.zeromq.ZMQ;

import org.zeromq.ZMQ;

public class Monitor {
    public static void main(String[] args) {
        String sensorType = args[0];

        try (ZMQ.Context context = ZMQ.context(1);
             ZMQ.Socket subscriber = context.socket(ZMQ.SUB)) {

            // Cambia la conexión al canal de suscripción para que sea la dirección IP de la computadora que ejecuta el programa 'Sensor'.
            subscriber.connect("tcp://192.168.0.10:5556");
            subscriber.subscribe(sensorType.getBytes());

            while (true) {
                // Recibe el mensaje de saludo del canal de suscripción
                String message = subscriber.recvStr();
                System.out.println("Received message: " + message);

                // Implementa la lógica para validar y almacenar la medición
                // ...
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}