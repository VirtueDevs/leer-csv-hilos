package cSV;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class leerCSV extends Thread {

    private String archivoCSV;
    private String separador;
    private String idBuscado;
    int startLine;
    int endLine;

    public leerCSV(String archivoCSV, String separador, String idBuscado, int startLine, int endLine) {
        this.archivoCSV = archivoCSV;
        this.separador = separador;
        this.idBuscado = idBuscado;
        this.startLine = startLine;
        this.endLine = endLine;
    }

    public void run() {
        try (BufferedReader br = new BufferedReader(new FileReader(archivoCSV))) {
            String linea;
            int lineNumber = 0;
            String coincidencia = null; // Variable para almacenar la coincidencia

            while ((linea = br.readLine()) != null) {
                lineNumber++;
                if (lineNumber < startLine || lineNumber > endLine) {
                    // Saltar esta línea si está fuera del rango del hilo
                    continue;
                }
                String[] valores = linea.split(separador);
                if (valores[0].equals(idBuscado)) {
                    // Almacenar la coincidencia en la variable
                    coincidencia = linea;
                }
            }

            // Imprimir la coincidencia si se encontró
            if (coincidencia != null) {
                System.out.println(coincidencia);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String archivoCSV = "src/cSV/male_teams.csv";
        String separador = ",";
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese el ID que desea buscar en el archivo CSV: ");
        String idBuscado = scanner.nextLine();

        int cores = Runtime.getRuntime().availableProcessors();
        List<leerCSV> hilos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivoCSV))) {
            String linea;
            int totalLines = 0;

            // Obtener la cantidad total de líneas en el archivo
            while ((linea = br.readLine()) != null) {
                totalLines++;
            }

            // Calcular la cantidad de líneas que procesará cada hilo
            int linesPerThread = totalLines / cores;

         // Crear hilos y asignar líneas a procesar
            int linesProcessed = 0;
            for (int i = 0; i < cores; i++) {
                int startIndex = linesProcessed;
                int linesToProcess = linesPerThread;
                if (i == cores - 1) {
                    // El último hilo procesa las líneas restantes
                    linesToProcess = totalLines - linesProcessed;
                }
                int endIndex = startIndex + linesToProcess - 1;
                hilos.add(new leerCSV(archivoCSV, separador, idBuscado, startIndex, endIndex));
                hilos.get(i).start();
                linesProcessed += linesToProcess;
            }

            // Esperar a que todos los hilos terminen
            for (leerCSV hilo : hilos) {
                hilo.join();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
