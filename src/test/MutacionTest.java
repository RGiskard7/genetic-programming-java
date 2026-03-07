package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import algoritmogenetico.AlgoritmoGenetico;
import algoritmogenetico.IAlgoritmo;
import algoritmogenetico.dominio.DominioAritmetico;
import algoritmogenetico.individuo.IIndividuo;
import algoritmogenetico.individuo.Individuo;
import algoritmogenetico.individuo.nodo.INodo;
import algoritmogenetico.individuo.nodo.funciones.Funcion;
import algoritmogenetico.individuo.nodo.terminales.Terminal;
import excepciones.ArgsDistintosFuncionesException;

/**
 * Tests del operador de mutacion: que mutar devuelve individuo valido, que con
 * semilla fija puede dar resultado distinto al original, y que el original no
 * se modifica.
 */
@DisplayName("Operador de mutacion")
class MutacionTest {

	private IAlgoritmo algoritmo;
	private List<Terminal> terminales;
	private List<Funcion> funciones;

	@BeforeEach
	void setUp() throws ArgsDistintosFuncionesException {
		DominioAritmetico dominio = new DominioAritmetico();
		terminales = dominio.definirConjuntoTerminales("x");
		funciones = dominio.definirConjuntoFunciones(new int[] { 2, 2, 2 }, "+", "-", "*");
		algoritmo = new AlgoritmoGenetico(10, 5, 3, 80, 3, 0.2, 12345L);
		algoritmo.defineConjuntoTerminales(terminales);
		algoritmo.defineConjuntoFunciones(funciones);
	}

	@Test
	@DisplayName("mutar devuelve individuo no nulo con expresion valida")
	void mutar_devuelveIndividuoValido() {
		Individuo ind = new Individuo();
		ind.crearIndividuoAleatorio(3, terminales, funciones, new Random(1));
		IIndividuo mutado = algoritmo.mutar(ind);
		assertNotNull(mutado);
		assertNotNull(mutado.getExpresion());
		assertTrue(mutado.getNumeroNodos() >= 1);
	}

	@Test
	@DisplayName("mutar con semilla fija produce individuo valido")
	void mutar_conSemilla_produceIndividuoValido() throws ArgsDistintosFuncionesException {
		IAlgoritmo alg = new AlgoritmoGenetico(10, 2, 3, 50, 2, 1.0, 999L);
		alg.defineConjuntoTerminales(terminales);
		alg.defineConjuntoFunciones(funciones);
		Individuo ind = new Individuo();
		ind.crearIndividuoAleatorio(4, terminales, funciones, new Random(88));
		IIndividuo mutado = alg.mutar(ind);
		assertNotNull(mutado.getExpresion());
		assertTrue(mutado.getNumeroNodos() >= 1);
	}

	@Test
	@DisplayName("mutar no modifica el individuo original")
	void mutar_noModificaOriginal() {
		Individuo ind = new Individuo();
		ind.crearIndividuoAleatorio(3, terminales, funciones, new Random(42));
		INodo expresionOrig = ind.getExpresion();
		int numNodosOrig = ind.getNumeroNodos();
		IIndividuo mutado = algoritmo.mutar(ind);
		assertNotNull(mutado);
		assertEquals(expresionOrig, ind.getExpresion());
		assertEquals(numNodosOrig, ind.getNumeroNodos());
	}
}
