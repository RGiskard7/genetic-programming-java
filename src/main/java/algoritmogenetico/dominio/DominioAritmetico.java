package algoritmogenetico.dominio;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import algoritmogenetico.individuo.IIndividuo;
import algoritmogenetico.individuo.nodo.INodo;
import algoritmogenetico.individuo.nodo.funciones.Funcion;
import algoritmogenetico.individuo.nodo.funciones.FuncionMultiplicacion;
import algoritmogenetico.individuo.nodo.funciones.FuncionResta;
import algoritmogenetico.individuo.nodo.funciones.FuncionSuma;
import algoritmogenetico.individuo.nodo.terminales.Terminal;
import algoritmogenetico.individuo.nodo.terminales.TerminalAritmetico;
import excepciones.ArgsDistintosFuncionesException;

/**
 * Dominio para regresión simbólica: evalúa individuos (árboles de expresiones)
 * sobre pares (x, y) leídos de un fichero; fitness = número de puntos donde
 * el error cuadrático no supera un umbral. Implementa {@link IDominio}.
 */
public class DominioAritmetico implements IDominio {
	private Map<Double, Double> valoresPrueba;
	private double fitnessBuscado;

	/**
	 * Permite instanciar objetos de tipo DominioAritmetico.
	 */
	public DominioAritmetico() {
		valoresPrueba = new LinkedHashMap<>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.dominio.IDominio#definirConjuntoTerminales(java.lang.String[])
	 */
	@Override
	public List<Terminal> definirConjuntoTerminales(String... terminales) {
		List<Terminal> conjunto = new ArrayList<>();
		for (int i = 0; i < terminales.length; i++)
			conjunto.add(new TerminalAritmetico(terminales[i]));
		return conjunto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.dominio.IDominio#definirConjuntoFunciones(int[], java.lang.String[])
	 */
	@Override
	public List<Funcion> definirConjuntoFunciones(int[] argumentos, String... funciones)
			throws ArgsDistintosFuncionesException {
		if (argumentos.length == funciones.length) {
			List<Funcion> conjunto = new ArrayList<>();
			for (int i = 0; i < funciones.length; i++) {
				if ("+".equals(funciones[i])) {
					conjunto.add(new FuncionSuma(funciones[i], argumentos[i]));
				} else if ("*".equals(funciones[i])) {
					conjunto.add(new FuncionMultiplicacion(funciones[i], argumentos[i]));
				} else if ("-".equals(funciones[i])) {
					conjunto.add(new FuncionResta(funciones[i], argumentos[i]));
				} else {
					return null;
				}
			}
			return conjunto;
		} else {
			throw new ArgsDistintosFuncionesException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * algoritmogenetico.dominio.IDominio#definirValoresPrueba(java.lang.String)
	 */
	@Override
	public void definirValoresPrueba(String ficheroDatos) throws FileNotFoundException, IOException {
		try (BufferedReader buffer = new BufferedReader(new FileReader(ficheroDatos))) {
			String linea;
			String[] cadena;
			while ((linea = buffer.readLine()) != null) {
				cadena = linea.split("\t");
				valoresPrueba.put(Double.parseDouble(cadena[0]), Double.parseDouble(cadena[1]));
				fitnessBuscado++;
			}
		}
	}

	/**
	 * Permite establecer el valor numerico a todos los TerminalesAritmeticos del
	 * arbol introducido por parametro.
	 *
	 * @param nodo la raiz del arbol
	 * @param valor el valor que se le quiere dar a todos los TerminalesAritmeticos
	 */
	private void setValorTerminales(INodo nodo, double valor) {
		if (nodo instanceof TerminalAritmetico) {
			((TerminalAritmetico) nodo).setValor(valor);
		} else {
			for (INodo n : nodo.getDescendientes())
				setValorTerminales(n, valor);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.dominio.IDominio#calcularFitness(algoritmogenetico.individuo.IIndividuo)
	 */
	@Override
	public double calcularFitness(IIndividuo individuo) {
		double fitness = 0.0;
		if (!valoresPrueba.isEmpty()) {
			for (Map.Entry<Double, Double> entry : valoresPrueba.entrySet()) {
				setValorTerminales(individuo.getExpresion(), entry.getKey());
				double valorEstimado = individuo.calcularExpresion();
				double valorReal = entry.getValue();
				if (Math.pow(valorEstimado - valorReal, 2) <= 1)
					fitness++;
				/*
				 * System.out.println("Valor: " + entry.getKey() + " <-> Rdo estimado:" +
				 * valorEstimado + " <-> Rdo real: " + valorReal);
				 */
			}
			individuo.setFitness(fitness);
		}
		return fitness;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see algoritmogenetico.dominio.IDominio#fitnessBuscado()
	 */
	@Override
	public double fitnessBuscado() {
		return fitnessBuscado;
	}
}
