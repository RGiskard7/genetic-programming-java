package algoritmogenetico.individuo.nodo.terminales;

import algoritmogenetico.individuo.nodo.INodo;

/**
 * La clase TerminalAritmetico es un tipo de Terminal concreto que trabaja con
 * valores aritmeticos, es decir, que representa un valor aritmetico
 * determinado.
 */
public class TerminalAritmetico extends Terminal {
	private double valor;

	/**
	 * Permite instanciar un nuevo objeto de tipo TerminalAritmetico.
	 *
	 * @param simbolo el simbolo que representa al TerminalAritmetico
	 */
	public TerminalAritmetico(String simbolo) {
		super(simbolo);
	}

	/**
	 * Permite instanciar un nuevo objeto de tipo TerminalAritmetico con valor.
	 *
	 * @param simbolo el simbolo que representa al TerminalAritmetico
	 * @param valor   el valor numerico del terminal
	 */
	public TerminalAritmetico(String simbolo, double valor) {
		super(simbolo);
		this.valor = valor;
	}

	/**
	 * Devuelve el valor aritmetico asociado al TerminalAritmetico.
	 *
	 * @return un valor tipo double
	 */
	@Override
	public double calcular() {
		return valor;
	}

	/**
	 * Permite establecer el valor aritmetico que representa el TerminalAritmetico.
	 *
	 * @param valor  el valor del TerminalAritmetico
	 */
	public void setValor(double valor) {
		this.valor = valor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.individuo.nodo.Nodo#copy()
	 */
	@Override
	public INodo copy() {
		TerminalAritmetico nodo = new TerminalAritmetico(simbolo);
		nodo.valor = valor;
		return nodo;
	}
}
