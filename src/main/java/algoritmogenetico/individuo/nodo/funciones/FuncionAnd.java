package algoritmogenetico.individuo.nodo.funciones;

import algoritmogenetico.individuo.nodo.INodo;

/**
 * Funcion binaria AND logica: devuelve 1.0 si ambos argumentos son &gt; 0.5, 0.0 en otro caso.
 */
public class FuncionAnd extends Funcion {

	public FuncionAnd(String simbolo, int numArgu) {
		super(simbolo, numArgu);
	}

	@Override
	public double calcular() {
		return (descendientes.get(0).calcular() > 0.5 && descendientes.get(1).calcular() > 0.5) ? 1.0 : 0.0;
	}

	@Override
	public INodo copy() {
		FuncionAnd f = new FuncionAnd(simbolo, numArgu);
		for (INodo nodo : descendientes)
			f.incluirDescendiente(nodo);
		return f;
	}
}
