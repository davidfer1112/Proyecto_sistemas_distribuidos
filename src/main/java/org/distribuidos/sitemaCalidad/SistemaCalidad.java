package org.distribuidos.sitemaCalidad;

import org.zeromq.ZMQ;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SistemaCalidad {
    public static void main(String[] args) {
        // Obtén el ID del proceso (PID) del Sistema de Calidad
        String processId = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

        try (ZMQ.Context context = ZMQ.context(1);
             ZMQ.Socket monitorSubscriber = context.socket(ZMQ.SUB)) {

            // Conéctate a todas las direcciones IP en el rango 192.168.0.1 a 192.168.0.255
            for (int i = 1; i <= 255; i++) {
                String ipAddress = "tcp://192.168.0." + i + ":5557";
                monitorSubscriber.connect(ipAddress);
            }

            monitorSubscriber.subscribe("".getBytes());

            while (true) {
                String monitorMessage = monitorSubscriber.recvStr();
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String dateString = dateFormat.format(date);

                // Modifica el formato del mensaje del Sistema de Calidad
                System.out.println(monitorMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
