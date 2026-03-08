package algoritmogenetico.individuo.nodo.funciones;

import algoritmogenetico.individuo.nodo.INodo;

/**
 * Funcion unaria seno ("sin"): calcula el seno del argumento en radianes.
 */
public class FuncionSeno extends FuncionAritmetica {

	public FuncionSeno(String simbolo, int numArgu) {
		super(simbolo, numArgu);
	}

	@Override
	public double calcular() {
		return Math.sin(descendientes.get(0).calcular());
	}

	@Override
	public INodo copy() {
		FuncionSeno funcion = new FuncionSeno(simbolo, numArgu);
		for (INodo nodo : descendientes)
			funcion.incluirDescendiente(nodo);
		return funcion;
	}
}
