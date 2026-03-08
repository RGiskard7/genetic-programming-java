package algoritmogenetico.individuo.nodo.funciones;

import algoritmogenetico.individuo.nodo.INodo;

/**
 * Funcion unaria cuadrado ("sqr"): x^2.
 */
public class FuncionCuadrado extends FuncionAritmetica {

	public FuncionCuadrado(String simbolo, int numArgu) {
		super(simbolo, numArgu);
	}

	@Override
	public double calcular() {
		double x = descendientes.get(0).calcular();
		return x * x;
	}

	@Override
	public INodo copy() {
		FuncionCuadrado f = new FuncionCuadrado(simbolo, numArgu);
		for (INodo n : descendientes) f.incluirDescendiente(n);
		return f;
	}
}
