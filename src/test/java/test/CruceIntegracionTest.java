package test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
import algoritmogenetico.individuo.nodo.funciones.Funcion;
import algoritmogenetico.individuo.nodo.terminales.Terminal;
import excepciones.ArgsDistintosFuncionesException;
import excepciones.CruceNuloException;

/**
 * Tests del cruce: dos descendientes, progenitores no modificados, y
 * integracion con el algoritmo (reintentos ante CruceNuloException).
 */
@DisplayName("Cruce por subarbol")
class CruceIntegracionTest {

	private IAlgoritmo algoritmo;
	private List<Terminal> terminales;
	private List<Funcion> funciones;

	@BeforeEach
	void setUp() throws ArgsDistintosFuncionesException {
		DominioAritmetico dominio = new DominioAritmetico();
		terminales = dominio.definirConjuntoTerminales("x");
		funciones = dominio.definirConjuntoFunciones(new int[] { 2, 2, 2 }, "+", "-", "*");
		algoritmo = new AlgoritmoGenetico(10, 5, 3, 80, 3, 0.0, 777L);
		algoritmo.defineConjuntoTerminales(terminales);
		algoritmo.defineConjuntoFunciones(funciones);
	}

	@Test
	@DisplayName("cruce devuelve exactamente dos descendientes")
	void cruce_devuelveDosDescendientes() throws CruceNuloException {
		Individuo p1 = new Individuo();
		Individuo p2 = new Individuo();
		p1.crearIndividuoAleatorio(3, terminales, funciones, new Random(1));
		p2.crearIndividuoAleatorio(3, terminales, funciones, new Random(2));
		List<IIndividuo> hijos = algoritmo.cruce(p1, p2);
		assertNotNull(hijos);
		assertEquals(2, hijos.size());
		assertNotNull(hijos.get(0).getExpresion());
		assertNotNull(hijos.get(1).getExpresion());
		assertTrue(hijos.get(0).getNumeroNodos() >= 1);
		assertTrue(hijos.get(1).getNumeroNodos() >= 1);
	}

	@Test
	@DisplayName("cruce no modifica a los progenitores")
	void cruce_noModificaProgenitores() throws CruceNuloException {
		Individuo p1 = new Individuo();
		Individuo p2 = new Individuo();
		p1.crearIndividuoAleatorio(3, terminales, funciones, new Random(10));
		p2.crearIndividuoAleatorio(3, terminales, funciones, new Random(20));
		int n1 = p1.getNumeroNodos();
		int n2 = p2.getNumeroNodos();
		algoritmo.cruce(p1, p2);
		assertEquals(n1, p1.getNumeroNodos());
		assertEquals(n2, p2.getNumeroNodos());
	}

	@Test
	@DisplayName("crearNuevaPoblacion mantiene tamaño con cruce y reintentos")
	void crearNuevaPoblacion_mantieneTamano() {
		algoritmo.crearPoblacion();
		algoritmo.crearNuevaPoblacion();
		// No tenemos getter de poblacion; verificamos que no lance y que una segunda generacion funciona
		algoritmo.crearNuevaPoblacion();
	}

	@Test
	@DisplayName("cruce con un progenitor de un solo nodo puede completarse con reintentos")
	void cruce_unProgenitorUnNodo_completaConReintentos() throws CruceNuloException {
		Individuo p1 = new Individuo();
		p1.setExpresion(terminales.get(0).copy());
		p1.etiquetaNodos();
		Individuo p2 = new Individuo();
		p2.crearIndividuoAleatorio(3, terminales, funciones, new Random(2));
		List<IIndividuo> hijos = null;
		int maxIntentos = 20;
		for (int i = 0; i < maxIntentos; i++) {
			try {
				hijos = algoritmo.cruce(p1, p2);
				break;
			} catch (CruceNuloException e) {
				if (i == maxIntentos - 1) {
					throw e;
				}
			}
		}
		assertNotNull(hijos);
		assertEquals(2, hijos.size());
		assertTrue(hijos.get(0).getNumeroNodos() >= 1);
		assertTrue(hijos.get(1).getNumeroNodos() >= 1);
	}

	@Test
	@DisplayName("cruce con funciones unarias (sin, neg) no lanza IndexOutOfBounds")
	void cruce_conFuncionesUnarias_noLanza() throws ArgsDistintosFuncionesException {
		DominioAritmetico dom = new DominioAritmetico();
		List<Terminal> terms = dom.definirConjuntoTerminales("x");
		List<Funcion> funcs = dom.definirConjuntoFunciones(new int[]{1, 1}, "sin", "neg");
		AlgoritmoGenetico alg = new AlgoritmoGenetico(10, 5, 3, 80, 3, 0.2, 42L);
		alg.defineConjuntoTerminales(terms);
		alg.defineConjuntoFunciones(funcs);

		Individuo p1 = new Individuo();
		Individuo p2 = new Individuo();
		p1.crearIndividuoAleatorio(3, terms, funcs, new Random(100));
		p2.crearIndividuoAleatorio(3, terms, funcs, new Random(200));

		for (int seed = 0; seed < 50; seed++) {
			AlgoritmoGenetico algSeed = new AlgoritmoGenetico(10, 5, 3, 80, 3, 0.2, (long) seed);
			algSeed.defineConjuntoTerminales(terms);
			algSeed.defineConjuntoFunciones(funcs);
			try {
				List<IIndividuo> hijos = algSeed.cruce(p1, p2);
				assertEquals(2, hijos.size());
				assertTrue(hijos.get(0).getNumeroNodos() >= 1);
				assertTrue(hijos.get(1).getNumeroNodos() >= 1);
			} catch (CruceNuloException e) {
				// Acceptable
			}
		}
	}

	@Test
	@DisplayName("cruce con mezcla de funciones binarias y unarias no lanza")
	void cruce_conFuncionesMixtas_noLanza() throws ArgsDistintosFuncionesException {
		DominioAritmetico dom = new DominioAritmetico();
		List<Terminal> terms = dom.definirConjuntoTerminales("x");
		List<Funcion> funcs = dom.definirConjuntoFunciones(
				new int[]{2, 2, 1, 1, 1}, "+", "*", "sin", "neg", "abs");
		AlgoritmoGenetico alg = new AlgoritmoGenetico(20, 5, 4, 80, 3, 0.15, 55L);
		alg.defineConjuntoTerminales(terms);
		alg.defineConjuntoFunciones(funcs);

		Individuo p1 = new Individuo();
		Individuo p2 = new Individuo();
		p1.crearIndividuoAleatorio(4, terms, funcs, new Random(300));
		p2.crearIndividuoAleatorio(4, terms, funcs, new Random(400));

		for (int seed = 0; seed < 50; seed++) {
			AlgoritmoGenetico algSeed = new AlgoritmoGenetico(20, 5, 4, 80, 3, 0.15, (long) seed);
			algSeed.defineConjuntoTerminales(terms);
			algSeed.defineConjuntoFunciones(funcs);
			try {
				List<IIndividuo> hijos = algSeed.cruce(p1, p2);
				assertEquals(2, hijos.size());
				assertNotNull(hijos.get(0).getExpresion());
				assertNotNull(hijos.get(1).getExpresion());
			} catch (CruceNuloException e) {
				// Acceptable
			}
		}
	}
}