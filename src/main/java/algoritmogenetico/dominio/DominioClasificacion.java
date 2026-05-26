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
import algoritmogenetico.individuo.nodo.terminales.Terminal;
import algoritmogenetico.individuo.nodo.terminales.TerminalAritmetico;
import algoritmogenetico.individuo.nodo.terminales.TerminalConstante;
import excepciones.ArgsDistintosFuncionesException;

/**
 * Dominio para clasificacion binaria: cada fila tiene N entradas numericas y
 * una etiqueta 0.0 o 1.0. El individuo evalua a un numero; se considera
 * clase 1 si &gt; 0.5, sino clase 0. Fitness = precision - ALPHA*nodos.
 * Acepta CSV/TSV con o sin cabecera. Implementa {@link IDominio}.
 */
public class DominioClasificacion implements IDominio {

	/** Valor por defecto de la penalización por tamaño (parsimonia). */
	public static final double ALPHA = 0.001;

	/** Penalización por tamaño activa para esta instancia. Configurable vía {@link #setAlpha(double)}. */
	private double alpha = ALPHA;

	private final List<Map<String, Double>> filas = new ArrayList<>();
	private String[] nombresVariables = new String[0];

	@Override
	public List<Terminal> definirConjuntoTerminales(String... terminales) {
		List<Terminal> lista = new ArrayList<>();
		for (String s : terminales)
			lista.add(new TerminalAritmetico(s));
		return lista;
	}

	/** Terminales con variables y constantes opcionales (igual que en DominioAritmetico). */
	public List<Terminal> definirConjuntoTerminalesConConstantes(String[] nombresVariables, double[] constantes) {
		List<Terminal> lista = new ArrayList<>();
		for (String s : nombresVariables)
			lista.add(new TerminalAritmetico(s));
		if (constantes != null)
			for (double v : constantes)
				lista.add(new TerminalConstante(v));
		return lista;
	}

	@Override
	public List<algoritmogenetico.individuo.nodo.funciones.Funcion> definirConjuntoFunciones(int[] argumentos, String... funciones)
			throws ArgsDistintosFuncionesException {
		DominioAritmetico aux = new DominioAritmetico();
		return aux.definirConjuntoFunciones(argumentos, funciones);
	}

	/**
	 * Carga el fichero: columnas numericas + ultima columna clase (0.0 o 1.0).
	 * Si la primera linea no es numerica, se usa como nombres de variables.
	 */
	@Override
	public void definirValoresPrueba(String ficheroDatos) throws FileNotFoundException, IOException {
		filas.clear();
		try (BufferedReader br = new BufferedReader(new FileReader(ficheroDatos))) {
			String linea;
			boolean primera = true;
			while ((linea = br.readLine()) != null) {
				linea = linea.trim();
				if (linea.isEmpty()) continue;
				String[] partes = linea.contains("\t") ? linea.split("\t") : linea.split(",");
				for (int i = 0; i < partes.length; i++) partes[i] = partes[i].trim();
				if (partes.length < 2) continue;
				if (primera && !esNumerico(partes[0])) {
					nombresVariables = new String[partes.length - 1];
					for (int i = 0; i < nombresVariables.length; i++)
						nombresVariables[i] = partes[i];
					primera = false;
					continue;
				}
				primera = false;
				try {
					Map<String, Double> fila = new LinkedHashMap<>();
					int n = partes.length - 1;
					if (nombresVariables.length != n)
						nombresVariables = nombresVarsPorDefecto(n);
					for (int i = 0; i < n; i++)
						fila.put(nombresVariables[i], Double.parseDouble(partes[i]));
					fila.put("_y", Double.parseDouble(partes[n]));
					filas.add(fila);
				} catch (NumberFormatException e) {
					// ignorar linea
				}
			}
			if (nombresVariables.length == 0 && !filas.isEmpty())
				nombresVariables = nombresVarsPorDefecto(filas.get(0).size() - 1);
		}
	}

	private static boolean esNumerico(String s) {
		if (s == null || s.isEmpty()) return false;
		try {
			Double.parseDouble(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private static String[] nombresVarsPorDefecto(int n) {
		String[] v = new String[n];
		for (int i = 0; i < n; i++) v[i] = "v" + i;
		return v;
	}

	/** Nombres de variables detectados al cargar (cabecera o v0, v1, ...). */
	public String[] getNombresVariables() {
		return nombresVariables.clone();
	}

	@Override
	public double calcularFitness(IIndividuo individuo) {
		if (filas.isEmpty()) return 0.0;
		int correctas = 0;
		boolean hayInvalidos = false;
		for (Map<String, Double> fila : filas) {
			setValores(individuo.getExpresion(), fila);
			double out = individuo.calcularExpresion();
			if (!Double.isFinite(out)) {
				hayInvalidos = true;
				continue; // salida inestable → cuenta como error
			}
			boolean pred = out > 0.5;
			boolean real = fila.get("_y") > 0.5;
			if (pred == real) correctas++;
		}
		individuo.setTieneSingularidades(hayInvalidos);
		double accuracy = (double) correctas / filas.size();
		double fitness = accuracy - alpha * individuo.getNumeroNodos();
		individuo.setFitness(fitness);
		return fitness;
	}

	@Override
	public double fitnessBuscado() {
		return 1.0;
	}

	/**
	 * Establece el coeficiente de penalización por tamaño (parsimonia).
	 * Por defecto {@value #ALPHA}.
	 *
	 * @param alpha valor no negativo
	 */
	public void setAlpha(double alpha) {
		this.alpha = Math.max(0, alpha);
	}

	public double getAlpha() {
		return alpha;
	}

	private void setValores(INodo nodo, Map<String, Double> fila) {
		if (nodo instanceof TerminalAritmetico) {
			Double v = fila.get(nodo.getSimbolo());
			if (v != null) ((TerminalAritmetico) nodo).setValor(v);
		} else {
			for (INodo n : nodo.getDescendientes())
				setValores(n, fila);
		}
	}
}
