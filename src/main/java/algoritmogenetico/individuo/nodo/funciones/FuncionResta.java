package algoritmogenetico.individuo.nodo.funciones;

import algoritmogenetico.individuo.nodo.INodo;

/**
 * La clase FuncionResta es un tipo de FuncionAritmetica que representa la
 * funcion matematica resta ("-").
 */
public class FuncionResta extends FuncionAritmetica {

	/**
	 * Permite instanciar un objeto de tipo FuncionResta.
	 *
	 * @param simbolo el simbolo que representa a la FuncionResta
	 * @param numArgu el numero de argumentos (descendientes) que puede tener la FuncionResta
	 */
	public FuncionResta(String simbolo, int numArgu) {
		super(simbolo, numArgu);
	}

	/**
	 * Realiza la resta matematica del valor de sus descendientes y devuelve el
	 * resultado.
	 *
	 * @return el resultado de la resta de sus descendientes
	 */
	@Override
	public double calcular() {
		return descendientes.get(0).calcular() - descendientes.get(1).calcular();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.individuo.nodo.Nodo#copy()
	 */
	@Override
	public INodo copy() {
		FuncionResta funcion = new FuncionResta(simbolo, numArgu);
		for (INodo nodo : descendientes) {
			funcion.incluirDescendiente(nodo);
		}
		return funcion;
	}
}
