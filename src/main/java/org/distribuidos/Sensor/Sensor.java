package org.distribuidos.Sensor;


import org.zeromq.ZMQ;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        String unidad = "";

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

        List<MessageType> messageTypes = new ArrayList<>();

        // Lee el contenido del archivo de configuración
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(configFileName)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Divide la línea en partes usando espacio como separador
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    double probability;
                    try {
                        // Intenta convertir la primera parte a un valor double
                        probability = Double.parseDouble(parts[0]);
                    } catch (NumberFormatException e) {
                        System.out.println("Error al convertir la probabilidad a un número en la línea: " + line);
                        continue; // Salta a la siguiente línea si hay un error
                    }

                    // Añade el tipo de mensaje y su probabilidad a la lista
                    messageTypes.add(new MessageType(parts[1], probability));
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
                // Selecciona un tipo de mensaje aleatorio basado en la probabilidad
                MessageType selectedMessageType = selectMessageType(messageTypes);

                // Genera un valor según el tipo de mensaje y redondea a entero
                int value;
                if (sensorType.equalsIgnoreCase("temperatura")) {
                    // Código existente para el sensor de temperatura
                    if (selectedMessageType.getType().equalsIgnoreCase("Correctos")) {
                        // Valor aleatorio entre 68 y 89 para "Correctos"
                        value = (int) Math.round(68 + new Random().nextDouble() * (89 - 68));
                    } else if (selectedMessageType.getType().equalsIgnoreCase("FueraDeRango")) {
                        // Valor positivo que no esté en el rango (68, 89) para "FueraDeRango"
                        do {
                            value = (int) Math.round(new Random().nextDouble() * 100); // Puedes ajustar el rango según tus necesidades
                        } while (value >= 68 && value <= 89);
                    } else if (selectedMessageType.getType().equalsIgnoreCase("Errores")) {
                        // Valor negativo para "Errores"
                        value = (int) Math.round(-1 * new Random().nextDouble() * 100); // Puedes ajustar el rango según tus necesidades
                    } else {
                        // Tipo desconocido, asigna un valor por defecto
                        value = 0;
                    }
                    unidad = " °F";
                } else if (sensorType.equalsIgnoreCase("oxigeno")) {
                    // Caso para el sensor de oxígeno
                    if (selectedMessageType.getType().equalsIgnoreCase("Correctos")) {
                        // Valor aleatorio entre 2 y 11 para "Correctos"
                        value = (int) Math.round(2 + new Random().nextDouble() * (11 - 2));
                    } else if (selectedMessageType.getType().equalsIgnoreCase("FueraDeRango")) {
                        // Valor positivo que no esté en el rango (2, 11) para "FueraDeRango"
                        do {
                            value = (int) Math.round(new Random().nextDouble() * 100); // Puedes ajustar el rango según tus necesidades
                        } while (value >= 2 && value <= 11);
                    } else if (selectedMessageType.getType().equalsIgnoreCase("Errores")) {
                        // Valor negativo para "Errores"
                        value = (int) Math.round(-1 * new Random().nextDouble() * 100); // Puedes ajustar el rango según tus necesidades
                    } else {
                        // Tipo desconocido, asigna un valor por defecto
                        value = 0;
                    }
                    unidad = " Mg/L";
                }else if (sensorType.equalsIgnoreCase("ph")) {
                    // Caso para el sensor de pH
                    if (selectedMessageType.getType().equalsIgnoreCase("Correctos")) {
                        // Valor aleatorio entre 6 y 8 para "Correctos"
                        value = (int) (6 + Math.round(new Random().nextDouble() * (8 - 6)));
                    } else if (selectedMessageType.getType().equalsIgnoreCase("FueraDeRango")) {
                        // Valor positivo entre 0 y 9 para "FueraDeRango"
                        value = (int) Math.round(new Random().nextDouble() * 9);
                    } else if (selectedMessageType.getType().equalsIgnoreCase("Errores")) {
                        // Valor negativo entre -9 y 0 para "Errores"
                        value = (int) Math.round(-9 + new Random().nextDouble() * 9);
                    } else {
                        // Tipo desconocido, asigna un valor por defecto
                        value = 0;
                    }
                    unidad = "";
                }else {
                    // Tipo de sensor desconocido
                    System.out.println("Tipo de sensor desconocido: " + sensorType);
                    return;
                }
                // Forma el mensaje final


                String message = sensorType + " de tipo: " + selectedMessageType.getType() + " con valor: " + value + unidad;

                // Envía el mensaje
                publisher.send(message);

                // Espera antes de enviar el siguiente mensaje
                Thread.sleep(sendInterval);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para seleccionar un tipo de mensaje aleatorio basado en la probabilidad
    private static MessageType selectMessageType(List<MessageType> messageTypes) {
        double randomValue = new Random().nextDouble();
        double cumulativeProbability = 0.0;

        for (MessageType messageType : messageTypes) {
            cumulativeProbability += messageType.getProbability();
            if (randomValue < cumulativeProbability) {
                return messageType;
            }
        }

        // En caso de errores o si no se seleccionó ningún tipo
        return new MessageType("Desconocido", 0.0);
    }

    // Clase para representar un tipo de mensaje y su probabilidad
    private static class MessageType {
        private String type;
        private double probability;

        public MessageType(String type, double probability) {
            this.type = type;
            this.probability = probability;
        }

        public String getType() {
            return type;
        }

        public double getProbability() {
            return probability;
        }
    }
}