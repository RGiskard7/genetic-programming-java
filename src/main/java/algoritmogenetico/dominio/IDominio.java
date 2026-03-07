package algoritmogenetico.dominio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import algoritmogenetico.individuo.IIndividuo;
import algoritmogenetico.individuo.nodo.funciones.Funcion;
import algoritmogenetico.individuo.nodo.terminales.Terminal;
import excepciones.ArgsDistintosFuncionesException;

/**
 * La interfaz IDominio reune todo lo necesario para que una clase sea capaz de
 * implementar distintas formas de evaluar los individuos que intervienen en un
 * problema.
 */
public interface IDominio {

	/**
	 * Permite definir el conjunto de terminales que intervienen en un tipo de
	 * problema concreto a resolver.
	 *
	 * @param terminales el simbolo de los terminales que intervienen en el problema
	 * @return la lista de los terminales creados
	 */
	public List<Terminal> definirConjuntoTerminales(String... terminales);

	/**
	 * Permite definir el conjunto de funciones que intervienen en un tipo de
	 * problema concreto a resolver.
	 *
	 * @param argumentos el numero de argumentos que tendran cada una de las funciones
	 * @param funciones el simbolo de las funciones que intervienen en el problema
	 * @return la lista de las funciones creadas
	 * @throws ArgsDistintosFuncionesException the args distintos funciones exception
	 */
	public List<Funcion> definirConjuntoFunciones(int[] argumentos, String... funciones)
			throws ArgsDistintosFuncionesException;

	/**
	 * Permite establecer un conjunto de datos de prueba para los calculos mediante
	 * un fichero .txt indicado por argumento.
	 *
	 * @param ficheroDatos la ruta del fichero donde se encuentran los datos de prueba
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void definirValoresPrueba(String ficheroDatos) throws FileNotFoundException, IOException;

	/**
	 * Calcula el fitness o aptitud que tiene un individuo determinado para resolver
	 * el problema establecido.
	 *
	 * @param individuo el individuo del que se quiere calcular el fitness
	 * @return el fitness del individuo
	 */
	public double calcularFitness(IIndividuo individuo);

	/**
	 * Devuelve el fitness idoneo para resolver correctamente el problema.
	 *
	 * @return el fitness necesario para resolver el problema
	 */
	public double fitnessBuscado();
}