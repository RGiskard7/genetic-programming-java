package algoritmogenetico.individuo.nodo.funciones;

import algoritmogenetico.individuo.nodo.INodo;

/**
 * Funcion unaria exponencial ("exp"): e^x. Para evitar overflow se acota el argumento.
 */
public class FuncionExp extends FuncionAritmetica {

	private static final double MAX_EXP = 700;

	public FuncionExp(String simbolo, int numArgu) {
		super(simbolo, numArgu);
	}

	@Override
	public double calcular() {
		double x = descendientes.get(0).calcular();
		if (x > MAX_EXP) return Math.exp(MAX_EXP);
		if (x < -MAX_EXP) return 0.0;
		return Math.exp(x);
	}

	@Override
	public INodo copy() {
		FuncionExp f = new FuncionExp(simbolo, numArgu);
		for (INodo n : descendientes) f.incluirDescendiente(n);
		return f;
	}
}
