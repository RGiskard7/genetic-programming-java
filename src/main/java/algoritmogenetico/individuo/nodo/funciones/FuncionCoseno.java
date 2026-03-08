package algoritmogenetico.individuo.nodo.funciones;

import algoritmogenetico.individuo.nodo.INodo;

/**
 * Funcion unaria coseno ("cos"): calcula el coseno del argumento en radianes.
 */
public class FuncionCoseno extends FuncionAritmetica {

	public FuncionCoseno(String simbolo, int numArgu) {
		super(simbolo, numArgu);
	}

	@Override
	public double calcular() {
		return Math.cos(descendientes.get(0).calcular());
	}

	@Override
	public INodo copy() {
		FuncionCoseno funcion = new FuncionCoseno(simbolo, numArgu);
		for (INodo nodo : descendientes)
			funcion.incluirDescendiente(nodo);
		return funcion;
	}
}
