package algoritmogenetico.individuo.nodo;

import java.util.List;

/**
 * La interfaz INodo reune todo lo necesario que debe tener un objeto para actuar como
 * un nodo dentro de un arbol.
 */
public interface INodo {

	/**
	 * Devuelve el simbolo que representa el nodo.
	 *
	 * @return el simbolo del nodo
	 */
	public String getSimbolo();

	/**
	 * Devuelve los descendientes que tiene el nodo.
	 *
	 * @return los descendientes
	 */
	public List<INodo> getDescendientes();

	/**
	 * Permite annadir un nuevo nodo a un nodo existente
	 *
	 * @param nodo
	 *            el nuevo nodo
	 */
	public void incluirDescendiente(INodo nodo);

	/**
	 * Devuelve el valor asociado al nodo.
	 *
	 * @return un valor tipo double
	 */
	public double calcular();

	/**
	 * Permite hacer una copia del nodo.
	 *
	 * @return el resultado de la copia
	 */
	public INodo copy();
}
