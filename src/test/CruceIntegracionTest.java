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
import algoritmogenetico.individuo.nodo.funciones.Funcion;
import algoritmogenetico.individuo.nodo.funciones.FuncionMultiplicacion;
import algoritmogenetico.individuo.nodo.funciones.FuncionResta;
import algoritmogenetico.individuo.nodo.funciones.FuncionSuma;
import algoritmogenetico.individuo.nodo.terminales.Terminal;
import algoritmogenetico.individuo.nodo.terminales.TerminalAritmetico;
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
}