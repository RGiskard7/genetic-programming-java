package test;

import java.io.FileNotFoundException;
import java.io.IOException;

import algoritmogenetico.AlgoritmoGenetico;
import algoritmogenetico.IAlgoritmo;
import algoritmogenetico.dominio.DominioAritmetico;
import algoritmogenetico.dominio.IDominio;
import excepciones.ArgsDistintosFuncionesException;

public class TesterAlgoritmoProgramacionGenetica {

	public static void main(String[] args) {
		int[] argumentos = { 2, 2, 2 };
		String[] funciones = { "+", "-", "*" };
		IDominio dominio = new DominioAritmetico();
		IAlgoritmo alg = new AlgoritmoGenetico(100, 100, 4, 90, 4, 0.15);
		alg.defineConjuntoTerminales(dominio.definirConjuntoTerminales("x"));
		try {
			alg.defineConjuntoFunciones(dominio.definirConjuntoFunciones(argumentos, funciones));
		} catch (ArgsDistintosFuncionesException e) {
			e.printStackTrace();
		}
		try {
			dominio.definirValoresPrueba("valores.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		alg.ejecutar(dominio);
	}
}
