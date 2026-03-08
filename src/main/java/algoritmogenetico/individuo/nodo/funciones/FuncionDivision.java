package algoritmogenetico.individuo.nodo.funciones;

import algoritmogenetico.individuo.nodo.INodo;

/**
 * Funcion aritmetica division ("/") con proteccion frente a division por cero:
 * si el denominador es cero o muy proximo a cero, devuelve 1.0.
 */
public class FuncionDivision extends FuncionAritmetica {

	private static final double UMBRAL_DIVISION_CERO = 1e-10;

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
		if (Math.abs(den) < UMBRAL_DIVISION_CERO) {
			return 1.0;
		}
		return descendientes.get(0).calcular() / den;
	}

	@Override
	public INodo copy() {
		FuncionDivision funcion = new FuncionDivision(simbolo, numArgu);
		for (INodo nodo : descendientes) {
			funcion.incluirDescendiente(nodo.copy());
		}
		return funcion;
	}
}
