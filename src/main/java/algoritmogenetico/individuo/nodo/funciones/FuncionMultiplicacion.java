package algoritmogenetico.individuo.nodo.funciones;

import algoritmogenetico.individuo.nodo.INodo;

/**
 * La clase FuncionMultiplicacion es un tipo de FuncionAritmetica que representa
 * la funcion matematica multiplicacion ("*").
 */
public class FuncionMultiplicacion extends FuncionAritmetica {

	/**
	 * Permite instanciar un nuevo objeto de tipo FuncionMultiplicacion
	 *
	 * @param simbolo el simbolo que representa a la FuncionMultiplicacion
	 * @param numArgu el numero de argumentos (descendientes) que tendra la FuncionMultiplicacion
	 */
	public FuncionMultiplicacion(String simbolo, int numArgu) {
		super(simbolo, numArgu);
	}

	/**
	 * Realiza la multiplicacion matematica del valor de sus descendientes y
	 * devuelve el resultado.
	 *
	 * @return el resultado de la multiplicacion de sus descendientes
	 */
	@Override
	public double calcular() {
		return descendientes.get(0).calcular() * descendientes.get(1).calcular();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.individuo.nodo.Nodo#copy()
	 */
	@Override
	public INodo copy() {
		FuncionMultiplicacion funcion = new FuncionMultiplicacion(simbolo, numArgu);
		for (INodo nodo : descendientes) {
			funcion.incluirDescendiente(nodo);
		}
		return funcion;
	}
}
