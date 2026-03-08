package algoritmogenetico.individuo.nodo.funciones;

import algoritmogenetico.individuo.nodo.INodo;

/**
 * Funcion unaria logaritmo protegido ("log"): log(1 + |x|). Evita log(0) o argumentos negativos.
 */
public class FuncionLog extends FuncionAritmetica {

	public FuncionLog(String simbolo, int numArgu) {
		super(simbolo, numArgu);
	}

	@Override
	public double calcular() {
		double x = descendientes.get(0).calcular();
		return Math.log(1.0 + Math.abs(x));
	}

	@Override
	public INodo copy() {
		FuncionLog f = new FuncionLog(simbolo, numArgu);
		for (INodo n : descendientes) f.incluirDescendiente(n);
		return f;
	}
}
