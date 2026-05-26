package algoritmogenetico.operadores;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import algoritmogenetico.individuo.IIndividuo;

/**
 * Selección por torneo: elige {@code valorTorneo} individuos al azar sin
 * reemplazo y devuelve los dos con mejor fitness.
 */
public class SelectorTorneo implements Selector {

	private final int valorTorneo;
	private final Random random;

	public SelectorTorneo(int valorTorneo, Random random) {
		this.valorTorneo = valorTorneo;
		this.random = random;
	}

	@Override
	public List<IIndividuo> seleccionar(List<IIndividuo> poblacion) {
		List<IIndividuo> candidatos = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		int j = 0;
		while (j < valorTorneo) {
			int n = random.nextInt(poblacion.size());
			if (!indices.contains(n)) {
				indices.add(n);
				candidatos.add(poblacion.get(n));
				j++;
			}
		}
		candidatos.sort(CMP_FITNESS);
		List<IIndividuo> ganadores = new ArrayList<>();
		ganadores.add(candidatos.get(candidatos.size() - 1));
		ganadores.add(candidatos.get(candidatos.size() - 2));
		return ganadores;
	}
}
