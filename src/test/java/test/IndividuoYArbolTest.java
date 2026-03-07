package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import algoritmogenetico.dominio.DominioAritmetico;
import algoritmogenetico.individuo.IIndividuo;
import algoritmogenetico.individuo.Individuo;
import algoritmogenetico.individuo.nodo.INodo;
import algoritmogenetico.individuo.nodo.funciones.Funcion;
import algoritmogenetico.individuo.nodo.funciones.FuncionMultiplicacion;
import algoritmogenetico.individuo.nodo.funciones.FuncionResta;
import algoritmogenetico.individuo.nodo.funciones.FuncionSuma;
import algoritmogenetico.individuo.nodo.terminales.Terminal;
import algoritmogenetico.individuo.nodo.terminales.TerminalAritmetico;
import excepciones.ArgsDistintosFuncionesException;

/**
 * Tests de Individuo y arbol de expresiones: creacion aleatoria, numero de
 * nodos, crearSubarbolAleatorio, reemplazarNodo (raiz e interno), calcularExpresion.
 */
@DisplayName("Individuo y arbol de expresiones")
class IndividuoYArbolTest {

	private List<Terminal> terminales;
	private List<Funcion> funciones;
	private Random rng;

	@BeforeEach
	void setUp() throws ArgsDistintosFuncionesException {
		DominioAritmetico dominio = new DominioAritmetico();
		terminales = dominio.definirConjuntoTerminales("x");
		funciones = dominio.definirConjuntoFunciones(new int[] { 2, 2, 2 }, "+", "-", "*");
		rng = new Random(42);
	}

	@Test
	@DisplayName("Crear individuo aleatorio tiene expresion y al menos un nodo")
	void crearIndividuoAleatorio_tieneExpresion() {
		Individuo ind = new Individuo();
		ind.crearIndividuoAleatorio(3, terminales, funciones, rng);
		assertNotNull(ind.getExpresion());
		assertTrue(ind.getNumeroNodos() >= 1);
	}

	@Test
	@DisplayName("Numero de nodos es coherente con la profundidad")
	void numeroNodos_coherenteConProfundidad() {
		Individuo ind = new Individuo();
		ind.crearIndividuoAleatorio(2, terminales, funciones, rng);
		int n = ind.getNumeroNodos();
		assertTrue(n >= 1 && n <= 7);
	}

	@Test
	@DisplayName("crearSubarbolAleatorio devuelve nodo no nulo")
	void crearSubarbolAleatorio_devuelveNodo() {
		Individuo aux = new Individuo();
		INodo sub = aux.crearSubarbolAleatorio(2, terminales, funciones, rng);
		assertNotNull(sub);
		assertNotNull(sub.getDescendientes());
	}

	@Test
	@DisplayName("reemplazarNodo en raiz sustituye toda la expresion")
	void reemplazarNodo_raiz_sustituyeExpresion() {
		TerminalAritmetico x = new TerminalAritmetico("x", 5.0);
		Individuo ind = new Individuo();
		ind.setExpresion(x);
		ind.etiquetaNodos();
		Individuo aux = new Individuo();
		INodo nuevo = aux.crearSubarbolAleatorio(2, terminales, funciones, rng);
		ind.reemplazarNodo(1, nuevo);
		assertEquals(nuevo, ind.getExpresion());
	}

	@Test
	@DisplayName("reemplazarNodo en hijo sustituye solo ese subarbol")
	void reemplazarNodo_hijo_sustituyeSubarbol() {
		TerminalAritmetico x = new TerminalAritmetico("x", 1.0);
		FuncionSuma suma = new FuncionSuma("+", 2);
		suma.incluirDescendiente(x.copy());
		suma.incluirDescendiente(x.copy());
		Individuo ind = new Individuo();
		ind.setExpresion(suma);
		ind.etiquetaNodos();
		INodo terminalSolo = terminales.get(0).copy();
		ind.reemplazarNodo(2, terminalSolo);
		// Raiz + 2 hijos (uno reemplazado por terminal, otro sigue siendo x) = 3 nodos
		assertTrue(ind.getNumeroNodos() >= 2 && ind.getNumeroNodos() <= 3);
		assertNotNull(ind.getExpresion());
	}

	@Test
	@DisplayName("calcularExpresion para arbol manual (x + x) con x=3")
	void calcularExpresion_arbolManual() {
		TerminalAritmetico x = new TerminalAritmetico("x", 3.0);
		FuncionSuma suma = new FuncionSuma("+", 2);
		suma.incluirDescendiente(x.copy());
		suma.incluirDescendiente(x.copy());
		IIndividuo ind = new Individuo();
		ind.setExpresion(suma);
		assertEquals(6.0, ind.calcularExpresion(), 1e-9);
	}
}
