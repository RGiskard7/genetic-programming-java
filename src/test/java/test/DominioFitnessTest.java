package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import algoritmogenetico.dominio.DominioAritmetico;
import algoritmogenetico.dominio.IDominio;
import algoritmogenetico.individuo.IIndividuo;
import algoritmogenetico.individuo.Individuo;
import algoritmogenetico.individuo.nodo.terminales.TerminalAritmetico;
import excepciones.ArgsDistintosFuncionesException;

/**
 * Tests del dominio aritmetico y fitness: definicion de terminales/funciones,
 * carga de datos desde fichero, calculo de fitness para expresion conocida.
 */
@DisplayName("Dominio aritmetico y fitness")
class DominioFitnessTest {

	private IDominio dominio;

	@BeforeEach
	void setUp() {
		dominio = new DominioAritmetico();
	}

	@Test
	@DisplayName("definirConjuntoTerminales devuelve lista no vacia")
	void definirTerminales_devuelveLista() {
		var list = dominio.definirConjuntoTerminales("x");
		assertNotNull(list);
		assertEquals(1, list.size());
	}

	@Test
	@DisplayName("definirConjuntoFunciones con argumentos coherentes devuelve lista")
	void definirFunciones_coherentes_devuelveLista() throws ArgsDistintosFuncionesException {
		var list = dominio.definirConjuntoFunciones(new int[] { 2, 2, 2 }, "+", "-", "*");
		assertNotNull(list);
		assertEquals(3, list.size());
	}

	@Test
	@DisplayName("definirConjuntoFunciones con longitud distinta lanza excepcion")
	void definirFunciones_longitudDistinta_lanza() {
		assertThrows(ArgsDistintosFuncionesException.class, () ->
				dominio.definirConjuntoFunciones(new int[] { 2, 2 }, "+", "-", "*"));
	}

	@Test
	@DisplayName("definirValoresPrueba carga datos y fitnessBuscado es numero de lineas")
	void definirValoresPrueba_cargaDatos(@TempDir Path tempDir) throws Exception {
		Path f = tempDir.resolve("puntos.txt");
		Files.writeString(f, "0.0\t0.0\n1.0\t1.0\n2.0\t4.0\n");
		dominio.definirValoresPrueba(f.toString());
		assertEquals(3.0, dominio.fitnessBuscado(), 1e-9);
	}

	@Test
	@DisplayName("calcularFitness para expresion x con datos (0,0) y (1,1) da 2")
	void calcularFitness_expresionX_dosPuntos(@TempDir Path tempDir) throws Exception {
		Path f = tempDir.resolve("dos.txt");
		Files.writeString(f, "0.0\t0.0\n1.0\t1.0\n");
		dominio.definirValoresPrueba(f.toString());
		TerminalAritmetico x = new TerminalAritmetico("x");
		IIndividuo ind = new Individuo();
		ind.setExpresion(x);
		double fitness = dominio.calcularFitness(ind);
		assertEquals(2.0, fitness, 1e-9);
		assertEquals(2.0, ind.getFitness(), 1e-9);
	}

	@Test
	@DisplayName("calcularFitness asigna fitness al individuo")
	void calcularFitness_asignaFitnessAlIndividuo(@TempDir Path tempDir) throws Exception {
		Path f = tempDir.resolve("uno.txt");
		Files.writeString(f, "2.0\t4.0\n");
		dominio.definirValoresPrueba(f.toString());
		TerminalAritmetico x = new TerminalAritmetico("x");
		IIndividuo ind = new Individuo();
		ind.setExpresion(x);
		dominio.calcularFitness(ind);
		assertTrue(ind.getFitness() >= 0 && ind.getFitness() <= 1);
	}

	@Test
	@DisplayName("fichero vacío: fitnessBuscado 0 y calcularFitness devuelve 0")
	void ficheroVacio_fitnessBuscadoCero_calcularFitnessDevuelveCero(@TempDir Path tempDir) throws Exception {
		Path f = tempDir.resolve("vacio.txt");
		Files.writeString(f, "");
		dominio.definirValoresPrueba(f.toString());
		assertEquals(0.0, dominio.fitnessBuscado(), 1e-9);
		IIndividuo ind = new Individuo();
		ind.setExpresion(new TerminalAritmetico("x"));
		double fitness = dominio.calcularFitness(ind);
		assertEquals(0.0, fitness, 1e-9);
	}
}
