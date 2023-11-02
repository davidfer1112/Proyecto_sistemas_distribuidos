package org.distribuidos.Monitor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
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

        // Obtén el ID del proceso (PID)
        String processId = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

        try (ZMQ.Context context = ZMQ.context(1);
             ZMQ.Socket subscriber = context.socket(ZMQ.SUB);
             ZMQ.Socket systemQualityPublisher = context.socket(ZMQ.PUB);
             ZMQ.Socket intervalSubscriber = context.socket(ZMQ.SUB);
             ZMQ.Socket healthCheckPublisher = context.socket(ZMQ.PUB)) {

            subscriber.connect("tcp://192.168.0.12:5556");
            subscriber.subscribe(sensorType.getBytes());

            systemQualityPublisher.bind("tcp://*:5557");

            intervalSubscriber.connect("tcp://192.168.0.12:5558");
            intervalSubscriber.subscribe("".getBytes());

            healthCheckPublisher.bind("tcp://*:5559");

            while (true) {
                String message = subscriber.recvStr();
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String dateString = dateFormat.format(date);

                System.out.println("[" + dateString + "] [" + processId + "] Mensaje: " + message);

                // Modifica el formato del mensaje enviado al Sistema de Calidad
                message = "[" + dateString + "] [" + processId + "]" + message;

                try (BufferedWriter writer = new BufferedWriter(new FileWriter("reporte.txt", true))) {
                    writer.write(message);
                    writer.newLine();
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (message.contains("Errores")) {
                    String systemQualityMessage = "[" + dateString + "] [" + processId + "] Error en el monitor de " +
                            sensorType + " con valor: " + parseErrorValue(message);
                    systemQualityPublisher.send(systemQualityMessage);
                }

                String intervalMessage = intervalSubscriber.recvStr();
                int sendInterval = Integer.parseInt(intervalMessage);

                // Envía el valor de sendInterval al HealthCheck
                healthCheckPublisher.send(String.valueOf(sendInterval));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String parseErrorValue(String errorMessage) {
        String[] parts = errorMessage.split(" ");

        for(int i = 0; i < parts.length; i++) {
            System.out.println("Parte " + i);
            System.out.println(parts[i]);
        }
        return parts[8];
    }
}