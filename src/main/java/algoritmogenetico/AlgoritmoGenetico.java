package algoritmogenetico;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import algoritmogenetico.dominio.DominioClasificacion;
import algoritmogenetico.dominio.IDominio;
import algoritmogenetico.individuo.IIndividuo;
import algoritmogenetico.individuo.Individuo;
import algoritmogenetico.individuo.nodo.INodo;
import algoritmogenetico.individuo.nodo.funciones.Funcion;
import algoritmogenetico.individuo.nodo.terminales.Terminal;
import algoritmogenetico.individuo.nodo.terminales.TerminalConstante;
import algoritmogenetico.operadores.OperadorCruce;
import algoritmogenetico.operadores.Selector;
import algoritmogenetico.operadores.SelectorRanking;
import algoritmogenetico.operadores.SelectorTorneo;
import algoritmogenetico.util.EvolucionLogger;
import algoritmogenetico.util.GpLogger;
import algoritmogenetico.util.ResultadoEjecucion;
import algoritmogenetico.util.SimplificadorExpresion;
import algoritmogenetico.util.UtilExpresion;
import excepciones.ArgsDistintosFuncionesException;
import excepciones.CruceNuloException;

import java.util.function.BiConsumer;

/**
 * Implementación del algoritmo de programación genética: población de árboles de
 * expresiones, selección por torneo o ranking, cruce por subárbol (con reintentos ante
 * cruce nulo), mutación (subárbol, punto, contracción) y elitismo. Implementa la interfaz {@link IAlgoritmo}.
 */
public class AlgoritmoGenetico implements IAlgoritmo {

	/** Tipo de selección de padres. */
	public enum TipoSeleccion { TORNEO, RANKING }

	private static final int DEFAULT_MAX_PROFUNDIDAD_INDIVIDUO = 6;

	private final int tamanioPoblacion;
	private final int profundidad;
	private final int probabilidadCruce;
	private final int valorTorneo;
	private final int maxGeneraciones;
	private final double probabilidadMutacion;
	private final int maxProfundidadIndividuo;
	private final Random random;

	// Constantes del algoritmo configurables (con valores por defecto razonables)
	private int maxReintentoCruce = 50;
	private int profundidadSubarbolMutacion = 2;
	/** Amplitud de perturbación para constante: delta en [-perturbConstante, perturbConstante]. */
	private double perturbConstante = 0.5;

	private EvolucionLogger evolucionLogger;
	private BiConsumer<Integer, IIndividuo> generacionListener;

	/** Resultado de la última ejecución (null si aún no se ha ejecutado). */
	private ResultadoEjecucion ultimoResultado;

	/** Limite de nodos por individuo (0 = sin limite). */
	private int maxNodosIndividuo = 0;
	/** Parar si el mejor fitness no mejora en tantas generaciones (0 = desactivado). */
	private int generacionesSinMejoraParaParar = 0;

	/** Prob. mutación subárbol (sustituir subárbol aleatorio). Por defecto 1.0. */
	private double probMutacionSubarbol = 1.0;
	/** Prob. mutación punto (cambiar terminal o función por otra de misma aridad). Por defecto 0. */
	private double probMutacionPunto = 0.0;
	/** Prob. mutación contracción (sustituir subárbol por terminal). Por defecto 0. */
	private double probMutacionContraccion = 0.0;

	private List<IIndividuo> poblacion;
	private List<Terminal> terminales;
	private List<Funcion> funciones;

	private Selector selector;
	private OperadorCruce operadorCruce;

	/**
	 * Permite crear una nueva instancia de tipo AlgoritmoGenetico con probabilidad
	 * de mutacion y semilla para reproducibilidad.
	 *
	 * @param tamanioPoblacion el tamanio de las poblaciones
	 * @param maxGeneraciones el numero maximo de generaciones que hara el algoritmo
	 * @param profundidadArbol la profundidad de las expresiones de los individuos de la generacion inicial
	 * @param probabilidadCruce el porcentaje de la poblacion que sera cruzada geneticamente
	 * @param valorTorneo el numero de individuos que competiran en el torneo para el cruce
	 * @param probabilidadMutacion probabilidad de mutar cada descendiente (0.0 a 1.0)
	 * @param semilla semilla para el generador aleatorio (null para no fijar semilla)
	 */
	public AlgoritmoGenetico(int tamanioPoblacion, int maxGeneraciones, int profundidadArbol, int probabilidadCruce,
			int valorTorneo, double probabilidadMutacion, Long semilla) {
		this(tamanioPoblacion, maxGeneraciones, profundidadArbol, probabilidadCruce, valorTorneo, probabilidadMutacion, semilla, DEFAULT_MAX_PROFUNDIDAD_INDIVIDUO);
	}

