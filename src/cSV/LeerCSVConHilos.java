package cSV;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LeerCSVConHilos {

	private String archivoCSV;
	private String separador;
	private String idBuscado;
	private int numHilos;

	public LeerCSVConHilos(String archivoCSV, String separador, String idBuscado, int numHilos) {
		this.archivoCSV = archivoCSV;
		this.separador = separador;
		this.idBuscado = idBuscado;
		this.numHilos = numHilos;
	}

	public void buscarID() {
		try (BufferedReader br = new BufferedReader(new FileReader(archivoCSV))) {
			// Leer el archivo una vez para obtener el número total de líneas
			int totalLineas = 0;
			while (br.readLine() != null) totalLineas++;

			// Calcular cuántas líneas corresponden a cada hilo
			int lineasPorHilo = totalLineas / numHilos;

			// Crear los hilos y asignarles las líneas correspondientes
			List<BuscarIDThread> hilos = new ArrayList<>();
			for (int i = 0; i < numHilos; i++) {
				int inicio = i * lineasPorHilo;
				int fin = (i == numHilos - 1) ? totalLineas : (i + 1) * lineasPorHilo;
				BuscarIDThread hilo = new BuscarIDThread(archivoCSV, separador, idBuscado, inicio, fin);
				hilos.add(hilo);
				hilo.start();
			}

			// Esperar a que todos los hilos terminen
			for (BuscarIDThread hilo : hilos) {
				hilo.join();
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static class BuscarIDThread extends Thread {

		private String archivoCSV;
		private String separador;
		private String idBuscado;
		private int inicio;
		private int fin;

		public BuscarIDThread(String archivoCSV, String separador, String idBuscado, int inicio, int fin) {
			this.archivoCSV = archivoCSV;
			this.separador = separador;
			this.idBuscado = idBuscado;
			this.inicio = inicio;
			this.fin = fin;
		}

		@Override
		public void run() {
			try (BufferedReader br = new BufferedReader(new FileReader(archivoCSV))) {
				String linea;
				int contador = 0;

				while ((linea = br.readLine()) != null && contador < fin) {
					if (contador >= inicio) {
						String[] valores = linea.split(separador);
						if (valores[0].equals(idBuscado)) {
							System.out.println(linea);
							return;
						}
					}
					contador++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
	    String archivoCSV = "src/cSV/male_teams.csv";
	    String separador = ",";
	    Scanner scanner = new Scanner(System.in);

	    System.out.print("Ingrese el ID que desea buscar en el archivo CSV: ");
	    String idBuscado = scanner.nextLine();
	    

	    int numHilos = 4;

	    LeerCSVConHilos buscador = new LeerCSVConHilos(archivoCSV, separador, idBuscado, numHilos);
	    buscador.buscarID();
	}
}



