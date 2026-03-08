package algoritmogenetico.individuo.nodo.funciones;

import algoritmogenetico.individuo.nodo.INodo;
import algoritmogenetico.individuo.nodo.Nodo;

/**
 * La clase Funcion representa un tipo de nodo concreto, el cual hace referencia
 * al concepto matematico de funcion. Puede tener descendientes, en este caso
 * denominados argumentos, que a su vez pueden ser otras Funciones o Terminales,
 * y sobre los que realiza una determinada accion, y de los que finalmente
 * obtiene un resultado.
 */
public abstract class Funcion extends Nodo {
	protected int numArgu;

	/**
	 * Constructor de la clase abstracta Funcion.
	 *
	 * @param simbolo el simbolo que representa a la funcion
	 * @param numArgu el numero de argumentos (descendientes) que puede tener la funcion
	 */
	public Funcion(String simbolo, int numArgu) {
		super(simbolo);
		this.numArgu = numArgu;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.individuo.nodo.Nodo#incluirDescendiente(algoritmogenetico.individuo.nodo.INodo)
	 */
	@Override
	public void incluirDescendiente(INodo nodo) {
		if (descendientes.size() < numArgu)
			super.incluirDescendiente(nodo);
	}

	/**
	 * Devuelve el numero de argumentos (aridad) de esta funcion.
	 *
	 * @return numero de argumentos
	 */
	public int getNumArgu() {
		return numArgu;
	}
}
