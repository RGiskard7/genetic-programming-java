package algoritmogenetico.dominio;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import algoritmogenetico.individuo.IIndividuo;
import algoritmogenetico.individuo.nodo.INodo;
import algoritmogenetico.individuo.nodo.funciones.Funcion;
import algoritmogenetico.individuo.nodo.funciones.FuncionAnd;
import algoritmogenetico.individuo.nodo.funciones.FuncionNot;
import algoritmogenetico.individuo.nodo.funciones.FuncionOr;
import algoritmogenetico.individuo.nodo.funciones.FuncionXor;
import algoritmogenetico.individuo.nodo.terminales.Terminal;
import algoritmogenetico.individuo.nodo.terminales.TerminalBooleano;
import excepciones.ArgsDistintosFuncionesException;

/**
 * Dominio para sintesis de circuitos logicos: dada una tabla de verdad (TSV
 * con la ultima columna como objetivo), busca la expresion logica mas corta
 * que la reproduzca. Fitness = filas correctas - ALPHA * numNodos.
 * Compatible con el mismo {@link algoritmogenetico.AlgoritmoGenetico} sin cambios.
 */
public class DominioBooleano implements IDominio {

	/** Penalizacion por parsimonia (misma constante que en DominioAritmetico). */
	public static final double ALPHA = 0.001;

	/** Filas de la tabla de verdad. Cada mapa tiene nombre_var -> 0.0/1.0, mas "_y" para el objetivo. */
	private final List<Map<String, Double>> filas = new ArrayList<>();
	private String[] nombresVariables;

	@Override
	public List<Terminal> definirConjuntoTerminales(String... terminales) {
		this.nombresVariables = terminales;
		List<Terminal> lista = new ArrayList<>();
		for (String nombre : terminales)
			lista.add(new TerminalBooleano(nombre));
		return lista;
	}

	/**
	 * Soporta los operadores logicos: AND (aridad 2), OR (aridad 2), NOT (aridad 1), XOR (aridad 2).
	 */
	@Override
	public List<Funcion> definirConjuntoFunciones(int[] argumentos, String... funciones)
			throws ArgsDistintosFuncionesException {
		if (argumentos.length != funciones.length)
			throw new ArgsDistintosFuncionesException();
		List<Funcion> lista = new ArrayList<>();
		for (int i = 0; i < funciones.length; i++) {
			switch (funciones[i]) {
				case "AND": lista.add(new FuncionAnd(funciones[i], argumentos[i])); break;
				case "OR":  lista.add(new FuncionOr(funciones[i], argumentos[i])); break;
				case "NOT": lista.add(new FuncionNot(funciones[i], argumentos[i])); break;
				case "XOR": lista.add(new FuncionXor(funciones[i], argumentos[i])); break;
				default:    return null;
			}
		}
		return lista;
	}

	/**
	 * Carga la tabla de verdad desde un fichero TSV. Las columnas coinciden en
	 * orden con los nombres de variables pasados a {@link #definirConjuntoTerminales};
	 * la ultima columna es el objetivo (0.0 o 1.0).
	 */
	@Override
	public void definirValoresPrueba(String ficheroDatos) throws FileNotFoundException, IOException {
		filas.clear();
		try (BufferedReader br = new BufferedReader(new FileReader(ficheroDatos))) {
			String linea;
			while ((linea = br.readLine()) != null) {
				String[] partes = linea.split("\t");
				if (partes.length < 2) continue;
				Map<String, Double> fila = new LinkedHashMap<>();
				int numVars = partes.length - 1;
				for (int i = 0; i < numVars; i++) {
					String nombre = (nombresVariables != null && i < nombresVariables.length)
							? nombresVariables[i] : "v" + i;
					fila.put(nombre, Double.parseDouble(partes[i]));
				}
				fila.put("_y", Double.parseDouble(partes[partes.length - 1]));
				filas.add(fila);
			}
		}
	}

	/**
	 * Cuenta las filas de la tabla de verdad que el individuo predice correctamente.
	 * Fitness = filas_correctas - ALPHA * numNodos.
	 */
	@Override
	public double calcularFitness(IIndividuo individuo) {
		if (filas.isEmpty()) return 0.0;
		int correctas = 0;
		for (Map<String, Double> fila : filas) {
			setValores(individuo.getExpresion(), fila);
			double estimado = individuo.calcularExpresion();
			boolean estimadoBool = estimado > 0.5;
			boolean realBool = fila.get("_y") > 0.5;
			if (estimadoBool == realBool) correctas++;
		}
		double fitness = correctas - ALPHA * individuo.getNumeroNodos();
		individuo.setFitness(fitness);
		return fitness;
	}

	/** Fitness objetivo: predecir todas las filas correctamente. */
	@Override
	public double fitnessBuscado() {
		return filas.size();
	}

	private void setValores(INodo nodo, Map<String, Double> fila) {
		if (nodo instanceof TerminalBooleano) {
			Double v = fila.get(nodo.getSimbolo());
			if (v != null) ((TerminalBooleano) nodo).setValor(v);
		} else {
			for (INodo n : nodo.getDescendientes())
				setValores(n, fila);
		}
	}
}
