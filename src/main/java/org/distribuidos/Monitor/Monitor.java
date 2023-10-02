package org.distribuidos.Monitor;


import org.distribuidos.Mensaje.Mensaje;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Monitor {
    public static void main(String[] args) {
        int monitorPort = 12345; // Mismo puerto que el sensor

        try (ServerSocket serverSocket = new ServerSocket(monitorPort);
             Socket socket = serverSocket.accept();
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            while (true) {
                // Recibir el mensaje del sensor
                Mensaje mensaje = (Mensaje) in.readObject();

                // Mostrar el mensaje por pantalla
                System.out.println("Medida: " + mensaje.getMedida() + " Hora: " + mensaje.getHora());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}