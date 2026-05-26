package algoritmogenetico.util;

import algoritmogenetico.individuo.IIndividuo;

/**
 * Resultado de una ejecución del algoritmo genético: mejor individuo al terminar,
 * generación en que finalizó y si se alcanzó el objetivo de fitness.
 */
public class ResultadoEjecucion {

	private final IIndividuo mejorIndividuo;
	private final int generacionFinal;
	private final boolean objetivoAlcanzado;

	public ResultadoEjecucion(IIndividuo mejorIndividuo, int generacionFinal, boolean objetivoAlcanzado) {
		this.mejorIndividuo = mejorIndividuo;
		this.generacionFinal = generacionFinal;
		this.objetivoAlcanzado = objetivoAlcanzado;
	}

	public IIndividuo getMejorIndividuo() {
		return mejorIndividuo;
	}

	public int getGeneracionFinal() {
		return generacionFinal;
	}

	public boolean isObjetivoAlcanzado() {
		return objetivoAlcanzado;
	}

	/**
	 * Indica si el mejor individuo de esta ejecución presentó singularidades
	 * numéricas durante su última evaluación (NaN, Infinity o valores extremos).
	 *
	 * @return true si hubo singularidades, false si el individuo es estable o no hay resultado
	 */
	public boolean haySingularidades() {
		return mejorIndividuo != null && mejorIndividuo.tieneSingularidades();
	}
}
