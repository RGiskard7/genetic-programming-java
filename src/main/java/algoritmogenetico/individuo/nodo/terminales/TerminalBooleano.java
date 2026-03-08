package algoritmogenetico.individuo.nodo.terminales;

import algoritmogenetico.individuo.nodo.INodo;

/**
 * Terminal booleano: representa una variable logica (p, q, r...) cuyo valor
 * es 0.0 (falso) o 1.0 (verdadero). Se inyecta desde el dominio antes de evaluar.
 */
public class TerminalBooleano extends TerminalAritmetico {

	public TerminalBooleano(String simbolo) {
		super(simbolo);
	}

	@Override
	public INodo copy() {
		TerminalBooleano copia = new TerminalBooleano(simbolo);
		copia.setValor(this.calcular());
		return copia;
	}
}
