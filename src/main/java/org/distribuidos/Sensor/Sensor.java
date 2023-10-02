package org.distribuidos.Sensor;

import org.distribuidos.Mensaje.Mensaje;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

import org.zeromq.ZMQ;

public class Sensor {
    public static void main(String[] args) {
        String sensorType = args[0];
        int sendInterval = Integer.parseInt(args[1]);
        String configFileName = args[2];

        // Aquí deberías leer el archivo de configuración y obtener las probabilidades.

        try (ZMQ.Context context = ZMQ.context(1);
             ZMQ.Socket publisher = context.socket(ZMQ.PUB)) {

            // Cambia la conexión al canal de publicación para que sea una dirección IP accesible en tu red.
            publisher.bind("tcp://*:5556");

            while (true) {
                // Envía el mensaje de saludo
                publisher.send(sensorType + " Hola, ¿cómo estás?");

                // Espera antes de enviar el siguiente mensaje
                Thread.sleep(sendInterval);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}