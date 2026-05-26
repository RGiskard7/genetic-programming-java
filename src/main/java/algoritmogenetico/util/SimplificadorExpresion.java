package algoritmogenetico.util;

import java.util.ArrayList;
import java.util.List;

import algoritmogenetico.individuo.nodo.INodo;
import algoritmogenetico.individuo.nodo.funciones.Funcion;
import algoritmogenetico.individuo.nodo.funciones.FuncionCoseno;
import algoritmogenetico.individuo.nodo.funciones.FuncionCuadrado;
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
import algoritmogenetico.individuo.nodo.terminales.TerminalConstante;

/**
 * Aplica reglas de simplificacion algebraica y evaluacion de constantes a un
 * arbol de expresion, devolviendo un nuevo arbol (no muta el original).
 *
 * <p>Reglas algebraicas: x+0→x, 0+x→x, x*1→x, 1*x→x, 0*x→0, x*0→0, x-0→x, x/1→x.</p>
 * <p>Evaluacion de constantes: subexpresiones con todos sus operandos constantes se reducen a
 * un {@link TerminalConstante}. Excepción: divisiones con denominador ≈ 0 no se pliegan
 * para que la singularidad quede visible en el árbol (aunque {@link algoritmogenetico.individuo.nodo.funciones.FuncionDivision}
 * la maneje en tiempo de ejecución con la división protegida).</p>
 */
public final class SimplificadorExpresion {

	private SimplificadorExpresion() {}

	private static final java.util.Set<String> SIMBOLOS_SOPORTADOS = java.util.Set.of(
		"+", "-", "*", "/", "sin", "cos", "neg", "abs", "exp", "log", "sqrt", "sqr");

	/**
	 * Devuelve un nuevo arbol equivalente con simplificaciones aplicadas.
	 * El arbol original no se modifica.
	 *
	 * @param raiz raiz del arbol (puede ser null)
	 * @return nuevo arbol simplificado, o null si raiz es null
	 */
	public static INodo simplificar(INodo raiz) {
		if (raiz == null)
			return null;
		if (!(raiz instanceof Funcion))
			return raiz.copy();

		Funcion f = (Funcion) raiz;
		String sim = f.getSimbolo();
		if (!SIMBOLOS_SOPORTADOS.contains(sim))
			return raiz.copy();

		int numArgu = f.getNumArgu();
		List<INodo> hijos = f.getDescendientes();
		if (hijos == null || hijos.size() < numArgu)
			return raiz.copy();

		// Simplificar hijos recursivamente
		List<INodo> simpl = new ArrayList<>(numArgu);
		for (INodo h : hijos)
			simpl.add(simplificar(h));

		// Reglas algebraicas (binarias)
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

			// Evaluación de constantes binaria: (2 + 3) → 5
			if (vIzq != null && vDer != null) {
				Double resultado = evaluarBinaria(sim, vIzq, vDer);
				if (resultado != null && Double.isFinite(resultado))
					return new TerminalConstante(resultado);
			}
		}

		// Evaluación de constantes unaria: (sin 0) → 0, (neg 3) → -3
		if (numArgu == 1) {
			Double vHijo = valorConstante(simpl.get(0));
			if (vHijo != null) {
				Double resultado = evaluarUnaria(sim, vHijo);
				if (resultado != null && Double.isFinite(resultado))
					return new TerminalConstante(resultado);
			}
		}

		// Reconstruir nodo con hijos simplificados
		INodo nuevo = crearFuncion(sim, numArgu);
		for (INodo s : simpl)
			nuevo.incluirDescendiente(s);
		return nuevo;
	}

	/**
	 * Simplifica el arbol y devuelve su representacion en cadena (notacion prefija).
	 * Si la simplificacion no cambia nada devuelve el mismo toString que el original.
	 *
	 * @param raiz raiz del arbol (puede ser null)
	 * @return expresion simplificada como cadena, o "" si raiz es null
	 */
	public static String toStringSimplificado(INodo raiz) {
		if (raiz == null) return "";
		INodo simplificado = simplificar(raiz);
		return simplificado != null ? simplificado.toString() : "";
	}

	// -------------------------------------------------------------------------
	// Helpers de evaluación numérica
	// -------------------------------------------------------------------------

	private static Double evaluarBinaria(String op, double a, double b) {
		switch (op) {
			case "+": return a + b;
			case "-": return a - b;
			case "*": return a * b;
			// Denominador ≈ 0: no plegar — preservar la singularidad visible en el árbol
			case "/": return Math.abs(b) < FuncionDivision.epsilon ? null : a / b;
			default:  return null;
		}
	}

	private static Double evaluarUnaria(String op, double x) {
		switch (op) {
			case "neg":  return -x;
			case "abs":  return Math.abs(x);
			case "sqr":  return x * x;
			case "sin":  return Math.sin(x);
			case "cos":  return Math.cos(x);
			case "exp":  return x > 700 ? null : (x < -700 ? 0.0 : Math.exp(x));
			case "log":  return Math.log(1.0 + Math.abs(x));
			case "sqrt": return Math.sqrt(Math.abs(x));
			default:     return null;
		}
	}

	// -------------------------------------------------------------------------
	// Helpers de árbol
	// -------------------------------------------------------------------------

	private static Double valorConstante(INodo n) {
		if (n instanceof TerminalConstante)
			return ((TerminalConstante) n).getValor();
		return null;
	}

	private static INodo crearFuncion(String simbolo, int numArgu) {
		switch (simbolo) {
			case "+":    return new FuncionSuma(simbolo, numArgu);
			case "-":    return new FuncionResta(simbolo, numArgu);
			case "*":    return new FuncionMultiplicacion(simbolo, numArgu);
			case "/":    return new FuncionDivision(simbolo, numArgu);
			case "sin":  return new FuncionSeno(simbolo, numArgu);
			case "cos":  return new FuncionCoseno(simbolo, numArgu);
			case "neg":  return new FuncionNegacion(simbolo, numArgu);
			case "abs":  return new FuncionValorAbsoluto(simbolo, numArgu);
			case "exp":  return new FuncionExp(simbolo, numArgu);
			case "log":  return new FuncionLog(simbolo, numArgu);
			case "sqrt": return new FuncionSqrt(simbolo, numArgu);
			case "sqr":  return new FuncionCuadrado(simbolo, numArgu);
			default:     return new FuncionSuma(simbolo, 2);
		}
	}
}
