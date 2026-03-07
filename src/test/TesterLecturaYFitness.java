package test;

import java.io.IOException;

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

public class TesterLecturaYFitness {

	public static void main(String[] args) throws IOException, IOException {
		IDominio domAritm;
		double fitness;
		domAritm = new DominioAritmetico();
		domAritm.definirValoresPrueba("valoresReducido.txt");
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

		IIndividuo indiv = new Individuo();
		indiv.setExpresion(resta);
		((Individuo) indiv).etiquetaNodos();
		System.out.println();
		System.out.println("INDIVIDUO");
		indiv.writeIndividuo();
		System.out.println();
		fitness = domAritm.calcularFitness(indiv);
		System.out.println("\nFITNESS= " + fitness);
	}
}
