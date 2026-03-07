package test;

import java.util.ArrayList;
import java.util.List;

import algoritmogenetico.AlgoritmoGenetico;
import algoritmogenetico.IAlgoritmo;
import algoritmogenetico.dominio.DominioAritmetico;
import algoritmogenetico.individuo.IIndividuo;
import algoritmogenetico.individuo.Individuo;
import algoritmogenetico.individuo.nodo.funciones.Funcion;
import algoritmogenetico.individuo.nodo.funciones.FuncionMultiplicacion;
import algoritmogenetico.individuo.nodo.funciones.FuncionResta;
import algoritmogenetico.individuo.nodo.funciones.FuncionSuma;
import algoritmogenetico.individuo.nodo.terminales.Terminal;
import algoritmogenetico.individuo.nodo.terminales.TerminalAritmetico;
import excepciones.ArgsDistintosFuncionesException;
import excepciones.CruceNuloException;

public class TesterCruce {

	public static void main(String[] args) {
		DominioAritmetico dominio = new DominioAritmetico();
		List<Terminal> terminales;
		List<Funcion> funciones;
		try {
			terminales = dominio.definirConjuntoTerminales("x");
			funciones = dominio.definirConjuntoFunciones(new int[] { 2, 2, 2 }, "+", "-", "*");
		} catch (ArgsDistintosFuncionesException e) {
			e.printStackTrace();
			return;
		}
		IAlgoritmo algoritmo = new AlgoritmoGenetico(10, 1, 4, 90, 2, 0.0, 12345L);
		algoritmo.defineConjuntoTerminales(terminales);
		try {
			algoritmo.defineConjuntoFunciones(funciones);
		} catch (ArgsDistintosFuncionesException e) {
			e.printStackTrace();
			return;
		}

		List<IIndividuo> descendientes = new ArrayList<>();
		Terminal x = new TerminalAritmetico("x");
		Funcion suma = new FuncionSuma("+", 2);
		Funcion resta = new FuncionResta("-", 2);
		Funcion multi = new FuncionMultiplicacion("*", 2);
		multi.incluirDescendiente(x);
		multi.incluirDescendiente(x);
		suma.incluirDescendiente(multi);
		suma.incluirDescendiente(x);
		resta.incluirDescendiente(suma);
		resta.incluirDescendiente(multi);

		IIndividuo prog1 = new Individuo();
		prog1.setExpresion(resta);
		prog1.etiquetaNodos();
		IIndividuo prog2 = new Individuo();
		prog2.setExpresion(suma);
		prog2.etiquetaNodos();
		System.out.println();
		System.out.println("PROGENITOR 1");
		prog1.writeIndividuo();
		System.out.println("PROGENITOR 2");
		prog2.writeIndividuo();

		int maxIntentos = 20;
		for (int intento = 0; intento < maxIntentos; intento++) {
			try {
				descendientes = algoritmo.cruce(prog1, prog2);
				break;
			} catch (CruceNuloException e) {
				if (intento == maxIntentos - 1) {
					System.err.println("No se pudo realizar el cruce tras " + maxIntentos + " intentos (puntos de cruce en raíz).");
					e.printStackTrace();
					return;
				}
			}
		}
		System.out.println();
		System.out.println("DESCENDIENTE 1");
		descendientes.get(0).writeIndividuo();
		System.out.println("DESCENDIENTE 2");
		descendientes.get(1).writeIndividuo();
	}
}
