package algoritmogenetico.individuo.nodo.funciones;

import algoritmogenetico.individuo.nodo.INodo;

/**
 * Funcion unaria negacion ("neg"): devuelve el opuesto del argumento.
 */
public class FuncionNegacion extends FuncionAritmetica {

	public FuncionNegacion(String simbolo, int numArgu) {
		super(simbolo, numArgu);
	}

	@Override
	public double calcular() {
		return -descendientes.get(0).calcular();
	}

	@Override
	public INodo copy() {
		FuncionNegacion funcion = new FuncionNegacion(simbolo, numArgu);
		for (INodo nodo : descendientes)
			funcion.incluirDescendiente(nodo);
		return funcion;
	}
}
