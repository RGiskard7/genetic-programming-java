package algoritmogenetico.individuo;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import algoritmogenetico.individuo.nodo.INodo;
import algoritmogenetico.individuo.nodo.funciones.Funcion;
import algoritmogenetico.individuo.nodo.terminales.Terminal;

/**
 * Individuo como árbol de expresiones (nodos funciones y terminales). Implementa
 * {@link IIndividuo}; permite creación aleatoria, etiquetado de nodos,
 * reemplazo de subárboles (mutación) y consulta del padre por nodo.
 */
public class Individuo implements IIndividuo {
	private INodo expresion;
	private Map<Integer, INodo> nodosEtiquetados;
	private double fitness;

	/**
	 * Permite instanciar un nuevo objeto de tipo Individuo.
	 */
	public Individuo() {
		nodosEtiquetados = new TreeMap<>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.individuo.IIndividuo#getExpresion()
	 */
	@Override
	public INodo getExpresion() {
		return expresion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.individuo.IIndividuo#setExpresion(algoritmogenetico.individuo.nodo.INodo)
	 */
	@Override
	public void setExpresion(INodo expresion) {
		this.expresion = expresion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.individuo.IIndividuo#getFitness()
	 */
	@Override
	public double getFitness() {
		return fitness;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.individuo.IIndividuo#setFitness(double)
	 */
	@Override
	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.individuo.IIndividuo#crearIndividuoAleatorio(int, java.util.List, java.util.List)
	 */
	@Override
	public void crearIndividuoAleatorio(int profundidad, List<Terminal> terminales, List<Funcion> funciones) {
		crearIndividuoAleatorio(profundidad, terminales, funciones, new Random());
	}

	/**
	 * Crea un individuo aleatorio usando el generador de numeros aleatorios indicado.
	 * Metodo "full": todos los caminos desde la raiz tienen la misma profundidad.
	 *
	 * @param profundidad la profundidad maxima del arbol
	 * @param terminales el conjunto de terminales
	 * @param funciones el conjunto de funciones
	 * @param rng generador aleatorio (para reproducibilidad)
	 */
	public void crearIndividuoAleatorio(int profundidad, List<Terminal> terminales, List<Funcion> funciones, Random rng) {
		expresion = crearIndividuoAleatorioRec(1, profundidad, terminales, funciones, rng);
	}

	/**
	 * Crea un individuo aleatorio usando el metodo "grow": en cada nivel
	 * se decide aleatoriamente si colocar un terminal o una funcion, hasta
	 * llegar a la profundidad maxima. Produce arboles de formas variadas.
	 *
	 * @param profundidadMax profundidad maxima del arbol
	 * @param terminales el conjunto de terminales
	 * @param funciones el conjunto de funciones
	 * @param rng generador aleatorio
	 */
	public void crearIndividuoAleatorioGrow(int profundidadMax, List<Terminal> terminales, List<Funcion> funciones,
			Random rng) {
		expresion = crearIndividuoGrowRec(1, profundidadMax, terminales, funciones, rng);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.individuo.IIndividuo#calcularExpresion()
	 */
	@Override
	public double calcularExpresion() {
		if (getNumeroNodos() > 0) {
			return expresion.calcular();
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.individuo.IIndividuo#getNumeroNodos()
	 */
	@Override
	public int getNumeroNodos() {
		if (expresion != null) {
			return getNumeroNodosRec(expresion) + 1;
		}
		return 0;
	}

	@Override
	public int getProfundidad() {
		if (expresion == null) {
			return 0;
		}
		return getProfundidadRec(expresion);
	}

	private int getProfundidadRec(INodo nodo) {
		if (nodo.getDescendientes().isEmpty()) {
			return 1;
		}
		int maxHijo = 0;
		for (INodo n : nodo.getDescendientes()) {
			int d = getProfundidadRec(n);
			if (d > maxHijo) {
				maxHijo = d;
			}
		}
		return 1 + maxHijo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.individuo.IIndividuo#writeIndividuo()
	 */
	@Override
	public void writeIndividuo() {
		System.out.println("Expresion: " + expresion);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.individuo.IIndividuo#etiquetaNodos()
	 */
	public void etiquetaNodos() {
		nodosEtiquetados.clear();
		etiquetaNodosRec(1, expresion);
	}

	/**
	 * Devuelve un Map con todos los nodos de la expresion etiquetados, en caso de
	 * que se hayan etiquetado llamando al metodo etiquetaNodos().
	 *
	 * @return los nodos etiquetados
	 */
	public Map<Integer, INodo> getNodosEtiquetados() {
		return nodosEtiquetados;
	}

	/**
	 * Devuelve el nodo padre del nodo especificado.
	 *
	 * @param nodo el nodo del que se quiere conocer el padre
	 * @return el nodo padre, o null si es la raiz del individuo
	 */
	public INodo getPadre(INodo nodo) {
		for (Map.Entry<Integer, INodo> entry : nodosEtiquetados.entrySet()) {
			INodo n = entry.getValue();
			if (n.getDescendientes().contains(nodo)) {
				return n;
			}
		}
		return null;
	}

	/**
	 * Crea un subarbol aleatorio de profundidad maxima dada, reutilizando la
	 * logica de creacion de individuos. Util para el operador de mutacion.
	 *
	 * @param profundidadMax profundidad maxima del subarbol
	 * @param terminales conjunto de terminales
	 * @param funciones conjunto de funciones
	 * @param rng generador aleatorio
	 * @return la raiz del nuevo subarbol
	 */
	public INodo crearSubarbolAleatorio(int profundidadMax, List<Terminal> terminales, List<Funcion> funciones,
			Random rng) {
		return crearIndividuoAleatorioRec(1, profundidadMax, terminales, funciones, rng);
	}

	/**
	 * Reemplaza el nodo con la etiqueta indicada por el nuevo subarbol. Debe
	 * haberse llamado antes a etiquetaNodos(). Si la etiqueta corresponde a la
	 * raiz, se sustituye toda la expresion.
	 *
	 * @param etiqueta etiqueta del nodo a reemplazar (1 = raiz)
	 * @param nuevoSubarbol nueva raiz del subarbol que sustituye al nodo
	 */
	public void reemplazarNodo(int etiqueta, INodo nuevoSubarbol) {
		INodo nodo = nodosEtiquetados.get(etiqueta);
		if (nodo == null) {
			return;
		}
		INodo padre = getPadre(nodo);
		if (padre == null) {
			setExpresion(nuevoSubarbol);
		} else {
			List<INodo> hijos = padre.getDescendientes();
			int idx = hijos.indexOf(nodo);
			if (idx >= 0) {
				hijos.set(idx, nuevoSubarbol);
			}
		}
	}

	// Funciones recursivas
	/**
	 * Metodo "full": crea un arbol donde todos los caminos tienen la misma
	 * profundidad. Usa la aridad real de cada funcion (soporta funciones unarias).
	 *
	 * @param profundidadActual profundidad actual en la recursion
	 * @param profundidadTotal profundidad maxima
	 * @param terminales conjunto de terminales
	 * @param funciones conjunto de funciones
	 * @param rng generador aleatorio
	 * @return nodo raiz del subarbol generado
	 */
	private INodo crearIndividuoAleatorioRec(int profundidadActual, int profundidadTotal, List<Terminal> terminales,
			List<Funcion> funciones, Random rng) {
		if (profundidadActual == profundidadTotal) {
			return terminales.get(rng.nextInt(terminales.size())).copy();
		}
		Funcion f = funciones.get(rng.nextInt(funciones.size()));
		INodo nodo = f.copy();
		for (int k = 0; k < f.getNumArgu(); k++) {
			nodo.incluirDescendiente(
					crearIndividuoAleatorioRec(profundidadActual + 1, profundidadTotal, terminales, funciones, rng));
		}
		return nodo;
	}

	/**
	 * Metodo "grow": en cada nivel decide aleatoriamente si colocar un terminal
	 * o una funcion. Produce arboles de distintos tamanios y formas.
	 *
	 * @param profundidadActual profundidad actual
	 * @param profundidadMax profundidad maxima permitida
	 * @param terminales conjunto de terminales
	 * @param funciones conjunto de funciones
	 * @param rng generador aleatorio
	 * @return nodo raiz del subarbol generado
	 */
	private INodo crearIndividuoGrowRec(int profundidadActual, int profundidadMax, List<Terminal> terminales,
			List<Funcion> funciones, Random rng) {
		if (profundidadActual == profundidadMax || rng.nextBoolean()) {
			return terminales.get(rng.nextInt(terminales.size())).copy();
		}
		Funcion f = funciones.get(rng.nextInt(funciones.size()));
		INodo nodo = f.copy();
		for (int k = 0; k < f.getNumArgu(); k++) {
			nodo.incluirDescendiente(
					crearIndividuoGrowRec(profundidadActual + 1, profundidadMax, terminales, funciones, rng));
		}
		return nodo;
	}

	/**
	 * Devuelve el numero de nodos descendientes de un nodo raiz que obtiene de
	 * forma recursiva.
	 *
	 * @param nodo el nodo del que se quiere conocer el numero de nodos descendientes
	 * @return el numero de nodos del nodo introducido
	 */
	private int getNumeroNodosRec(INodo nodo) {
		int numDes = nodo.getDescendientes().size();
		for (INodo n : nodo.getDescendientes()) {
			numDes += getNumeroNodosRec(n);
		}
		return numDes;
	}

	/**
	 * Etiqueta numericamente los nodos de la expresion del individuo usando
	 * recursividad.
	 *
	 * @param etiqueta la etiqueta numerica que se va a asociar con el nodo
	 * @param nodo el nodo que se quiere etiquetar
	 * @return la etiqueta actual
	 */
	private int etiquetaNodosRec(int etiqueta, INodo nodo) {
		nodosEtiquetados.put(etiqueta, nodo);
		for (INodo n : nodo.getDescendientes()) {
			etiqueta = etiquetaNodosRec(++etiqueta, n);
		}
		return etiqueta;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((expresion == null) ? 0 : expresion.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Individuo other = (Individuo) obj;
		if (expresion == null) {
			if (other.expresion != null) {
				return false;
			}
		} else if (!expresion.equals(other.expresion)) {
			return false;
		}
		return true;
	}
}
