package algoritmogenetico.individuo.nodo.funciones;

import algoritmogenetico.individuo.nodo.INodo;

/**
 * Funcion unaria cuadrado ("sqr"): x^2 con proteccion frente a desbordamiento.
 * Si |x| supera la raiz de Double.MAX_VALUE el resultado seria Infinity, por lo
 * que se devuelve directamente Double.MAX_VALUE con el signo de x^2 (positivo).
 */
public class FuncionCuadrado extends FuncionAritmetica {

	private static final double MAX_ARG = Math.sqrt(Double.MAX_VALUE); // ≈ 1.34e154

	public FuncionCuadrado(String simbolo, int numArgu) {
		super(simbolo, numArgu);
	}

	@Override
	public double calcular() {
		double x = descendientes.get(0).calcular();
		if (!Double.isFinite(x) || Math.abs(x) > MAX_ARG) return Double.MAX_VALUE;
		return x * x;
	}

	@Override
	public INodo copy() {
		FuncionCuadrado f = new FuncionCuadrado(simbolo, numArgu);
		for (INodo n : descendientes) f.incluirDescendiente(n);
		return f;
	}
}
