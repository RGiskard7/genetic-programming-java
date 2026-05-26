package algoritmogenetico.operadores;

import java.util.Comparator;
import java.util.List;

import algoritmogenetico.individuo.IIndividuo;

/**
 * Estrategia de selección de padres para el algoritmo genético.
 * Devuelve exactamente dos individuos elegidos de la población.
 */
public interface Selector {

	/** Mayor fitness primero; si empatan, menor número de nodos primero (parsimonia). */
	Comparator<IIndividuo> CMP_FITNESS =
			Comparator.comparingDouble(IIndividuo::getFitness)
					.thenComparing(Comparator.comparingInt(IIndividuo::getNumeroNodos).reversed());

	/**
	 * Selecciona dos padres de la población.
	 *
	 * @param poblacion la población actual (no se modifica)
	 * @return lista con exactamente dos individuos seleccionados
	 */
	List<IIndividuo> seleccionar(List<IIndividuo> poblacion);
}
