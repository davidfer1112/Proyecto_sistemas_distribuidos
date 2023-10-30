package org.distribuidos.sitemaCalidad;

import org.zeromq.ZMQ;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SistemaCalidad {
    public static void main(String[] args) {
        try (ZMQ.Context context = ZMQ.context(1);
             ZMQ.Socket monitorSubscriber = context.socket(ZMQ.SUB)) {

            // Cambia la conexión al canal de publicación del Monitor
            monitorSubscriber.connect("tcp://192.168.0.12:5557"); // Ajusta la dirección y el puerto según el Monitor
            monitorSubscriber.subscribe("".getBytes());

            while (true) {
                // Recibe mensajes del Monitor
                String monitorMessage = monitorSubscriber.recvStr();
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String dateString = dateFormat.format(date);

                // Procesa el mensaje recibido (puedes personalizar esto según tus necesidades)
                processMonitorMessage(dateString, monitorMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processMonitorMessage(String dateString, String monitorMessage) {
        // Analiza el mensaje del Monitor y realiza las acciones necesarias
        System.out.println("[" + dateString + "] " + monitorMessage);
        // Puedes agregar lógica adicional aquí, como enviar notificaciones, generar informes, etc.
    }
}
