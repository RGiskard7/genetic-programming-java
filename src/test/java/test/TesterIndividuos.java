package test;

import algoritmogenetico.dominio.DominioAritmetico;
import algoritmogenetico.dominio.IDominio;
import algoritmogenetico.individuo.IIndividuo;
import algoritmogenetico.individuo.Individuo;
import algoritmogenetico.individuo.nodo.funciones.Funcion;
import algoritmogenetico.individuo.nodo.funciones.FuncionMultiplicacion;
import algoritmogenetico.individuo.nodo.funciones.FuncionResta;
import algoritmogenetico.individuo.nodo.funciones.FuncionSuma;
import algoritmogenetico.individuo.nodo.terminales.Terminal;
import algoritmogenetico.individuo.nodo.terminales.TerminalAritmetico;
import excepciones.ArgsDistintosFuncionesException;

public class TesterIndividuos {

	public static void main(String[] args) {
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

		System.out.println("Funcion multiplicacion: " + multi);
		System.out.println();
		System.out.println("Funcion suma: " + suma);
		System.out.println();
		System.out.println("Funcion resta: " + resta);

		IIndividuo indiv = new Individuo();
		indiv.setExpresion(resta);
		System.out.println();
		System.out.println("INDIVIDUO");
		indiv.writeIndividuo();

		IDominio dominio = new DominioAritmetico();
		int[] argumentos = { 2, 2, 2 };
		String[] funciones = { "+", "*", "-" };
		try {
			indiv.crearIndividuoAleatorio(4, dominio.definirConjuntoTerminales("x"),
					dominio.definirConjuntoFunciones(argumentos, funciones));
		} catch (ArgsDistintosFuncionesException e) {
			e.printStackTrace();
		}

		indiv.writeIndividuo();
	}
}