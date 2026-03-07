package test;

import java.io.FileNotFoundException;
import java.io.IOException;

import algoritmogenetico.AlgoritmoGenetico;
import algoritmogenetico.IAlgoritmo;
import algoritmogenetico.dominio.DominioAritmetico;
import algoritmogenetico.dominio.IDominio;
import excepciones.ArgsDistintosFuncionesException;

/**
 * Demo que ejecuta el algoritmo sobre valoresX2.txt (puntos y = x²).
 * Objetivo: que el GP encuentre una expresión como ( * x x ) que ajuste los datos.
 */
public class TesterDemoValores {

	public static void main(String[] args) {
		String ficheroDatos = "valoresX2.txt";
		System.out.println("=== Demo: regresión simbólica ===");
		System.out.println("Fichero de datos: " + ficheroDatos + " (puntos y = x², x desde -3 a 3)");
		System.out.println("Objetivo: que el algoritmo encuentre una fórmula que ajuste estos datos (ej. ( * x x )).");
		System.out.println();

		int[] argumentos = { 2, 2, 2 };
		String[] funciones = { "+", "-", "*" };
		IDominio dominio = new DominioAritmetico();
		IAlgoritmo alg = new AlgoritmoGenetico(100, 100, 4, 90, 4, 0.15);
		alg.defineConjuntoTerminales(dominio.definirConjuntoTerminales("x"));
		try {
			alg.defineConjuntoFunciones(dominio.definirConjuntoFunciones(argumentos, funciones));
		} catch (ArgsDistintosFuncionesException e) {
			e.printStackTrace();
			return;
		}
		try {
			dominio.definirValoresPrueba(ficheroDatos);
		} catch (FileNotFoundException e) {
			System.err.println("No se encontró " + ficheroDatos + ". Ejecuta desde la raíz del proyecto.");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		alg.ejecutar(dominio);
	}
}
