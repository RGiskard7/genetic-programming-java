package algoritmogenetico.individuo.nodo.funciones;

/**
 * La clase FuncionAritmetica es un tipo de funcion concreta que trabaja
 * unicamente con TerminalesAritmeticos.
 */
public abstract class FuncionAritmetica extends Funcion {

	/**
	 * Constructor de la clase abstracta FuncionAritmetica.
	 *
	 * @param simbolo el simbolo que representa a la Funcion
	 * @param numArgu el numero de argumentos (descendientes) que puede tener la funcion
	 */
	public FuncionAritmetica(String simbolo, int numArgu) {
		super(simbolo, numArgu);
	}
}
