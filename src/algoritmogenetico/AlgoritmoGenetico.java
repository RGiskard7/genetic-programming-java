package algoritmogenetico;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import algoritmogenetico.dominio.IDominio;
import algoritmogenetico.individuo.IIndividuo;
import algoritmogenetico.individuo.Individuo;
import algoritmogenetico.individuo.nodo.INodo;
import algoritmogenetico.individuo.nodo.funciones.Funcion;
import algoritmogenetico.individuo.nodo.terminales.Terminal;
import excepciones.ArgsDistintosFuncionesException;
import excepciones.CruceNuloException;

/**
 * La clase AlgoritmoGenetico implementa todo lo especificado por la interfaz
 * IAlgoritmo.
 */
public class AlgoritmoGenetico implements IAlgoritmo {
	private static final int MAX_REINTENTOS_CRUCE = 50;
	private static final int PROFUNDIDAD_SUBARBOL_MUTACION = 2;

	private final int tamanioPoblacion;
	private final int profundidad;
	private final int probabilidadCruce;
	private final int valorTorneo;
	private final int maxGeneraciones;
	private final double probabilidadMutacion;
	private final Random random;

	private List<IIndividuo> poblacion;
	private List<Terminal> terminales;
	private List<Funcion> funciones;

	private static final Comparator<IIndividuo> COMPARADOR_FITNESS =
			Comparator.comparingDouble(IIndividuo::getFitness);

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
		this.tamanioPoblacion = tamanioPoblacion;
		this.profundidad = profundidadArbol;
		this.probabilidadCruce = probabilidadCruce;
		this.valorTorneo = valorTorneo;
		this.maxGeneraciones = maxGeneraciones;
		this.probabilidadMutacion = probabilidadMutacion;
		this.random = semilla != null ? new Random(semilla) : new Random();
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
	@Override
	public void crearPoblacion() {
		poblacion = new ArrayList<>();
		for (int i = 0; i < tamanioPoblacion; i++) {
			Individuo individuo = new Individuo();
			individuo.crearIndividuoAleatorio(profundidad, terminales, funciones, random);
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
		int ptoCruce1 = random.nextInt(prog1.getNumeroNodos()) + 1;
		int ptoCruce2 = random.nextInt(prog2.getNumeroNodos()) + 1;

		if (ptoCruce1 == 1 && ptoCruce2 == 1) {
			throw new CruceNuloException();
		}

		int index;
		INodo raiz, nodoSwap, nodoAux;
		Individuo descendiente1 = new Individuo();
		Individuo descendiente2 = new Individuo();

		descendiente1.setExpresion(prog1.getExpresion().copy());
		descendiente2.setExpresion(prog2.getExpresion().copy());

		descendiente1.etiquetaNodos();
		descendiente2.etiquetaNodos();

		nodoSwap = descendiente1.getNodosEtiquetados().get(ptoCruce1);

		raiz = descendiente1.getPadre(nodoSwap);
		if (raiz != null) {
			index = raiz.getDescendientes().indexOf(nodoSwap);
			if (index == 0) {
				nodoAux = raiz.getDescendientes().get(1);
				raiz.getDescendientes().clear();
				raiz.incluirDescendiente(descendiente2.getNodosEtiquetados().get(ptoCruce2));
				raiz.incluirDescendiente(nodoAux);
			} else {
				nodoAux = raiz.getDescendientes().get(0);
				raiz.getDescendientes().clear();
				raiz.incluirDescendiente(nodoAux);
				raiz.incluirDescendiente(descendiente2.getNodosEtiquetados().get(ptoCruce2));
			}
		} else {
			descendiente1.setExpresion(descendiente2.getNodosEtiquetados().get(ptoCruce2));
		}

		raiz = descendiente2.getPadre(descendiente2.getNodosEtiquetados().get(ptoCruce2));
		if (raiz != null) {
			index = raiz.getDescendientes().indexOf(descendiente2.getNodosEtiquetados().get(ptoCruce2));
			if (index == 0) {
				nodoAux = raiz.getDescendientes().get(1);
				raiz.getDescendientes().clear();
				raiz.incluirDescendiente(nodoSwap);
				raiz.incluirDescendiente(nodoAux);
			} else {
				nodoAux = raiz.getDescendientes().get(0);
				raiz.getDescendientes().clear();
				raiz.incluirDescendiente(nodoAux);
				raiz.incluirDescendiente(nodoSwap);
			}
		} else {
			descendiente2.setExpresion(nodoSwap);
		}

		descendiente1.etiquetaNodos();
		descendiente2.etiquetaNodos();

		List<IIndividuo> descendientes = new ArrayList<>();
		descendientes.add(descendiente1);
		descendientes.add(descendiente2);
		return descendientes;
	}

	/**
	 * Devuelve los dos individuos con el fitness mas alto dentro de una lista
	 * de individuos candidatos.
	 *
	 * @param candidatos los candidatos 
	 * @return la lista con los ganadores del torneo
	 */
	private List<IIndividuo> torneo(List<IIndividuo> candidatos) {
		List<IIndividuo> ganadores = new ArrayList<>();
		candidatos.sort(COMPARADOR_FITNESS);
		ganadores.add(candidatos.get(candidatos.size() - 1));
		ganadores.add(candidatos.get(candidatos.size() - 2));
		return ganadores;
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
		return Collections.max(poblacion, COMPARADOR_FITNESS);
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

		while (nuevaPoblacion.size() != tamanioPoblacion) {
			List<IIndividuo> candidatos = new ArrayList<>();
			List<Integer> indices = new ArrayList<>();
			int j = 0;

			while (j < valorTorneo) {
				int n = random.nextInt(tamanioPoblacion);
				if (!indices.contains(n)) {
					indices.add(n);
					candidatos.add(poblacion.get(n));
					j++;
				}
			}

			List<IIndividuo> ganadores = torneo(candidatos);
			boolean cruzados = false;
			for (int reintento = 0; reintento < MAX_REINTENTOS_CRUCE && !cruzados; reintento++) {
				try {
					List<IIndividuo> descendientes = cruce(ganadores.get(0), ganadores.get(1));
					for (IIndividuo d : descendientes) {
						IIndividuo aAnadir = (random.nextDouble() < probabilidadMutacion) ? mutar(d) : d;
						nuevaPoblacion.add(aAnadir);
					}
					cruzados = true;
				} catch (CruceNuloException e) {
					if (reintento == MAX_REINTENTOS_CRUCE - 1) {
						nuevaPoblacion.add(copiarIndividuo(ganadores.get(0)));
						nuevaPoblacion.add(copiarIndividuo(ganadores.get(1)));
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
		int etiquetaElegida = 1 + random.nextInt(numNodos);
		Individuo aux = new Individuo();
		INodo nuevoSubarbol = aux.crearSubarbolAleatorio(PROFUNDIDAD_SUBARBOL_MUTACION, terminales, funciones, random);
		copia.reemplazarNodo(etiquetaElegida, nuevoSubarbol);
		return copia;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.IAlgoritmo#ejecutar(algoritmogenetico.dominio.IDominio)
	 */
	@Override
	public void ejecutar(IDominio dominio) {
		IIndividuo mejorIndiv;

		crearPoblacion();

		for (int i = 0; i < maxGeneraciones; i++) {
			System.out.println(">>GENERACION: " + (i + 1));
			calcularFitnessPoblacion(dominio);
			mejorIndiv = bestFitness();
			System.out.println("Mejor individuo:");
			mejorIndiv.writeIndividuo();
			System.out.println("Fitness: " + mejorIndiv.getFitness());
			if (dominio.fitnessBuscado() == mejorIndiv.getFitness())
				break;
			crearNuevaPoblacion();
		}
	}
}
