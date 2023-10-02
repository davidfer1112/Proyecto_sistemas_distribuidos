package org.distribuidos.Sensor;

import org.distribuidos.Mensaje.Mensaje;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

public class Sensor {
    public static void main(String[] args) {
        String monitorHost = "localhost"; // Cambia esto con la dirección real del monitor
        int monitorPort = 12345; // Cambia esto con el puerto real del monitor

        try (Socket socket = new Socket(monitorHost, monitorPort);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            while (true) {
                // Generar alguna medición (simulado aquí con un número aleatorio)
                double medida = Math.random() * 100;

                // Crear un objeto Mensaje con la medición y la hora actual
                Mensaje mensaje = new Mensaje(medida, new Date());

                // Enviar el mensaje al monitor
                out.writeObject(mensaje);
                out.flush();

                // Esperar el tiempo especificado antes de enviar el siguiente mensaje
                Thread.sleep(5000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}