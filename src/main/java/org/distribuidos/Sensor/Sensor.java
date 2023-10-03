package org.distribuidos.Sensor;


import org.zeromq.ZMQ;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

        // Verifica que el tipo de sensor sea temperatura, oxigeno o PH
        if (!sensorType.equalsIgnoreCase("temperatura") &&
                !sensorType.equalsIgnoreCase("oxigeno") &&
                !sensorType.equalsIgnoreCase("ph")) {
            System.out.println("Solo se aceptan 'temperatura', 'oxigeno' o 'ph' como valores para el tipo de sensor.");
            return; // Sale del programa si el tipo de sensor no es válido
        }

        // Verifica que el segundo argumento sea un entero válido
        try {
            sendInterval = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("El segundo argumento (Tiempo) debe ser un número entero válido.");
            return; // Sale del programa si el segundo argumento no es un entero válido
        }

        // Verifica que el archivo de configuración exista
        File configFile = new File(configFileName);
        if (!configFile.exists()) {
            System.out.println("No se encontró el archivo de configuración: " + configFileName);
            return; // Sale del programa si el archivo de configuración no existe
        }



        // Lee el contenido del archivo de configuración
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Divide la línea en partes usando espacio como separador
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    double value;
                    try {
                        // Intenta convertir la primera parte a un valor double
                        value = Double.parseDouble(parts[0]);
                    } catch (NumberFormatException e) {
                        System.out.println("Error al convertir el valor a un número en la línea: " + line);
                        continue; // Salta a la siguiente línea si hay un error
                    }

                    // Muestra el valor y el tipo por consola
                    System.out.println("Valor: " + value + ", Tipo: " + parts[1]);
                } else {
                    System.out.println("Formato incorrecto en la línea: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Resto del código del programa
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