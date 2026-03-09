package algoritmogenetico.util;

import algoritmogenetico.individuo.nodo.INodo;
import algoritmogenetico.individuo.nodo.terminales.TerminalAritmetico;

/**
 * Utilidades para analizar expresiones (árboles de nodos).
 */
public final class UtilExpresion {

	private UtilExpresion() {}

	/**
	 * Indica si la expresión no depende de ninguna variable: solo constantes y funciones de constantes.
	 * En regresión/clasificación las variables son {@link TerminalAritmetico}; las constantes son
	 * TerminalConstante u otros terminales sin valor inyectado.
	 *
	 * @param raiz raíz del árbol (puede ser null)
	 * @return true si raiz es null o si el árbol no contiene ningún TerminalAritmetico (variable)
	 */
	public static boolean isConstant(INodo raiz) {
		if (raiz == null)
			return true;
		if (raiz instanceof TerminalAritmetico)
			return false;
		for (INodo hijo : raiz.getDescendientes()) {
			if (!isConstant(hijo))
				return false;
		}
		return true;
	}
}
