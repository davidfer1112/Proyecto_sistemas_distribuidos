package org.distribuidos.Monitor;



import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.zeromq.ZMQ;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.zeromq.ZMQ;

public class Monitor {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Debe proporcionar el tipo de sensor como argumento.");
            return;
        }

        String sensorType = args[0];

        try (ZMQ.Context context = ZMQ.context(1);
             ZMQ.Socket subscriber = context.socket(ZMQ.SUB)) {

            // Cambia la conexión al canal de suscripción para que sea la dirección IP de la computadora que ejecuta el programa 'Sensor'.
            subscriber.connect("tcp://192.168.0.12:5556");
            subscriber.subscribe(sensorType.getBytes());

            // Abre el archivo para escribir
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("reporte.txt", true))) {
                while (true) {
                    // Recibe el mensaje de saludo del canal de suscripción
                    String message = subscriber.recvStr();
                    System.out.println("Mensaje: " + message);

                    // Escribe el mensaje en el archivo
                    writer.write(message);
                    writer.newLine(); // Agrega una nueva línea para separar los mensajes
                    writer.flush(); // Asegura que los datos se escriban en el archivo
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}