package algoritmogenetico.individuo.nodo.funciones;

import algoritmogenetico.individuo.nodo.INodo;

/**
 * Funcion unaria raiz cuadrada protegida ("sqrt"): sqrt(|x|) para evitar argumentos negativos.
 */
public class FuncionSqrt extends FuncionAritmetica {

	public FuncionSqrt(String simbolo, int numArgu) {
		super(simbolo, numArgu);
	}

	@Override
	public double calcular() {
		return Math.sqrt(Math.abs(descendientes.get(0).calcular()));
	}

	@Override
	public INodo copy() {
		FuncionSqrt f = new FuncionSqrt(simbolo, numArgu);
		for (INodo n : descendientes) f.incluirDescendiente(n);
		return f;
	}
}
