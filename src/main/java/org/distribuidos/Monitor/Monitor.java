package org.distribuidos.Monitor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.zeromq.ZMQ;

public class Monitor {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Debe proporcionar el tipo de sensor como argumento.");
            return;
        }

        String sensorType = args[0];

        try (ZMQ.Context context = ZMQ.context(1);
             ZMQ.Socket subscriber = context.socket(ZMQ.SUB);
             ZMQ.Socket systemQualityPublisher = context.socket(ZMQ.PUB)) {

            // Cambia la conexión al canal de suscripción para que sea la dirección IP de la computadora que ejecuta el programa 'Sensor'.
            subscriber.connect("tcp://192.168.0.12:5556");
            subscriber.subscribe(sensorType.getBytes());

            // Conexión al canal de publicación del Sistema de Calidad
            systemQualityPublisher.bind("tcp://*:5557");

            while (true) {
                // Recibe el mensaje del canal de suscripción
                String message = subscriber.recvStr();
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String dateString = dateFormat.format(date);

                System.out.println("[" + dateString + "] Mensaje: " + message);

                message = "[" + dateString + "]" + message;

                // Escribe el mensaje en el archivo
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("reporte.txt", true))) {
                    writer.write(message);
                    writer.newLine(); // Agrega una nueva línea para separar los mensajes
                    writer.flush(); // Asegura que los datos se escriban en el archivo
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Verifica si el mensaje indica un error y envía al Sistema de Calidad
                if (message.contains("Errores")) {
                    // Envía mensaje al Sistema de Calidad
                    String systemQualityMessage = "[" + dateString + "] Error en el monitor de " + sensorType +
                            " con valor: " + parseErrorValue(message);
                    systemQualityPublisher.send(systemQualityMessage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String parseErrorValue(String errorMessage) {
        // Realiza el análisis del mensaje de error para extraer el valor
        String[] parts = errorMessage.split(" ");
        if (parts.length >= 10) {
            return parts[9];
        } else {
            return "Valor Desconocido";
        }
    }
}
