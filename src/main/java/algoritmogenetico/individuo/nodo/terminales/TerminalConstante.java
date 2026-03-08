package algoritmogenetico.individuo.nodo.terminales;

import algoritmogenetico.individuo.nodo.INodo;

/**
 * Terminal con un valor numerico fijo (constante efimera). No se modifica con setValor.
 */
public class TerminalConstante extends Terminal {

	private final double valor;

	/**
	 * Crea un terminal constante con el valor dado.
	 *
	 * @param valor valor numerico fijo
	 */
	public TerminalConstante(double valor) {
		super(String.valueOf(valor));
		this.valor = valor;
	}

	@Override
	public double calcular() {
		return valor;
	}

	@Override
	public INodo copy() {
		return new TerminalConstante(valor);
	}

	@Override
	public String toString() {
		return String.valueOf(valor);
	}
}
