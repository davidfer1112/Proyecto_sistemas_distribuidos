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

            // Obtener el texto del primer argumento
            String textoArgumento = "";
            if (args.length > 0) {
                textoArgumento = args[0];
            }

            // Imprimir el saludo con el primer argumento
            System.out.println("Hola " + textoArgumento);

            // Obtener el segundo argumento como número
            int numeroArgumento = 0;
            if (args.length > 1) {
                numeroArgumento = Integer.parseInt(args[1]);
            }

            // Sumar el segundo argumento con 10 y mostrar el resultado
            int resultado = numeroArgumento + 10;
            System.out.println("El resultado de sumar " + numeroArgumento + " con 10 es: " + resultado);

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