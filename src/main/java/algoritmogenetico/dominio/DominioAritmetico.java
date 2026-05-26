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
import algoritmogenetico.individuo.nodo.funciones.FuncionDivision;
import algoritmogenetico.individuo.nodo.funciones.FuncionMultiplicacion;
import algoritmogenetico.individuo.nodo.funciones.FuncionNegacion;
import algoritmogenetico.individuo.nodo.funciones.FuncionResta;
import algoritmogenetico.individuo.nodo.funciones.FuncionSeno;
import algoritmogenetico.individuo.nodo.funciones.FuncionCoseno;
import algoritmogenetico.individuo.nodo.funciones.FuncionCuadrado;
import algoritmogenetico.individuo.nodo.funciones.FuncionExp;
import algoritmogenetico.individuo.nodo.funciones.FuncionLog;
import algoritmogenetico.individuo.nodo.funciones.FuncionSqrt;
import algoritmogenetico.individuo.nodo.funciones.FuncionSuma;
import algoritmogenetico.individuo.nodo.funciones.FuncionValorAbsoluto;
import algoritmogenetico.individuo.nodo.terminales.Terminal;
import algoritmogenetico.individuo.nodo.terminales.TerminalAritmetico;
import algoritmogenetico.individuo.nodo.terminales.TerminalConstante;
import excepciones.ArgsDistintosFuncionesException;

/**
 * Dominio para regresión simbólica: evalúa individuos (árboles de expresiones)
 * sobre pares (x, y) leídos de un fichero. Fitness = -RMSE - alpha*nodos.
 * Soporta tanto datasets univariados (2 columnas) como multivariados (N+1 columnas).
 * Implementa {@link IDominio}.
 */
public class DominioAritmetico implements IDominio {
	/** Valor por defecto de la penalización por tamaño (parsimonia). */
	public static final double ALPHA = 0.001;

	/** Penalización por tamaño activa para esta instancia. Configurable vía {@link #setAlpha(double)}. */
	private double alpha = ALPHA;

	private Map<Double, Double> valoresPrueba;
	/** Datos para problemas multivariados: cada mapa tiene las variables + "_y" como clave del objetivo. */
	private final List<Map<String, Double>> filasMultiVar;
	/** Nombres de variables detectados al cargar datos multivariados. Vacío en modo univariado. */
	private String[] nombresVariables = new String[0];
	/** Si true, se ajustan a,b por mínimos cuadrados (a*f(x)+b vs y) antes de calcular RMSE. Por defecto true. */
	private boolean usarEscaladoLineal = true;
	/**
	 * Valor máximo de salida tolerado. Predicciones con |valor| mayor que este
	 * umbral (o no finitas: Infinity, NaN) se sustituyen por este valor antes del
	 * cálculo de RMSE. Evita que un NaN corrompa el fitness de toda la población.
	 * Configurable vía {@link #setValorMaxSalida(double)}.
	 */
	private double valorMaxSalida = 1e9;
	/**
	 * Penalización adicional por inestabilidad numérica. Por cada predicción inválida
	 * (NaN, Infinity o |valor| &gt; valorMaxSalida) se descuenta {@code pesoInestabilidad * (1/numPuntos)}
	 * del fitness, de forma que un individuo totalmente inestable pierde {@code pesoInestabilidad}
	 * puntos adicionales frente a uno estable. Valor 0 (por defecto) desactiva la penalización.
	 * Configurable vía {@link #setPesoInestabilidad(double)}.
	 */
	private double pesoInestabilidad = 0.0;

