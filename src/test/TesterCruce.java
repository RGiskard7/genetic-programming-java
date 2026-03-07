package test;

import java.util.ArrayList;
import java.util.List;

import algoritmogenetico.individuo.IIndividuo;
import algoritmogenetico.individuo.Individuo;
import algoritmogenetico.individuo.nodo.funciones.Funcion;
import algoritmogenetico.individuo.nodo.funciones.FuncionMultiplicacion;
import algoritmogenetico.individuo.nodo.funciones.FuncionResta;
import algoritmogenetico.individuo.nodo.funciones.FuncionSuma;
import algoritmogenetico.individuo.nodo.terminales.Terminal;
import algoritmogenetico.individuo.nodo.terminales.TerminalAritmetico;
import excepciones.CruceNuloException;

public class TesterCruce {

	public static void main(String[] args) {
		PruebaCruce prueba = new PruebaCruce();
		List<IIndividuo> descendientes = new ArrayList<IIndividuo>();
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
		try {
			descendientes = prueba.cruce(prog1, prog2);
			System.out.println();
			System.out.println("DESCENDIENTE 1");
			descendientes.get(0).writeIndividuo();
			System.out.println("DESCENDIENTE 2");
			descendientes.get(1).writeIndividuo();
		} catch (CruceNuloException e) {
			e.printStackTrace();
		}
	}

}
