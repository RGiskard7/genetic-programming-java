package algoritmogenetico.util;

import java.util.ArrayList;
import java.util.List;

import algoritmogenetico.individuo.nodo.INodo;
import algoritmogenetico.individuo.nodo.funciones.Funcion;
import algoritmogenetico.individuo.nodo.funciones.FuncionCoseno;
import algoritmogenetico.individuo.nodo.funciones.FuncionDivision;
import algoritmogenetico.individuo.nodo.funciones.FuncionExp;
import algoritmogenetico.individuo.nodo.funciones.FuncionLog;
import algoritmogenetico.individuo.nodo.funciones.FuncionMultiplicacion;
import algoritmogenetico.individuo.nodo.funciones.FuncionNegacion;
import algoritmogenetico.individuo.nodo.funciones.FuncionResta;
import algoritmogenetico.individuo.nodo.funciones.FuncionSeno;
import algoritmogenetico.individuo.nodo.funciones.FuncionSqrt;
import algoritmogenetico.individuo.nodo.funciones.FuncionSuma;
import algoritmogenetico.individuo.nodo.funciones.FuncionValorAbsoluto;
import algoritmogenetico.individuo.nodo.funciones.FuncionCuadrado;
import algoritmogenetico.individuo.nodo.terminales.TerminalConstante;

/**
 * Aplica reglas de simplificacion algebraica a un arbol de expresion,
 * devolviendo un nuevo arbol (no muta el original).
 * Reglas: x+0→x, 0+x→x, x*1→x, 1*x→x, 0*x→0, x*0→0, x-0→x, x/1→x.
 */
public final class SimplificadorExpresion {

	private SimplificadorExpresion() {}

	/**
	 * Devuelve un nuevo arbol equivalente con simplificaciones aplicadas.
	 * El arbol original no se modifica.
	 *
	 * @param raiz raiz del arbol (puede ser null)
	 * @return nuevo arbol simplificado, o null si raiz es null
	 */
	private static final java.util.Set<String> SIMBOLOS_SOPORTADOS = java.util.Set.of(
		"+", "-", "*", "/", "sin", "cos", "neg", "abs", "exp", "log", "sqrt", "sqr");

	public static INodo simplificar(INodo raiz) {
		if (raiz == null)
			return null;
		if (!(raiz instanceof Funcion)) {
			// Terminal
			return raiz.copy();
		}
		Funcion f = (Funcion) raiz;
		String sim = f.getSimbolo();
		if (!SIMBOLOS_SOPORTADOS.contains(sim))
			return raiz.copy();
		int numArgu = f.getNumArgu();
		List<INodo> hijos = f.getDescendientes();
		if (hijos == null || hijos.size() < numArgu)
			return raiz.copy();

		List<INodo> simpl = new ArrayList<>(numArgu);
		for (INodo h : hijos)
			simpl.add(simplificar(h));

		if (numArgu == 2) {
			INodo izq = simpl.get(0);
			INodo der = simpl.get(1);
			Double vIzq = valorConstante(izq);
			Double vDer = valorConstante(der);

			switch (sim) {
				case "+":
					if (vIzq != null && Math.abs(vIzq) < 1e-12) return der;
					if (vDer != null && Math.abs(vDer) < 1e-12) return izq;
					break;
				case "-":
					if (vDer != null && Math.abs(vDer) < 1e-12) return izq;
					break;
				case "*":
					if (vIzq != null && Math.abs(vIzq) < 1e-12) return new TerminalConstante(0);
					if (vDer != null && Math.abs(vDer) < 1e-12) return new TerminalConstante(0);
					if (vIzq != null && Math.abs(vIzq - 1) < 1e-12) return der;
					if (vDer != null && Math.abs(vDer - 1) < 1e-12) return izq;
					break;
				case "/":
					if (vDer != null && Math.abs(vDer - 1) < 1e-12) return izq;
					break;
				default:
					break;
			}
		}

		INodo nuevo = crearFuncion(sim, numArgu);
		for (INodo s : simpl)
			nuevo.incluirDescendiente(s);
		return nuevo;
	}

	private static Double valorConstante(INodo n) {
		if (n instanceof TerminalConstante)
			return ((TerminalConstante) n).getValor();
		return null;
	}

	private static INodo crearFuncion(String simbolo, int numArgu) {
		switch (simbolo) {
			case "+":   return new FuncionSuma(simbolo, numArgu);
			case "-":   return new FuncionResta(simbolo, numArgu);
			case "*":   return new FuncionMultiplicacion(simbolo, numArgu);
			case "/":   return new FuncionDivision(simbolo, numArgu);
			case "sin": return new FuncionSeno(simbolo, numArgu);
			case "cos": return new FuncionCoseno(simbolo, numArgu);
			case "neg": return new FuncionNegacion(simbolo, numArgu);
			case "abs": return new FuncionValorAbsoluto(simbolo, numArgu);
			case "exp": return new FuncionExp(simbolo, numArgu);
			case "log": return new FuncionLog(simbolo, numArgu);
			case "sqrt": return new FuncionSqrt(simbolo, numArgu);
			case "sqr":  return new FuncionCuadrado(simbolo, numArgu);
			default:
				return new FuncionSuma(simbolo, 2);
		}
	}
}
