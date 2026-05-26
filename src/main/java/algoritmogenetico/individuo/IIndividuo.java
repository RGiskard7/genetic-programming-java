package algoritmogenetico.individuo;

import java.util.List;

import algoritmogenetico.individuo.nodo.INodo;
import algoritmogenetico.individuo.nodo.funciones.Funcion;
import algoritmogenetico.individuo.nodo.terminales.Terminal;

/**
 * La interfaz IIndividuo define todos los requisitos basicos que debe tener una
 * clase para ser considerado un individuo, y asi poder ser evaluada a la hora
 * de resolver un problema. A grandes ragos, los individuos se describen a
 * partir de un conjunto de Terminales y de Funciones, presentados en forma de
 * arbol.
 */
public interface IIndividuo {

	/**
	 * Devuelve un Nodo que representa la raíz del arbol, es decir, el Nodo inicial
	 * del individuo.
	 *
	 * @return el nodo inicial del individuo
	 */
	public INodo getExpresion();

	/**
	 * Permite establecer el Nodo inicial o raiz del individuo.
	 *
	 * @param expresion el nuevo Nodo inicial del individuo
	 */
	public void setExpresion(INodo expresion);

	/**
	 * Devuelve el fitness (aptitud o capacidad) que tiene el individuo para
	 * resolver un problema concreto.
	 *
	 * @return el fitness del individuo
	 */
	public double getFitness();

	/**
	 * Permite establecer el fitness (aptitud o capacidad) del individuo.
	 *
	 * @param fitness el fitness del individuo
	 */
	public void setFitness(double fitness);

	/**
	 * Permite crear individuo un aleatorio, con una profundidad de arbol
	 * determinada y un conjunto de funciones y terminales especificos.
	 *
	 * @param profundidad la profundidad de la expresion (arbol) del individuo
	 * @param terminales el conjunto de terminales que se usaran para crear al individuo
	 * @param funciones el conjunto de funciones que se usaran para crear al individuo
	 */
	public void crearIndividuoAleatorio(int profundidad, List<Terminal> terminales, List<Funcion> funciones);

	/**
	 * Calcula el valor de la expresion del individuo.
	 *
	 * @return el resultado del calculo de la expresion del individuo
	 */
	public double calcularExpresion();

	/**
	 * Devuelve el numero de nodos que tiene la expresion del individuo.
	 *
	 * @return el numero de nodos del individuo
	 */
	public int getNumeroNodos();

	/**
	 * Devuelve la profundidad del arbol de expresion (1 para un solo nodo, 1 + max(hijos) para raiz con hijos).
	 *
	 * @return la profundidad del arbol, o 0 si no hay expresion
	 */
	public int getProfundidad();

	/**
	 * Indica si la última evaluación del individuo produjo valores inestables
	 * (NaN, Infinity o magnitud superior al umbral del dominio).
	 * Inicialmente false; se actualiza en cada llamada a {@code calcularFitness}.
	 *
	 * @return true si se detectaron singularidades durante la evaluación
	 */
	public boolean tieneSingularidades();

	/**
	 * Establece si el individuo presentó singularidades en la última evaluación.
	 * Llamado por el dominio dentro de {@code calcularFitness}.
	 *
	 * @param valor true si hubo valores inestables
	 */
	public void setTieneSingularidades(boolean valor);

	/**
	 * Imprime por pantalla la expresion del individuo.
	 */
	public void writeIndividuo();

	/**
	 * Permite etiquetar numericamente los nodos actuales de la expresion del
	 * individuo.
	 */
	public void etiquetaNodos();
}
