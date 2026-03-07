package test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import algoritmogenetico.individuo.IIndividuo;
import algoritmogenetico.individuo.Individuo;
import algoritmogenetico.individuo.nodo.INodo;
import excepciones.CruceNuloException;

public class PruebaCruce {

	public List<IIndividuo> cruce(IIndividuo progenitor1, IIndividuo progenitor2) throws CruceNuloException {
		int ptoCruce1;
		int ptoCruce2;
		Random aleatorio = new Random(System.currentTimeMillis());
		List<IIndividuo> descendientes = new ArrayList<>();

		ptoCruce1 = aleatorio.nextInt(progenitor1.getNumeroNodos()) + 1;
		ptoCruce2 = aleatorio.nextInt(progenitor2.getNumeroNodos()) + 1;

		System.out.println("\nPunto de cruce del progenitor 1: " + ptoCruce1);
		System.out.println("Punto de cruce del progenitor 2: " + ptoCruce2);

		if (ptoCruce1 == 1 && ptoCruce2 == 1) {
			throw new CruceNuloException();
		} else {
			int index;
			INodo raiz;
			INodo nodoSwap;
			INodo nodoAux;
			Individuo descendiente1 = new Individuo();
			Individuo descendiente2 = new Individuo();

			descendiente1.setExpresion(progenitor1.getExpresion().copy());
			descendiente2.setExpresion(progenitor2.getExpresion().copy());

			descendiente1.etiquetaNodos();
			descendiente2.etiquetaNodos();

			nodoSwap = descendiente1.getNodosEtiquetados().get(ptoCruce1);

			raiz = descendiente1.getPadre(nodoSwap);
			if (raiz != null) {
				index = raiz.getDescendientes().indexOf(nodoSwap);
				if (index == 0) {
					nodoAux = raiz.getDescendientes().get(1);
					raiz.getDescendientes().clear();
					raiz.incluirDescendiente(descendiente2.getNodosEtiquetados().get(ptoCruce2));
					raiz.incluirDescendiente(nodoAux);
				} else {
					nodoAux = raiz.getDescendientes().get(0);
					raiz.getDescendientes().clear();
					raiz.incluirDescendiente(nodoAux);
					raiz.incluirDescendiente(descendiente2.getNodosEtiquetados().get(ptoCruce2));
				}
			} else {
				descendiente1.setExpresion(descendiente2.getNodosEtiquetados().get(ptoCruce2));
			}

			raiz = descendiente2.getPadre(descendiente2.getNodosEtiquetados().get(ptoCruce2));
			if (raiz != null) {
				index = raiz.getDescendientes().indexOf(descendiente2.getNodosEtiquetados().get(ptoCruce2));
				if (index == 0) {
					nodoAux = raiz.getDescendientes().get(1);
					raiz.getDescendientes().clear();
					raiz.incluirDescendiente(nodoSwap);
					raiz.incluirDescendiente(nodoAux);
				} else {
					nodoAux = raiz.getDescendientes().get(0);
					raiz.getDescendientes().clear();
					raiz.incluirDescendiente(nodoAux);
					raiz.incluirDescendiente(nodoSwap);
				}
			} else {
				descendiente2.setExpresion(nodoSwap);
			}

			descendiente1.etiquetaNodos();
			descendiente2.etiquetaNodos();

			descendientes.add(descendiente1);
			descendientes.add(descendiente2);

			return descendientes;
		}
	}
}
