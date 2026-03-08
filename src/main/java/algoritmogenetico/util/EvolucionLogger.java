package algoritmogenetico.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Escribe por generacion el mejor fitness, tamano del arbol y expresion en CSV.
 */
public class EvolucionLogger {

	private final BufferedWriter writer;

	public EvolucionLogger(Path outputPath) throws IOException {
		writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8);
		writer.write("generacion,mejorFitness,tamanoArbol,expresion");
		writer.newLine();
	}

	/**
	 * Registra una linea por generacion.
	 *
	 * @param generacion numero de generacion
	 * @param mejorFitness fitness del mejor individuo
	 * @param tamanoMejor numero de nodos del mejor
	 * @param expresionMejor expresion en notacion prefija (se escapa si contiene coma)
	 */
	public void registrar(int generacion, double mejorFitness, int tamanoMejor, String expresionMejor) throws IOException {
		String exp = expresionMejor != null ? expresionMejor : "";
		if (exp.contains(",") || exp.contains("\n")) {
			exp = "\"" + exp.replace("\"", "\"\"") + "\"";
		}
		writer.write(generacion + "," + mejorFitness + "," + tamanoMejor + "," + exp);
		writer.newLine();
	}

	public void cerrar() throws IOException {
		writer.close();
	}
}