	public AlgoritmoGenetico(int tamanioPoblacion, int maxGeneraciones, int profundidadArbol, int probabilidadCruce,
			int valorTorneo, double probabilidadMutacion, Long semilla, int maxProfundidadIndividuo) {
		this.tamanioPoblacion = tamanioPoblacion;
		this.profundidad = profundidadArbol;
		this.probabilidadCruce = probabilidadCruce;
		this.valorTorneo = valorTorneo;
		this.maxGeneraciones = maxGeneraciones;
		this.probabilidadMutacion = probabilidadMutacion;
		this.maxProfundidadIndividuo = maxProfundidadIndividuo;
		this.random = semilla != null ? new Random(semilla) : new Random();
		this.selector = new SelectorTorneo(valorTorneo, this.random);
		this.operadorCruce = new OperadorCruce(maxProfundidadIndividuo, this.random);
	}

	/**
	 * Permite crear una nueva instancia de tipo AlgoritmoGenetico con probabilidad
	 * de mutacion.
	 *
	 * @param tamanioPoblacion el tamanio de las poblaciones
	 * @param maxGeneraciones el numero maximo de generaciones que hara el algoritmo
	 * @param profundidadArbol la profundidad de las expresiones de los individuos de la generacion inicial
	 * @param probabilidadCruce el porcentaje de la poblacion que sera cruzada geneticamente
	 * @param valorTorneo el numero de individuos que competiran en el torneo para el cruce
	 * @param probabilidadMutacion probabilidad de mutar cada descendiente (0.0 a 1.0)
	 */
	public AlgoritmoGenetico(int tamanioPoblacion, int maxGeneraciones, int profundidadArbol, int probabilidadCruce,
			int valorTorneo, double probabilidadMutacion) {
		this(tamanioPoblacion, maxGeneraciones, profundidadArbol, probabilidadCruce, valorTorneo, probabilidadMutacion, null);
	}

