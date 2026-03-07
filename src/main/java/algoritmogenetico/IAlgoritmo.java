package algoritmogenetico;

import java.util.List;

import algoritmogenetico.dominio.IDominio;
import algoritmogenetico.individuo.IIndividuo;
import algoritmogenetico.individuo.nodo.funciones.Funcion;
import algoritmogenetico.individuo.nodo.terminales.Terminal;
import excepciones.ArgsDistintosFuncionesException;
import excepciones.CruceNuloException;

/**
 * La interfaz IAlgoritmo define todos los requisitos basicos que debe tener un
 * algoritmo de programcion genetica.
 */
public interface IAlgoritmo {

	/**
	 * Permite establecer el conjunto de terminales que usara el algoritmo para
	 * resolver el problema.
	 *
	 * @param terminales el conjunto de terminales
	 */
	public void defineConjuntoTerminales(List<Terminal> terminales);

	/**
	 * Permite establecer el conjunto de funciones que usara el algoritmo para
	 * resolver el problema.
	 *
	 * @param funciones el conjunto de funciones
	 * @throws ArgsDistintosFuncionesException the args distintos funciones exception
	 */
	public void defineConjuntoFunciones(List<Funcion> funciones) throws ArgsDistintosFuncionesException;

	/**
	 * Permite crear una poblacion de individuos aleatorios.
	 */
	public void crearPoblacion();

	/**
	 * Permite realizar el cruce genetico entre dos individuos especificos para
	 * obtener dos individuos descendientes totalmente nuevos.
	 *
	 * @param prog1 el primer individuo progenitor
	 * @param prog2 el segundo individuo progenitor
	 * 
	 * @return la lista con los dos nuevos descendientes
	 * @throws CruceNuloException en caso de que se intenten cruzar las dos raizes de los progenitores
	 */
	public List<IIndividuo> cruce(IIndividuo prog1, IIndividuo prog2) throws CruceNuloException;

	/**
	 * Permite crea una nueva población a partir de una población anterior,
	 * aplicando el operador de cruce y el paso directo de individuos no cruzados o
	 * elitistas.
	 */
	public void crearNuevaPoblacion();

	/**
	 * Aplica el operador de mutacion al individuo: se elige un nodo al azar y se
	 * sustituye su subarbol por uno aleatorio de profundidad limitada. Devuelve
	 * un nuevo individuo mutado (el original no se modifica).
	 *
	 * @param individuo el individuo a mutar
	 * @return un nuevo individuo con la mutacion aplicada
	 */
	public IIndividuo mutar(IIndividuo individuo);

	/**
	 * Ejecutar el algoritmo genetico.
	 *
	 * @param dominio el dominio que se usara para evaluar a los individuos
	 */
	public void ejecutar(IDominio dominio);
}
