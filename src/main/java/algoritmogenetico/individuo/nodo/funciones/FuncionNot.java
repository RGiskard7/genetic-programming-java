package algoritmogenetico.individuo.nodo.funciones;

import algoritmogenetico.individuo.nodo.INodo;

/**
 * Funcion unaria NOT logica: devuelve 0.0 si el argumento es &gt; 0.5, 1.0 en otro caso.
 */
public class FuncionNot extends Funcion {

	public FuncionNot(String simbolo, int numArgu) {
		super(simbolo, numArgu);
	}

	@Override
	public double calcular() {
		return descendientes.get(0).calcular() > 0.5 ? 0.0 : 1.0;
	}

	@Override
	public INodo copy() {
		FuncionNot f = new FuncionNot(simbolo, numArgu);
		for (INodo nodo : descendientes)
			f.incluirDescendiente(nodo);
		return f;
	}
}