	/**
	 * Permite crear una nueva instancia de tipo AlgoritmoGenetico (sin mutacion).
	 *
	 * @param tamanioPoblacion el tamanio de las poblaciones
	 * @param maxGeneraciones el numero maximo de generaciones que hara el algoritmo
	 * @param profundidadArbol la profundidad de las expresiones de los individuos de la generacion inicial
	 * @param probabilidadCruce el porcentaje de la poblacion que sera cruzada geneticamente
	 * @param valorTorneo el numero de individuos que competiran en el torneo para el cruce
	 */
	public AlgoritmoGenetico(int tamanioPoblacion, int maxGeneraciones, int profundidadArbol, int probabilidadCruce,
			int valorTorneo) {
		this(tamanioPoblacion, maxGeneraciones, profundidadArbol, probabilidadCruce, valorTorneo, 0.0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see algoritmogenetico.IAlgoritmo#defineConjuntoTerminales(java.util.List)
	 */
	@Override
	public void defineConjuntoTerminales(List<Terminal> terminales) {
		this.terminales = terminales;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see algoritmogenetico.IAlgoritmo#defineConjuntoFunciones(java.util.List)
	 */
	@Override
	public void defineConjuntoFunciones(List<Funcion> funciones) throws ArgsDistintosFuncionesException {
		this.funciones = funciones;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see algoritmogenetico.IAlgoritmo#crearPoblacion()
	 */
	/**
	 * Crea la poblacion inicial usando el metodo "ramped half-and-half":
	 * la primera mitad se genera con "full" (profundidad exacta) y la segunda
	 * mitad con "grow" (profundidad variable). Esto maximiza la diversidad inicial,
	 * tal como propone Koza (1992).
	 */
	@Override
	public void crearPoblacion() {
		poblacion = new ArrayList<>();
		int mitad = tamanioPoblacion / 2;
		for (int i = 0; i < tamanioPoblacion; i++) {
			Individuo individuo = new Individuo();
			if (i < mitad) {
				individuo.crearIndividuoAleatorio(profundidad, terminales, funciones, random);
			} else {
				individuo.crearIndividuoAleatorioGrow(profundidad, terminales, funciones, random);
			}
			poblacion.add(individuo);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see algoritmogenetico.IAlgoritmo#cruce(algoritmogenetico.individuo.IIndividuo, algoritmogenetico.individuo.IIndividuo)
	 */
	@Override
	public List<IIndividuo> cruce(IIndividuo prog1, IIndividuo prog2) throws CruceNuloException {
		return operadorCruce.cruzar(prog1, prog2);
	}

	/**
	 * Calcula el fitness de todos los individuos de la poblacion actual.
	 *
	 * @param dominio el dominio que se quiere usar para evaluar a los individuos
	 */
	private void calcularFitnessPoblacion(IDominio dominio) {
		for (IIndividuo indiv : poblacion) {
			dominio.calcularFitness(indiv);
		}
	}

	/**
	 * Devuelve el individuo con mejor fitness de la poblacion actual.
	 *
	 * @return el individuo con el mejor fitness
	 */
	private IIndividuo bestFitness() {
		return Collections.max(poblacion, Selector.CMP_FITNESS);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see algoritmogenetico.IAlgoritmo#crearNuevaPoblacion()
	 */
	@Override
	public void crearNuevaPoblacion() {
		ArrayList<IIndividuo> nuevaPoblacion = new ArrayList<>();
		int referencia = (int) (tamanioPoblacion * ((double) (100 - probabilidadCruce) / 100));

		Collections.swap(poblacion, poblacion.indexOf(bestFitness()), 0);

		for (int i = 0; i < referencia; i++) {
			nuevaPoblacion.add(poblacion.get(i));
		}

		while (nuevaPoblacion.size() < tamanioPoblacion) {
			List<IIndividuo> ganadores = selector.seleccionar(poblacion);
			boolean cruzados = false;
			for (int reintento = 0; reintento < maxReintentoCruce && !cruzados; reintento++) {
				try {
					List<IIndividuo> descendientes = cruce(ganadores.get(0), ganadores.get(1));
					for (IIndividuo d : descendientes) {
						if (nuevaPoblacion.size() >= tamanioPoblacion) break;
						IIndividuo aAnadir = (random.nextDouble() < probabilidadMutacion) ? mutar(d) : d;
						nuevaPoblacion.add(aAnadir);
					}
					cruzados = true;
				} catch (CruceNuloException e) {
					if (reintento == maxReintentoCruce - 1) {
						nuevaPoblacion.add(copiarIndividuo(ganadores.get(0)));
						if (nuevaPoblacion.size() < tamanioPoblacion) {
							nuevaPoblacion.add(copiarIndividuo(ganadores.get(1)));
						}
					}
				}
			}
		}

		poblacion = nuevaPoblacion;
	}

	private IIndividuo copiarIndividuo(IIndividuo orig) {
		Individuo copia = new Individuo();
		copia.setExpresion(orig.getExpresion().copy());
		copia.setFitness(orig.getFitness());
		return copia;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see algoritmogenetico.IAlgoritmo#mutar(algoritmogenetico.individuo.IIndividuo)
	 */
	@Override
	public IIndividuo mutar(IIndividuo ind) {
		Individuo copia = new Individuo();
		copia.setExpresion(ind.getExpresion().copy());
		copia.etiquetaNodos();
		int numNodos = copia.getNumeroNodos();
		if (numNodos < 2) {
			return copia;
		}
		double total = probMutacionSubarbol + probMutacionPunto + probMutacionContraccion;
		double r = total > 0 ? random.nextDouble() * total : 0;
		boolean hacerSubarbol = r < probMutacionSubarbol;
		boolean hacerPunto = !hacerSubarbol && r < probMutacionSubarbol + probMutacionPunto;
		boolean hacerContraccion = !hacerSubarbol && !hacerPunto && probMutacionContraccion > 0;

		int etiquetaElegida = 1 + random.nextInt(numNodos);
		INodo nodo = copia.getNodosEtiquetados().get(etiquetaElegida);
		if (nodo == null) return copia;

		if (hacerContraccion) {
			List<Integer> noTerminales = new ArrayList<>();
			for (Map.Entry<Integer, INodo> e : copia.getNodosEtiquetados().entrySet()) {
				if (!e.getValue().getDescendientes().isEmpty()) noTerminales.add(e.getKey());
			}
			if (noTerminales.isEmpty()) return copia;
			etiquetaElegida = noTerminales.get(random.nextInt(noTerminales.size()));
			INodo terminal = terminales.get(random.nextInt(terminales.size())).copy();
			copia.reemplazarNodo(etiquetaElegida, terminal);
		} else if (hacerPunto) {
			if (nodo.getDescendientes().isEmpty()) {
				if (nodo instanceof TerminalConstante) {
					double v = ((TerminalConstante) nodo).getValor();
					double delta = (random.nextDouble() * 2 - 1) * perturbConstante;
					copia.reemplazarNodo(etiquetaElegida, new TerminalConstante(v + delta));
				} else {
					INodo nuevoTerm = terminales.get(random.nextInt(terminales.size())).copy();
					copia.reemplazarNodo(etiquetaElegida, nuevoTerm);
				}
			} else {
				int aridad = ((Funcion) nodo).getNumArgu();
				List<Funcion> mismasAridad = new ArrayList<>();
				for (Funcion f : funciones) { if (f.getNumArgu() == aridad) mismasAridad.add(f); }
				if (mismasAridad.isEmpty()) return copia;
				Funcion nuevaFunc = (Funcion) mismasAridad.get(random.nextInt(mismasAridad.size())).copy();
				for (INodo hijo : nodo.getDescendientes()) nuevaFunc.incluirDescendiente(hijo);
				copia.reemplazarNodo(etiquetaElegida, nuevaFunc);
			}
		} else {
			Individuo aux = new Individuo();
			INodo nuevoSubarbol = aux.crearSubarbolAleatorio(profundidadSubarbolMutacion, terminales, funciones, random);
			copia.reemplazarNodo(etiquetaElegida, nuevoSubarbol);
		}

		if (copia.getProfundidad() > maxProfundidadIndividuo) {
			return copiarIndividuo(ind);
		}
		if (maxNodosIndividuo > 0 && copia.getNumeroNodos() > maxNodosIndividuo) {
			return copiarIndividuo(ind);
		}
		return copia;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see algoritmogenetico.IAlgoritmo#ejecutar(algoritmogenetico.dominio.IDominio)
	 */
	@Override
	public void ejecutar(IDominio dominio) {
		IIndividuo mejorIndiv = null;
		double mejorFitnessAnterior = Double.NEGATIVE_INFINITY;
		int generacionesSinMejora = 0;
		int generacionFinal = maxGeneraciones;
		boolean objetivoAlcanzado = false;

		crearPoblacion();

		for (int i = 0; i < maxGeneraciones; i++) {
			GpLogger.info(">>GENERACION: " + (i + 1));
			calcularFitnessPoblacion(dominio);
			mejorIndiv = bestFitness();
			if (evolucionLogger != null) {
				try {
					String exp = mejorIndiv.getExpresion() != null ? mejorIndiv.getExpresion().toString() : "";
					evolucionLogger.registrar(i + 1, mejorIndiv.getFitness(), mejorIndiv.getNumeroNodos(), exp);
				} catch (Exception e) {
					GpLogger.warn("Error al registrar evolucion: " + e.getMessage());
				}
			}
			if (generacionListener != null) {
				generacionListener.accept(i + 1, mejorIndiv);
			}
			GpLogger.info("Mejor individuo:");
			mejorIndiv.writeIndividuo();
			GpLogger.info("Fitness: " + mejorIndiv.getFitness());
			if (mejorIndiv.getExpresion() != null) {
				String orig   = mejorIndiv.getExpresion().toString();
				String simpl  = SimplificadorExpresion.toStringSimplificado(mejorIndiv.getExpresion());
				if (!simpl.equals(orig)) GpLogger.debug("Simplificado: " + simpl);
			}
			if (mejorIndiv.getFitness() >= dominio.fitnessBuscado() - 0.05) {
				boolean esConstanteTrivial = dominio instanceof DominioClasificacion
						&& UtilExpresion.isConstant(mejorIndiv.getExpresion());
				if (!esConstanteTrivial) {
					generacionFinal = i + 1;
					objetivoAlcanzado = true;
					GpLogger.info("Objetivo de fitness alcanzado.");
					break;
				}
			}
			if (mejorIndiv.getFitness() > mejorFitnessAnterior) {
				mejorFitnessAnterior = mejorIndiv.getFitness();
				generacionesSinMejora = 0;
			} else {
				generacionesSinMejora++;
				if (generacionesSinMejoraParaParar > 0 && generacionesSinMejora >= generacionesSinMejoraParaParar) {
					generacionFinal = i + 1;
					GpLogger.info("Parada por convergencia (sin mejora en " + generacionesSinMejoraParaParar + " generaciones).");
					break;
				}
			}
			crearNuevaPoblacion();
		}
		if (evolucionLogger != null) {
			try {
				evolucionLogger.cerrar();
			} catch (Exception e) {
				GpLogger.warn("Error al cerrar logger: " + e.getMessage());
			}
		}
		ultimoResultado = new ResultadoEjecucion(mejorIndiv, generacionFinal, objetivoAlcanzado);
	}

	/**
	 * Devuelve el resultado de la última ejecución (mejor individuo, generación final, si se alcanzó objetivo).
	 * Null si aún no se ha llamado a {@link #ejecutar(IDominio)}.
	 */
	public ResultadoEjecucion getUltimoResultado() {
		return ultimoResultado;
	}

	@Override
	public void setLogger(EvolucionLogger logger) {
		this.evolucionLogger = logger;
	}

	@Override
	public void setGeneracionListener(BiConsumer<Integer, IIndividuo> listener) {
		this.generacionListener = listener;
	}

	/**
	 * Limite maximo de nodos por individuo (cruce/mutacion). 0 = sin limite.
	 *
	 * @param max 0 para desactivar, &gt; 0 para rechazar hijos que lo superen
	 */
	public void setMaxNodosIndividuo(int max) {
		this.maxNodosIndividuo = Math.max(0, max);
		this.operadorCruce.setMaxNodos(this.maxNodosIndividuo);
	}

	/**
	 * Parar si el mejor fitness no mejora en tantas generaciones. 0 = desactivado.
	 *
	 * @param generaciones numero de generaciones sin mejora para parar (0 = no parar por convergencia)
	 */
	public void setGeneracionesSinMejoraParaParar(int generaciones) {
		this.generacionesSinMejoraParaParar = Math.max(0, generaciones);
	}

	/** Establece el tipo de selección de padres (TORNEO por defecto). */
	public void setTipoSeleccion(TipoSeleccion tipo) {
		if (tipo == null) tipo = TipoSeleccion.TORNEO;
		this.selector = (tipo == TipoSeleccion.RANKING)
				? new SelectorRanking(random)
				: new SelectorTorneo(valorTorneo, random);
	}

	/**
	 * Establece las probabilidades de cada tipo de mutación (se normalizan para elegir una por individuo).
	 * Por defecto solo subárbol = 1.0.
	 *
	 * @param subarbol probabilidad mutación por subárbol
	 * @param punto probabilidad mutación por punto
	 * @param contraccion probabilidad mutación por contracción
	 */
	public void setProbabilidadesMutacion(double subarbol, double punto, double contraccion) {
		this.probMutacionSubarbol = Math.max(0, subarbol);
		this.probMutacionPunto = Math.max(0, punto);
		this.probMutacionContraccion = Math.max(0, contraccion);
	}

	/**
	 * Número máximo de reintentos ante cruce nulo antes de copiar los progenitores.
	 * Por defecto 50.
	 */
	public void setMaxReintentoCruce(int max) {
		this.maxReintentoCruce = Math.max(1, max);
	}

	/**
	 * Profundidad máxima del subárbol generado en mutación por subárbol.
	 * Por defecto 2.
	 */
	public void setProfundidadSubarbolMutacion(int prof) {
		this.profundidadSubarbolMutacion = Math.max(1, prof);
	}

	/**
	 * Amplitud de perturbación para mutación punto en constantes:
	 * el delta se muestrea uniformemente en [-perturbConstante, perturbConstante].
	 * Por defecto 0.5.
	 */
	public void setPerturbConstante(double perturb) {
		this.perturbConstante = Math.max(0, perturb);
	}
}
