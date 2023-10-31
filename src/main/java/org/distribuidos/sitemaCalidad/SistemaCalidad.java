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

            monitorSubscriber.connect("tcp://192.168.0.12:5557");
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
