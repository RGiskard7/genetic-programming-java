package algoritmogenetico.individuo.nodo.funciones;

import algoritmogenetico.individuo.nodo.INodo;

/**
 * Funcion unaria valor absoluto ("abs"): devuelve |argumento|.
 */
public class FuncionValorAbsoluto extends FuncionAritmetica {

	public FuncionValorAbsoluto(String simbolo, int numArgu) {
		super(simbolo, numArgu);
	}

	@Override
	public double calcular() {
		return Math.abs(descendientes.get(0).calcular());
	}

	@Override
	public INodo copy() {
		FuncionValorAbsoluto funcion = new FuncionValorAbsoluto(simbolo, numArgu);
		for (INodo nodo : descendientes)
			funcion.incluirDescendiente(nodo);
		return funcion;
	}
}
