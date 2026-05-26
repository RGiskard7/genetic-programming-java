package algoritmogenetico.operadores;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import algoritmogenetico.individuo.IIndividuo;

/**
 * Selección por ranking cuadrático: los individuos se ordenan por fitness y se
 * asignan pesos proporcionales al cuadrado de su posición (el mejor tiene el
 * peso más alto). Se muestrea con reemplazo hasta obtener dos índices distintos.
 */
public class SelectorRanking implements Selector {

	private final Random random;

	public SelectorRanking(Random random) {
		this.random = random;
	}

	@Override
	public List<IIndividuo> seleccionar(List<IIndividuo> poblacion) {
		List<IIndividuo> ordenada = new ArrayList<>(poblacion);
		ordenada.sort(CMP_FITNESS);

		double[] pesos = new double[ordenada.size()];
		double suma = 0;
		for (int i = 0; i < ordenada.size(); i++) {
			pesos[i] = (i + 1) * (i + 1);
			suma += pesos[i];
		}
		for (int i = 0; i < pesos.length; i++) pesos[i] /= suma;

		int idx1 = muestrear(pesos);
		int idx2 = muestrear(pesos);
		while (idx2 == idx1 && ordenada.size() > 1) idx2 = muestrear(pesos);

		List<IIndividuo> padres = new ArrayList<>();
		padres.add(ordenada.get(idx1));
		padres.add(ordenada.get(idx2));
		return padres;
	}

	private int muestrear(double[] prob) {
		double r = random.nextDouble();
		double acum = 0;
		for (int i = 0; i < prob.length; i++) {
			acum += prob[i];
			if (r < acum) return i;
		}
		return prob.length - 1;
	}
}