	/**
	 * Permite instanciar objetos de tipo DominioAritmetico.
	 */
	public DominioAritmetico() {
		valoresPrueba = new LinkedHashMap<>();
		filasMultiVar = new ArrayList<>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.dominio.IDominio#definirConjuntoTerminales(java.lang.String[])
	 */
	@Override
	public List<Terminal> definirConjuntoTerminales(String... terminales) {
		List<Terminal> conjunto = new ArrayList<>();
		for (int i = 0; i < terminales.length; i++)
			conjunto.add(new TerminalAritmetico(terminales[i]));
		return conjunto;
	}

	/**
	 * Define terminales: variables (por nombre) y constantes (por valor). Las constantes no se modifican al evaluar.
	 *
	 * @param nombresVariables nombres de variables (p. ej. "x")
	 * @param constantes valores numericos fijos (p. ej. 1.0, 2.0, -1.0); puede ser null o vacio
	 * @return lista con TerminalAritmetico para cada variable y TerminalConstante para cada constante
	 */
	public List<Terminal> definirConjuntoTerminalesConConstantes(String[] nombresVariables, double[] constantes) {
		List<Terminal> conjunto = new ArrayList<>();
		for (String s : nombresVariables)
			conjunto.add(new TerminalAritmetico(s));
		if (constantes != null) {
			for (double v : constantes)
				conjunto.add(new TerminalConstante(v));
		}
		return conjunto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.dominio.IDominio#definirConjuntoFunciones(int[], java.lang.String[])
	 */
	@Override
	public List<Funcion> definirConjuntoFunciones(int[] argumentos, String... funciones)
			throws ArgsDistintosFuncionesException {
		if (argumentos.length != funciones.length) {
			throw new ArgsDistintosFuncionesException();
		}
		List<Funcion> conjunto = new ArrayList<>();
		for (int i = 0; i < funciones.length; i++) {
			switch (funciones[i]) {
				case "+":   conjunto.add(new FuncionSuma(funciones[i], argumentos[i])); break;
				case "-":   conjunto.add(new FuncionResta(funciones[i], argumentos[i])); break;
				case "*":   conjunto.add(new FuncionMultiplicacion(funciones[i], argumentos[i])); break;
				case "/":   conjunto.add(new FuncionDivision(funciones[i], argumentos[i])); break;
				case "sin": conjunto.add(new FuncionSeno(funciones[i], argumentos[i])); break;
				case "cos": conjunto.add(new FuncionCoseno(funciones[i], argumentos[i])); break;
				case "neg": conjunto.add(new FuncionNegacion(funciones[i], argumentos[i])); break;
				case "abs": conjunto.add(new FuncionValorAbsoluto(funciones[i], argumentos[i])); break;
				case "exp": conjunto.add(new FuncionExp(funciones[i], argumentos[i])); break;
				case "log": conjunto.add(new FuncionLog(funciones[i], argumentos[i])); break;
				case "sqrt": conjunto.add(new FuncionSqrt(funciones[i], argumentos[i])); break;
				case "sqr": conjunto.add(new FuncionCuadrado(funciones[i], argumentos[i])); break;
				default:    return null;
			}
		}
		return conjunto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * algoritmogenetico.dominio.IDominio#definirValoresPrueba(java.lang.String)
	 */
	/**
	 * Carga datos desde un fichero. Acepta TSV (tabulador) o CSV (coma).
	 * Si la primera linea no es numerica, se usa como cabecera de variables.
	 * Para 2 columnas: modo univariado (x, y).
	 * Para 3+ columnas: modo multivariado — todas salvo la última son variables
	 * (nombradas por cabecera, o v0/v1/... si no hay cabecera).
	 */
	@Override
	public void definirValoresPrueba(String ficheroDatos) throws FileNotFoundException, IOException {
		valoresPrueba.clear();
		filasMultiVar.clear();
		nombresVariables = new String[0];
		try (BufferedReader buffer = new BufferedReader(new FileReader(ficheroDatos))) {
			String linea;
			boolean primera = true;
			String[] cabecera = null;
			while ((linea = buffer.readLine()) != null) {
				linea = linea.trim();
				if (linea.isEmpty()) continue;
				String[] partes = linea.contains("\t") ? linea.split("\t") : linea.split(",");
				for (int i = 0; i < partes.length; i++) partes[i] = partes[i].trim();
				if (partes.length < 2) continue;
				if (primera && !esNumerico(partes[0])) {
					cabecera = partes;
					primera = false;
					continue;
				}
				primera = false;
				try {
					if (partes.length == 2) {
						valoresPrueba.put(Double.parseDouble(partes[0]), Double.parseDouble(partes[1]));
					} else {
						int nVars = partes.length - 1;
						if (nombresVariables.length != nVars) {
							if (cabecera != null && cabecera.length > nVars) {
								nombresVariables = new String[nVars];
								System.arraycopy(cabecera, 0, nombresVariables, 0, nVars);
							} else {
								nombresVariables = new String[nVars];
								for (int i = 0; i < nVars; i++) nombresVariables[i] = "v" + i;
							}
						}
						Map<String, Double> fila = new LinkedHashMap<>();
						for (int i = 0; i < nVars; i++)
							fila.put(nombresVariables[i], Double.parseDouble(partes[i]));
						fila.put("_y", Double.parseDouble(partes[nVars]));
						filasMultiVar.add(fila);
					}
				} catch (NumberFormatException e) {
					// ignorar linea mal formada
				}
			}
		}
	}

	/**
	 * Nombres de variables detectados al cargar datos multivariados.
	 * Devuelve array vacío en modo univariado.
	 */
	public String[] getNombresVariables() {
		return nombresVariables.clone();
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

	/**
	 * Carga un fichero de datos multivariado: todas las columnas salvo la ultima
	 * son variables (cuyos nombres se pasan como parametro), y la ultima columna
	 * es el valor objetivo.
	 *
	 * @param ficheroDatos ruta al fichero TSV
	 * @param nombresVariables nombres de las variables (deben coincidir con los terminales)
	 * @throws FileNotFoundException si el fichero no existe
	 * @throws IOException en caso de error de lectura
	 */
	public void definirValoresPruebaMultiVar(String ficheroDatos, String... nombresVariables)
			throws FileNotFoundException, IOException {
		filasMultiVar.clear();
		try (BufferedReader buffer = new BufferedReader(new FileReader(ficheroDatos))) {
			String linea;
			while ((linea = buffer.readLine()) != null) {
				String[] partes = linea.split("\t");
				if (partes.length < nombresVariables.length + 1) continue;
				Map<String, Double> fila = new LinkedHashMap<>();
				for (int i = 0; i < nombresVariables.length; i++) {
					fila.put(nombresVariables[i], Double.parseDouble(partes[i]));
				}
				fila.put("_y", Double.parseDouble(partes[nombresVariables.length]));
				filasMultiVar.add(fila);
			}
		}
	}

	/**
	 * Inyecta el mismo valor a todos los TerminalAritmetico del arbol (modo univariado).
	 * Los TerminalConstante no se modifican porque no extienden TerminalAritmetico.
	 */
	private void setValorTerminales(INodo nodo, double valor) {
		if (nodo instanceof TerminalAritmetico) {
			((TerminalAritmetico) nodo).setValor(valor);
		} else {
			for (INodo n : nodo.getDescendientes())
				setValorTerminales(n, valor);
		}
	}

	/**
	 * Inyecta valores a los TerminalAritmetico por nombre (modo multivariado).
	 * Cada clave del mapa es el simbolo del terminal; los TerminalConstante no se modifican.
	 */
	private void setValorTerminales(INodo nodo, Map<String, Double> valores) {
		if (nodo instanceof TerminalAritmetico) {
			Double v = valores.get(nodo.getSimbolo());
			if (v != null) ((TerminalAritmetico) nodo).setValor(v);
		} else {
			for (INodo n : nodo.getDescendientes())
				setValorTerminales(n, valores);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.dominio.IDominio#calcularFitness(algoritmogenetico.individuo.IIndividuo)
	 */
	/** Activa o desactiva el escalado lineal (a*f(x)+b ajustado por mínimos cuadrados). Por defecto true. */
	public void setUsarEscaladoLineal(boolean usar) {
		this.usarEscaladoLineal = usar;
	}

	public boolean isUsarEscaladoLineal() {
		return usarEscaladoLineal;
	}

	/**
	 * Establece el coeficiente de penalización por tamaño (parsimonia).
	 * Valores más altos favorecen expresiones más pequeñas. Por defecto {@value #ALPHA}.
	 *
	 * @param alpha valor no negativo
	 */
	public void setAlpha(double alpha) {
		this.alpha = Math.max(0, alpha);
	}

	public double getAlpha() {
		return alpha;
	}

	/**
	 * Límite de magnitud para las predicciones del árbol. Valores fuera del rango
	 * [-valorMaxSalida, valorMaxSalida] o no finitos (Infinity, NaN) se clampan a
	 * este valor antes de calcular el RMSE. Por defecto 1e9.
	 *
	 * @param max valor positivo
	 */
	public void setValorMaxSalida(double max) {
		this.valorMaxSalida = Math.abs(max);
	}

	public double getValorMaxSalida() {
		return valorMaxSalida;
	}

	/**
	 * Peso de la penalización por inestabilidad numérica. 0 = desactivado (por defecto).
	 * Un valor de 1.0 descuenta hasta 1 punto extra si el 100% de las predicciones son inválidas.
	 *
	 * @param peso valor no negativo
	 */
	public void setPesoInestabilidad(double peso) {
		this.pesoInestabilidad = Math.max(0, peso);
	}

	public double getPesoInestabilidad() {
		return pesoInestabilidad;
	}

	/** Devuelve true si el valor es NaN, Infinity o supera el umbral de magnitud. */
	private boolean esInvalido(double v) {
		return !Double.isFinite(v) || Math.abs(v) > valorMaxSalida;
	}

	private double clampSalida(double v) {
		if (!Double.isFinite(v)) return valorMaxSalida;
		if (v > valorMaxSalida) return valorMaxSalida;
		if (v < -valorMaxSalida) return -valorMaxSalida;
		return v;
	}

	/**
	 * Calcula el fitness del individuo usando RMSE negado mas parsimonia:
	 * {@code fitness = -RMSE(individuo) - ALPHA * numNodos}.
	 * Si usarEscaladoLineal es true, se ajustan a,b por mínimos cuadrados (a*p_i+b vs y_i) antes del RMSE.
	 * Soporta datos univariados y multivariados.
	 */
	@Override
	public double calcularFitness(IIndividuo individuo) {
		int numPuntos;
		double[] pred;
		double[] real;

		int numInvalidos = 0;

		if (!filasMultiVar.isEmpty()) {
			numPuntos = filasMultiVar.size();
			pred = new double[numPuntos];
			real = new double[numPuntos];
			int i = 0;
			for (Map<String, Double> fila : filasMultiVar) {
				setValorTerminales(individuo.getExpresion(), fila);
				double raw = individuo.calcularExpresion();
				if (esInvalido(raw)) numInvalidos++;
				pred[i] = clampSalida(raw);
				real[i] = fila.get("_y");
				i++;
			}
		} else if (!valoresPrueba.isEmpty()) {
			numPuntos = valoresPrueba.size();
			pred = new double[numPuntos];
			real = new double[numPuntos];
			int i = 0;
			for (Map.Entry<Double, Double> entry : valoresPrueba.entrySet()) {
				setValorTerminales(individuo.getExpresion(), entry.getKey());
				double raw = individuo.calcularExpresion();
				if (esInvalido(raw)) numInvalidos++;
				pred[i] = clampSalida(raw);
				real[i] = entry.getValue();
				i++;
			}
		} else {
			individuo.setFitness(0.0);
			return 0.0;
		}

		individuo.setTieneSingularidades(numInvalidos > 0);
		double[] coef = usarEscaladoLineal ? calcularCoeficientesEscalado(pred, real) : new double[]{1.0, 0.0};
		double a = coef[0];
		double b = coef[1];

		double sumaCuadrados = 0.0;
		for (int i = 0; i < numPuntos; i++) {
			double err = real[i] - (a * pred[i] + b);
			sumaCuadrados += err * err;
		}
		double rmse = Math.sqrt(sumaCuadrados / numPuntos);
		double penalizacion = pesoInestabilidad * ((double) numInvalidos / numPuntos);
		double fitness = -rmse - alpha * individuo.getNumeroNodos() - penalizacion;
		individuo.setFitness(fitness);
		return fitness;
	}

	/**
	 * Calcula los coeficientes a y b del escalado lineal a*pred+b que minimiza
	 * el RMSE contra real, usando mínimos cuadrados ordinarios.
	 *
	 * @return double[]{a, b}; si el denominador es despreciable devuelve {1.0, 0.0}
	 */
	private double[] calcularCoeficientesEscalado(double[] pred, double[] real) {
		int n = pred.length;
		if (n < 2) return new double[]{1.0, 0.0};
		double sumP = 0, sumY = 0, sumPY = 0, sumP2 = 0;
		for (int i = 0; i < n; i++) {
			sumP  += pred[i];
			sumY  += real[i];
			sumPY += pred[i] * real[i];
			sumP2 += pred[i] * pred[i];
		}
		double denom = n * sumP2 - sumP * sumP;
		if (Math.abs(denom) <= 1e-14) return new double[]{1.0, 0.0};
		double a = (n * sumPY - sumP * sumY) / denom;
		double b = (sumY - a * sumP) / n;
		return new double[]{a, b};
	}

	/**
	 * Con fitness RMSE, el valor objetivo es 0.0 (RMSE = 0 significa ajuste perfecto).
	 * El algoritmo para cuando el mejor fitness supera {@code fitnessBuscado - 0.05},
	 * es decir cuando RMSE es inferior a aproximadamente 0.05.
	 */
	@Override
	public double fitnessBuscado() {
		return 0.0;
	}

	/**
	 * Devuelve el mapa de datos de prueba (x, y) para visualizacion. No modificar.
	 *
	 * @return copia del mapa de valores de prueba
	 */
	public Map<Double, Double> getValoresPrueba() {
		return new LinkedHashMap<>(valoresPrueba);
	}

	/** Devuelve true si se cargaron datos multivariados (3+ columnas). */
	public boolean esMultivariado() {
		return !filasMultiVar.isEmpty();
	}

	/**
	 * Calcula pares (predicho escalado, real) para todos los puntos multivariados,
	 * aplicando el mismo escalado lineal que {@link #calcularFitness} si está activo.
	 *
	 * @param individuo individuo a evaluar
	 * @return lista de double[]{predicho, real}; vacía si no hay datos multivariados
	 */
	public List<double[]> getPredicionesYReales(IIndividuo individuo) {
		if (filasMultiVar.isEmpty()) return new ArrayList<>();
		int n = filasMultiVar.size();
		double[] pred = new double[n];
		double[] real = new double[n];
		int i = 0;
		for (Map<String, Double> fila : filasMultiVar) {
			setValorTerminales(individuo.getExpresion(), fila);
			pred[i] = clampSalida(individuo.calcularExpresion());
			real[i] = fila.get("_y");
			i++;
		}
		double[] coef = usarEscaladoLineal ? calcularCoeficientesEscalado(pred, real) : new double[]{1.0, 0.0};
		List<double[]> result = new ArrayList<>();
		for (int j = 0; j < n; j++)
			result.add(new double[]{coef[0] * pred[j] + coef[1], real[j]});
		return result;
	}
}
