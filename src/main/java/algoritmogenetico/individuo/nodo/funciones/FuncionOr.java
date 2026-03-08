package algoritmogenetico.individuo.nodo.funciones;

import algoritmogenetico.individuo.nodo.INodo;

/**
 * Funcion binaria OR logica: devuelve 1.0 si al menos un argumento es &gt; 0.5.
 */
public class FuncionOr extends Funcion {

	public FuncionOr(String simbolo, int numArgu) {
		super(simbolo, numArgu);
	}

	@Override
	public double calcular() {
		return (descendientes.get(0).calcular() > 0.5 || descendientes.get(1).calcular() > 0.5) ? 1.0 : 0.0;
	}

	@Override
	public INodo copy() {
		FuncionOr f = new FuncionOr(simbolo, numArgu);
		for (INodo nodo : descendientes)
			f.incluirDescendiente(nodo);
		return f;
	}
}
