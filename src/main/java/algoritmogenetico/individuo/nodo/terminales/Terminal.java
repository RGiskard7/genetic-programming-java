package algoritmogenetico.individuo.nodo.terminales;

import algoritmogenetico.individuo.nodo.INodo;
import algoritmogenetico.individuo.nodo.Nodo;

/**
 * La clase terminal representa un tipo de nodo concreto, el cual es un simbolo
 * final que no puede expandirse, es decir, no tiene descendientes.
 */
public abstract class Terminal extends Nodo {

	/**
	 * Constructor de la clase abstracta Terminal
	 *
	 * @param simbolo the simbolo
	 */
	public Terminal(String simbolo) {
		super(simbolo);
	}

	/**
	 * En el caso de los Terminales, esta funcion no permite annadir ningun nuevo
	 * nodo a sus descendientes.
	 *
	 * @param nodo objeto tipo INodo, pero no se annade
	 */
	@Override
	public void incluirDescendiente(INodo nodo) {
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.individuo.nodo.Nodo#toString()
	 */
	@Override
	public String toString() {
		return simbolo;
	}
}
