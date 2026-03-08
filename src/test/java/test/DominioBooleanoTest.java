package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import algoritmogenetico.dominio.DominioBooleano;
import algoritmogenetico.individuo.IIndividuo;
import algoritmogenetico.individuo.Individuo;
import algoritmogenetico.individuo.nodo.funciones.Funcion;
import algoritmogenetico.individuo.nodo.funciones.FuncionAnd;
import algoritmogenetico.individuo.nodo.terminales.Terminal;
import algoritmogenetico.individuo.nodo.terminales.TerminalBooleano;

/**
 * Tests del dominio booleano: carga de tabla de verdad, calculo de fitness
 * para expresiones logicas conocidas y verificacion del objetivo.
 */
@DisplayName("Dominio booleano y logica")
class DominioBooleanoTest {

	private DominioBooleano dominio;

	@BeforeEach
	void setUp() {
		dominio = new DominioBooleano();
	}

	@Test
	@DisplayName("definirConjuntoTerminales devuelve TerminalBooleano")
	void terminales_sonTerminalBooleano() {
		List<Terminal> lista = dominio.definirConjuntoTerminales("p", "q");
		assertNotNull(lista);
		assertEquals(2, lista.size());
		assertTrue(lista.get(0) instanceof TerminalBooleano);
		assertTrue(lista.get(1) instanceof TerminalBooleano);
	}

	@Test
	@DisplayName("fitnessBuscado es el numero de filas de la tabla de verdad")
	void fitnessBuscado_esNumeroDeFilas(@TempDir Path tempDir) throws Exception {
		Path f = tempDir.resolve("tabla.txt");
		// AND de 2 variables: 4 filas
		Files.writeString(f, "0.0\t0.0\t0.0\n0.0\t1.0\t0.0\n1.0\t0.0\t0.0\n1.0\t1.0\t1.0\n");
		dominio.definirConjuntoTerminales("p", "q");
		dominio.definirValoresPrueba(f.toString());
		assertEquals(4.0, dominio.fitnessBuscado(), 1e-9);
	}

	@Test
	@DisplayName("calcularFitness AND(p,q) en tabla AND da fitness maximo")
	void calcularFitness_and_tablaAnd_fitnessMaximo(@TempDir Path tempDir) throws Exception {
		Path f = tempDir.resolve("and.txt");
		Files.writeString(f, "0.0\t0.0\t0.0\n0.0\t1.0\t0.0\n1.0\t0.0\t0.0\n1.0\t1.0\t1.0\n");
		dominio.definirConjuntoTerminales("p", "q");
		dominio.definirValoresPrueba(f.toString());

		// Construir AND(p, q) manualmente
		TerminalBooleano p = new TerminalBooleano("p");
		TerminalBooleano q = new TerminalBooleano("q");
		FuncionAnd and = new FuncionAnd("AND", 2);
		and.incluirDescendiente(p);
		and.incluirDescendiente(q);
		IIndividuo ind = new Individuo();
		ind.setExpresion(and);

		double fitness = dominio.calcularFitness(ind);
		// 4 filas correctas, 3 nodos → fitness = 4 - ALPHA * 3
		double esperado = 4.0 - DominioBooleano.ALPHA * 3;
		assertEquals(esperado, fitness, 1e-9);
	}

	@Test
	@DisplayName("calcularFitness OR(p,q) en tabla AND da fitness parcial")
	void calcularFitness_or_tablaAnd_fitnessParcial(@TempDir Path tempDir) throws Exception {
		Path f = tempDir.resolve("and.txt");
		// OR acierta en 0,0,1,1 pero la tabla AND espera 0,0,0,1 → error en la fila 3 (p=1,q=0)
		Files.writeString(f, "0.0\t0.0\t0.0\n0.0\t1.0\t0.0\n1.0\t0.0\t0.0\n1.0\t1.0\t1.0\n");
		dominio.definirConjuntoTerminales("p", "q");
		dominio.definirValoresPrueba(f.toString());

		// Construir OR(p, q) manualmente usando FuncionOr
		TerminalBooleano p = new TerminalBooleano("p");
		TerminalBooleano q = new TerminalBooleano("q");
		algoritmogenetico.individuo.nodo.funciones.FuncionOr or =
				new algoritmogenetico.individuo.nodo.funciones.FuncionOr("OR", 2);
		or.incluirDescendiente(p);
		or.incluirDescendiente(q);
		IIndividuo ind = new Individuo();
		ind.setExpresion(or);

		double fitness = dominio.calcularFitness(ind);
		// OR falla en (0,1→OR=1 pero AND=0) y (1,0→OR=1 pero AND=0): 2 errores
		// 2 correctas, 3 nodos → fitness = 2 - ALPHA * 3
		double esperado = 2.0 - DominioBooleano.ALPHA * 3;
		assertEquals(esperado, fitness, 1e-9);
	}

	@Test
	@DisplayName("definirConjuntoFunciones con AND, OR, NOT, XOR devuelve lista no nula")
	void definirFunciones_booleanas_devuelveListaNoNula() throws Exception {
		List<Funcion> lista = dominio.definirConjuntoFunciones(
				new int[]{ 2, 2, 1, 2 }, "AND", "OR", "NOT", "XOR");
		assertNotNull(lista);
		assertEquals(4, lista.size());
	}

	@Test
	@DisplayName("fichero vacio: fitnessBuscado 0 y calcularFitness devuelve 0")
	void ficheroVacio_fitnessBuscadoCero(@TempDir Path tempDir) throws Exception {
		Path f = tempDir.resolve("vacio.txt");
		Files.writeString(f, "");
		dominio.definirConjuntoTerminales("p");
		dominio.definirValoresPrueba(f.toString());
		assertEquals(0.0, dominio.fitnessBuscado(), 1e-9);
		IIndividuo ind = new Individuo();
		ind.setExpresion(new TerminalBooleano("p"));
		assertEquals(0.0, dominio.calcularFitness(ind), 1e-9);
	}
}
