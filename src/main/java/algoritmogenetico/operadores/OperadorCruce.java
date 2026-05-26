package algoritmogenetico.operadores;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import algoritmogenetico.individuo.IIndividuo;
import algoritmogenetico.individuo.Individuo;
import algoritmogenetico.individuo.nodo.INodo;
import excepciones.CruceNuloException;

/**
 * Operador de cruce por subárbol: intercambia subárboles aleatorios entre dos
 * progenitores. Si los descendientes superan los límites de profundidad o nodos,
 * devuelve copias de los progenitores originales.
 */
public class OperadorCruce {

	private final int maxProfundidad;
	private int maxNodos = 0;
	private final Random random;

	public OperadorCruce(int maxProfundidad, Random random) {
		this.maxProfundidad = maxProfundidad;
		this.random = random;
	}

	/** Límite de nodos por descendiente (0 = sin límite). */
	public void setMaxNodos(int max) {
		this.maxNodos = Math.max(0, max);
	}

	/**
	 * Cruza dos progenitores intercambiando subárboles aleatorios.
	 *
	 * @throws CruceNuloException si ambos puntos de cruce son la raíz
	 */
	public List<IIndividuo> cruzar(IIndividuo prog1, IIndividuo prog2) throws CruceNuloException {
		int ptoCruce1 = random.nextInt(prog1.getNumeroNodos()) + 1;
		int ptoCruce2 = random.nextInt(prog2.getNumeroNodos()) + 1;

		if (ptoCruce1 == 1 && ptoCruce2 == 1) {
			throw new CruceNuloException();
		}

		Individuo descendiente1 = new Individuo();
		Individuo descendiente2 = new Individuo();
		descendiente1.setExpresion(prog1.getExpresion().copy());
		descendiente2.setExpresion(prog2.getExpresion().copy());
		descendiente1.etiquetaNodos();
		descendiente2.etiquetaNodos();

		INodo nodo1 = descendiente1.getNodosEtiquetados().get(ptoCruce1);
		INodo nodo2 = descendiente2.getNodosEtiquetados().get(ptoCruce2);
		INodo padre1 = descendiente1.getPadre(nodo1);
		INodo padre2 = descendiente2.getPadre(nodo2);

		if (padre1 != null) {
			List<INodo> hijos = padre1.getDescendientes();
			hijos.set(hijos.indexOf(nodo1), nodo2);
		} else {
			descendiente1.setExpresion(nodo2);
		}
		if (padre2 != null) {
			List<INodo> hijos = padre2.getDescendientes();
			hijos.set(hijos.indexOf(nodo2), nodo1);
		} else {
			descendiente2.setExpresion(nodo1);
		}

		descendiente1.etiquetaNodos();
		descendiente2.etiquetaNodos();

		if (descendiente1.getProfundidad() > maxProfundidad || descendiente2.getProfundidad() > maxProfundidad) {
			return fallback(prog1, prog2);
		}
		if (maxNodos > 0 && (descendiente1.getNumeroNodos() > maxNodos || descendiente2.getNumeroNodos() > maxNodos)) {
			return fallback(prog1, prog2);
		}

		List<IIndividuo> resultado = new ArrayList<>();
		resultado.add(descendiente1);
		resultado.add(descendiente2);
		return resultado;
	}

	private List<IIndividuo> fallback(IIndividuo p1, IIndividuo p2) {
		Individuo c1 = new Individuo();
		c1.setExpresion(p1.getExpresion().copy());
		c1.setFitness(p1.getFitness());
		Individuo c2 = new Individuo();
		c2.setExpresion(p2.getExpresion().copy());
		c2.setFitness(p2.getFitness());
		List<IIndividuo> lista = new ArrayList<>();
		lista.add(c1);
		lista.add(c2);
		return lista;
	}
}
