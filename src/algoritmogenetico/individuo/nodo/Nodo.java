package algoritmogenetico.individuo.nodo;

import java.util.ArrayList;
import java.util.List;

/**
 * La clase Nodo implementa toda la funcionalidad exigida por la interfaz INodo.
 */
public abstract class Nodo implements INodo {
	private static int numNodos;
	private final int id;
	protected String simbolo;
	protected List<INodo> descendientes;

	/**
	 * Constructor de la clase abstracta Nodo.
	 *
	 * @param simbolo el simbolo que representa el nodo
	 */
	public Nodo(String simbolo) {
		id = ++numNodos;
		this.simbolo = simbolo;
		descendientes = new ArrayList<>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.individuo.nodo.INodo#getSimbolo()
	 */
	@Override
	public String getSimbolo() {
		return simbolo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.individuo.nodo.INodo#getDescendientes()
	 */
	@Override
	public List<INodo> getDescendientes() {
		return descendientes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * algoritmogenetico.individuo.nodo.INodo#incluirDescendiente(algoritmogenetico.individuo.nodo.INodo)
	 */
	@Override
	public void incluirDescendiente(INodo nodo) {
		INodo newNodo = nodo.copy();
		descendientes.add(newNodo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.individuo.nodo.INodo#calcular()
	 */
	@Override
	public double calcular() {
		return 0.0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.individuo.nodo.INodo#copy()
	 */
	@Override
	public abstract INodo copy();

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String cadena = "( " + simbolo + " ";
		for (INodo n : descendientes)
			cadena += n + " ";
		cadena += ")";
		return cadena;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Nodo other = (Nodo) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}
}
