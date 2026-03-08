package test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import algoritmogenetico.AlgoritmoGenetico;
import algoritmogenetico.IAlgoritmo;
import algoritmogenetico.dominio.DominioAritmetico;
import algoritmogenetico.dominio.IDominio;
import algoritmogenetico.individuo.nodo.funciones.Funcion;
import algoritmogenetico.individuo.nodo.terminales.Terminal;
import excepciones.ArgsDistintosFuncionesException;

/**
 * Tests de integracion del algoritmo: crearPoblacion, crearNuevaPoblacion con
 * y sin mutacion, ejecutar una generacion (sin depender de fichero externo si
 * es posible).
 */
@DisplayName("Algoritmo genetico - integracion")
class AlgoritmoGeneticoIntegracionTest {

	private IDominio dominio;
	private int[] argumentos;
	private String[] nombresFunciones;

	@BeforeEach
	void setUp() throws ArgsDistintosFuncionesException {
		dominio = new DominioAritmetico();
		argumentos = new int[] { 2, 2, 2 };
		nombresFunciones = new String[] { "+", "-", "*" };
	}

	@Test
	@DisplayName("crearPoblacion con constructor sin mutacion no lanza")
	void crearPoblacion_sinMutacion_noLanza() throws ArgsDistintosFuncionesException {
		IAlgoritmo alg = new AlgoritmoGenetico(10, 5, 3, 50, 2);
		alg.defineConjuntoTerminales(dominio.definirConjuntoTerminales("x"));
		alg.defineConjuntoFunciones(dominio.definirConjuntoFunciones(argumentos, nombresFunciones));
		alg.crearPoblacion();
	}

	@Test
	@DisplayName("crearNuevaPoblacion tras crearPoblacion con mutacion 0 no lanza")
	void crearNuevaPoblacion_sinMutacion_noLanza() throws ArgsDistintosFuncionesException {
		IAlgoritmo alg = new AlgoritmoGenetico(10, 5, 3, 80, 2, 0.0, 1L);
		alg.defineConjuntoTerminales(dominio.definirConjuntoTerminales("x"));
		alg.defineConjuntoFunciones(dominio.definirConjuntoFunciones(argumentos, nombresFunciones));
		alg.crearPoblacion();
		alg.crearNuevaPoblacion();
	}

	@Test
	@DisplayName("crearNuevaPoblacion con probabilidad mutacion > 0 no lanza")
	void crearNuevaPoblacion_conMutacion_noLanza() throws ArgsDistintosFuncionesException {
		IAlgoritmo alg = new AlgoritmoGenetico(10, 5, 3, 80, 2, 0.2, 2L);
		alg.defineConjuntoTerminales(dominio.definirConjuntoTerminales("x"));
		alg.defineConjuntoFunciones(dominio.definirConjuntoFunciones(argumentos, nombresFunciones));
		alg.crearPoblacion();
		alg.crearNuevaPoblacion();
	}

	@Test
	@DisplayName("ejecutar una generacion con fichero temporal no lanza")
	void ejecutar_unaGeneracion_conFichero_noLanza(@TempDir Path tempDir) throws Exception {
		Path datos = tempDir.resolve("datos.txt");
		Files.writeString(datos, "0.0\t0.0\n1.0\t2.0\n");
		IAlgoritmo alg = new AlgoritmoGenetico(4, 1, 2, 80, 2, 0.1, 3L);
		alg.defineConjuntoTerminales(dominio.definirConjuntoTerminales("x"));
		alg.defineConjuntoFunciones(dominio.definirConjuntoFunciones(argumentos, nombresFunciones));
		dominio.definirValoresPrueba(datos.toString());
		alg.ejecutar(dominio);
	}

	@Test
	@DisplayName("poblacion de tamanio impar no causa bucle infinito en crearNuevaPoblacion")
	void crearNuevaPoblacion_tamanioImpar_noSeBloquea() throws ArgsDistintosFuncionesException {
		for (int tam : new int[]{7, 11, 13, 21, 51}) {
			AlgoritmoGenetico alg = new AlgoritmoGenetico(tam, 3, 3, 80, 2, 0.1, 99L);
			alg.defineConjuntoTerminales(dominio.definirConjuntoTerminales("x"));
			alg.defineConjuntoFunciones(dominio.definirConjuntoFunciones(argumentos, nombresFunciones));
			alg.crearPoblacion();
			assertDoesNotThrow(alg::crearNuevaPoblacion, "Bucle infinito con tamanioPoblacion=" + tam);
		}
	}

	@Test
	@DisplayName("ejecutar con funciones unarias (sin, cos, neg, exp) no crashea")
	void ejecutar_conFuncionesUnarias_noLanza(@TempDir Path tempDir) throws Exception {
		Path datos = tempDir.resolve("datos_unary.txt");
		Files.writeString(datos, "0.0\t0.0\n1.0\t0.841\n2.0\t0.909\n3.0\t0.141\n");
		DominioAritmetico dom = new DominioAritmetico();
		List<Terminal> terms = dom.definirConjuntoTerminalesConConstantes(
				new String[]{"x"}, new double[]{1.0, 2.0});
		List<Funcion> funcs = dom.definirConjuntoFunciones(
				new int[]{2, 2, 1, 1, 1, 1}, "+", "*", "sin", "cos", "neg", "exp");
		AlgoritmoGenetico alg = new AlgoritmoGenetico(30, 10, 4, 80, 3, 0.2, 42L);
		alg.defineConjuntoTerminales(terms);
		alg.defineConjuntoFunciones(funcs);
		dom.definirValoresPrueba(datos.toString());
		assertDoesNotThrow(() -> alg.ejecutar(dom));
	}

	@Test
	@DisplayName("ejecutar con solo funciones unarias no crashea")
	void ejecutar_soloFuncionesUnarias_noLanza(@TempDir Path tempDir) throws Exception {
		Path datos = tempDir.resolve("datos_solo_unary.txt");
		Files.writeString(datos, "0.5\t0.479\n1.0\t0.841\n1.5\t0.997\n");
		DominioAritmetico dom = new DominioAritmetico();
		List<Terminal> terms = dom.definirConjuntoTerminales("x");
		List<Funcion> funcs = dom.definirConjuntoFunciones(
				new int[]{1, 1, 1}, "sin", "neg", "abs");
		AlgoritmoGenetico alg = new AlgoritmoGenetico(20, 10, 3, 80, 3, 0.3, 77L);
		alg.defineConjuntoTerminales(terms);
		alg.defineConjuntoFunciones(funcs);
		dom.definirValoresPrueba(datos.toString());
		assertDoesNotThrow(() -> alg.ejecutar(dom));
	}
}
