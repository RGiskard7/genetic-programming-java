package algoritmogenetico.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import algoritmogenetico.individuo.nodo.INodo;
import algoritmogenetico.individuo.nodo.funciones.Funcion;
import algoritmogenetico.individuo.nodo.terminales.TerminalAritmetico;
import algoritmogenetico.individuo.nodo.terminales.TerminalConstante;

/**
 * Exporta un arbol de expresion a notacion prefija, LaTeX (infija) y Python.
 */
public final class ExportadorExpresion {

	private ExportadorExpresion() {}

	/**
	 * Notacion prefija (equivalente a toString del nodo).
	 *
	 * @param nodo raiz del arbol (puede ser null)
	 * @return cadena en notacion prefija, o "" si nodo es null
	 */
	public static String toPrefija(INodo nodo) {
		if (nodo == null) return "";
		return nodo.toString();
	}

	/**
	 * Notacion infija en LaTeX con parentesis para evitar ambiguedad.
	 * Division como \frac{a}{b}; sin, cos, etc. con backslash.
	 *
	 * @param nodo raiz del arbol (puede ser null)
	 * @return cadena LaTeX, o "" si nodo es null
	 */
	public static String toLatex(INodo nodo) {
		if (nodo == null) return "";
		return toLatexRec(nodo);
	}

	private static String toLatexRec(INodo n) {
		if (n instanceof TerminalConstante) {
			double v = ((TerminalConstante) n).getValor();
			return formatConstanteLatex(v);
		}
		if (n instanceof TerminalAritmetico)
			return n.getSimbolo();

		if (n instanceof Funcion) {
			Funcion f = (Funcion) n;
			String sim = f.getSimbolo();
			List<INodo> hijos = f.getDescendientes();
			switch (sim) {
				case "+":
				case "-":
				case "*":
					if (hijos != null && hijos.size() >= 2) {
						String op = " * ".equals(sim) ? " \\cdot " : sim;
						return " ( " + toLatexRec(hijos.get(0)) + " " + op + " " + toLatexRec(hijos.get(1)) + " ) ";
					}
					break;
				case "/":
					if (hijos != null && hijos.size() >= 2)
						return " \\frac{ " + toLatexRec(hijos.get(0)) + " }{ " + toLatexRec(hijos.get(1)) + " } ";
					break;
				case "sin": case "cos": case "exp": case "log":
					if (hijos != null && !hijos.isEmpty())
						return " \\" + sim + " ( " + toLatexRec(hijos.get(0)) + " ) ";
					break;
				case "neg":
					if (hijos != null && !hijos.isEmpty())
						return " - ( " + toLatexRec(hijos.get(0)) + " ) ";
					break;
				case "abs":
					if (hijos != null && !hijos.isEmpty())
						return " | " + toLatexRec(hijos.get(0)) + " | ";
					break;
				case "sqrt":
					if (hijos != null && !hijos.isEmpty())
						return " \\sqrt{ " + toLatexRec(hijos.get(0)) + " } ";
					break;
				case "sqr":
					if (hijos != null && !hijos.isEmpty())
						return " ( " + toLatexRec(hijos.get(0)) + " )^2 ";
					break;
				default:
					if (hijos != null && hijos.size() >= 2)
						return " ( " + toLatexRec(hijos.get(0)) + " " + sim + " " + toLatexRec(hijos.get(1)) + " ) ";
					if (hijos != null && !hijos.isEmpty())
						return " " + sim + " ( " + toLatexRec(hijos.get(0)) + " ) ";
					break;
			}
		}
		return n.getSimbolo();
	}

	private static String formatConstanteLatex(double v) {
		if (Double.isNaN(v) || Double.isInfinite(v)) return "0";
		long entero = Math.round(v);
		if (Math.abs(v - entero) < 1e-12) return String.valueOf(entero);
		return String.valueOf(v);
	}

	/**
	 * Expresion en sintaxis Python (infija), sin definicion de funcion.
	 *
	 * @param nodo raiz del arbol (puede ser null)
	 * @return cadena tipo (x*x)+1, o "" si nodo es null
	 */
	public static String toPython(INodo nodo) {
		if (nodo == null) return "";
		return toPythonRec(nodo);
	}

	private static String toPythonRec(INodo n) {
		if (n instanceof TerminalConstante) {
			double v = ((TerminalConstante) n).getValor();
			return formatConstantePython(v);
		}
		if (n instanceof TerminalAritmetico)
			return n.getSimbolo();

		if (n instanceof Funcion) {
			Funcion f = (Funcion) n;
			String sim = f.getSimbolo();
			List<INodo> hijos = f.getDescendientes();
			switch (sim) {
				case "+":
				case "-":
				case "*":
				case "/":
					if (hijos != null && hijos.size() >= 2)
						return "(" + toPythonRec(hijos.get(0)) + " " + sim + " " + toPythonRec(hijos.get(1)) + ")";
					break;
				case "sin": case "cos": case "exp": case "log": case "sqrt":
					if (hijos != null && !hijos.isEmpty())
						return "math." + sim + "(" + toPythonRec(hijos.get(0)) + ")";
					break;
				case "neg":
					if (hijos != null && !hijos.isEmpty())
						return "(-(" + toPythonRec(hijos.get(0)) + "))";
					break;
				case "abs":
					if (hijos != null && !hijos.isEmpty())
						return "abs(" + toPythonRec(hijos.get(0)) + ")";
					break;
				case "sqr":
					if (hijos != null && !hijos.isEmpty())
						return "(" + toPythonRec(hijos.get(0)) + ")**2";
					break;
				default:
					if (hijos != null && hijos.size() >= 2)
						return "(" + toPythonRec(hijos.get(0)) + " " + sim + " " + toPythonRec(hijos.get(1)) + ")";
					if (hijos != null && !hijos.isEmpty())
						return sim + "(" + toPythonRec(hijos.get(0)) + ")";
					break;
			}
		}
		return n.getSimbolo();
	}

	private static String formatConstantePython(double v) {
		if (Double.isNaN(v) || Double.isInfinite(v)) return "0.0";
		return String.valueOf(v);
	}

	/**
	 * Devuelve una definicion de funcion Python con la expresion del arbol.
	 * Nombres de variables en orden alfabetico (tal como aparecen en el arbol, sin duplicados).
	 *
	 * @param nodo raiz del arbol (puede ser null)
	 * @return cadena tipo "def f(x): return (x*x)+1" o "import math\n\ndef f(x,y): return ...", o "" si nodo es null
	 */
	public static String toPythonDef(INodo nodo) {
		if (nodo == null) return "";
		List<String> vars = new ArrayList<>(nombresVariables(nodo));
		java.util.Collections.sort(vars);
		String params = String.join(", ", vars);
		if (params.isEmpty()) params = "x";
		String expr = toPython(nodo);
		boolean usaMath = expr.contains("math.");
		StringBuilder sb = new StringBuilder();
		if (usaMath) sb.append("import math\n\n");
		sb.append("def f(").append(params).append("):\n    return ").append(expr);
		return sb.toString();
	}

	private static Set<String> nombresVariables(INodo n) {
		Set<String> set = new LinkedHashSet<>();
		collectVars(n, set);
		return set;
	}

	private static void collectVars(INodo n, Set<String> out) {
		if (n == null) return;
		if (n instanceof TerminalAritmetico) {
			out.add(n.getSimbolo());
			return;
		}
		for (INodo h : n.getDescendientes())
			collectVars(h, out);
	}
}
