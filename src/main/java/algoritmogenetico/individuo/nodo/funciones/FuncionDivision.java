package algoritmogenetico.individuo.nodo.funciones;

import algoritmogenetico.individuo.nodo.INodo;

/**
 * Funcion aritmetica division ("/") con proteccion frente a division por cero:
 * si |denominador| &lt; epsilon devuelve 1.0 (division protegida, estilo Koza).
 */
public class FuncionDivision extends FuncionAritmetica {

	/**
	 * Umbral de protección frente a denominadores cercanos a cero.
	 * Puede ajustarse globalmente antes de crear la población.
	 */
	public static double epsilon = 1e-6;

	/**
	 * Crea una funcion division con el simbolo y numero de argumentos dados.
	 *
	 * @param simbolo el simbolo (p. ej. "/")
	 * @param numArgu numero de argumentos (2)
	 */
	public FuncionDivision(String simbolo, int numArgu) {
		super(simbolo, numArgu);
	}

	@Override
	public double calcular() {
		double den = descendientes.get(1).calcular();
		if (Math.abs(den) < epsilon) {
			return 1.0;
		}
		return descendientes.get(0).calcular() / den;
	}

	@Override
	public INodo copy() {
		FuncionDivision funcion = new FuncionDivision(simbolo, numArgu);
		for (INodo nodo : descendientes) {
			funcion.incluirDescendiente(nodo);
		}
		return funcion;
	}
}
