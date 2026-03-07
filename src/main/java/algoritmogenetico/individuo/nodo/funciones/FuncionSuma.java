package algoritmogenetico.individuo.nodo.funciones;

import algoritmogenetico.individuo.nodo.INodo;

/**
 * La clase FuncionSuma es un tipo de FuncionAritmetica que representa la
 * funcion matematica suma ("+").
 */
public class FuncionSuma extends FuncionAritmetica {

	/**
	 * Permite instanciar un objeto de tipo FuncionSuma
	 *
	 * @param simbolo el simbolo que representa a la FuncionSuma
	 * @param numArgu el numero de argumentos (descendientes) que puede tener la FuncionSuma
	 */
	public FuncionSuma(String simbolo, int numArgu) {
		super(simbolo, numArgu);
	}

	/**
	 * Realiza la suma matematica del valor de sus descendientes y devuelve el
	 * resultado.
	 *
	 * @return el resultado de la suma de sus descendientes
	 */
	@Override
	public double calcular() {
		return descendientes.get(0).calcular() + descendientes.get(1).calcular();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.individuo.nodo.Nodo#copy()
	 */
	@Override
	public INodo copy() {
		FuncionSuma funcion = new FuncionSuma(simbolo, numArgu);
		for (INodo nodo : descendientes) {
			funcion.incluirDescendiente(nodo);
		}
		return funcion;
	}
}
