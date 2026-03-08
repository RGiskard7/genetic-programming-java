package algoritmogenetico.individuo.nodo.funciones;

import algoritmogenetico.individuo.nodo.INodo;

/**
 * Funcion binaria XOR logica: devuelve 1.0 si exactamente uno de los dos argumentos es &gt; 0.5.
 */
public class FuncionXor extends Funcion {

	public FuncionXor(String simbolo, int numArgu) {
		super(simbolo, numArgu);
	}

	@Override
	public double calcular() {
		boolean a = descendientes.get(0).calcular() > 0.5;
		boolean b = descendientes.get(1).calcular() > 0.5;
		return (a ^ b) ? 1.0 : 0.0;
	}

	@Override
	public INodo copy() {
		FuncionXor f = new FuncionXor(simbolo, numArgu);
		for (INodo nodo : descendientes)
			f.incluirDescendiente(nodo);
		return f;
	}
}
