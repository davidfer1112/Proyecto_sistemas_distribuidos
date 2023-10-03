package org.distribuidos.Sensor;


import org.zeromq.ZMQ;

import org.zeromq.ZMQ;

public class Sensor {
    public static void main(String[] args) {
        // Verifica que se ingresen tres argumentos
        if (args.length != 3) {
            System.out.println("La forma correcta de los parámetros es: tipoSensor Tiempo archivoConfig");
            return; // Sale del programa si no hay tres argumentos
        }

        String sensorType = args[0];
        int sendInterval;
        String configFileName = args[2];

        // Verifica que el segundo argumento sea un entero válido
        try {
            sendInterval = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("El segundo argumento (Tiempo) debe ser un número entero válido.");
            return; // Sale del programa si el segundo argumento no es un entero válido
        }

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