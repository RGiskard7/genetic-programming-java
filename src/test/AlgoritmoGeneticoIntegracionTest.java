package test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import algoritmogenetico.AlgoritmoGenetico;
import algoritmogenetico.IAlgoritmo;
import algoritmogenetico.dominio.DominioAritmetico;
import algoritmogenetico.dominio.IDominio;
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
}
