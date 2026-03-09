package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import algoritmogenetico.individuo.nodo.INodo;
import algoritmogenetico.individuo.nodo.funciones.FuncionMultiplicacion;
import algoritmogenetico.individuo.nodo.funciones.FuncionSuma;
import algoritmogenetico.individuo.nodo.terminales.TerminalAritmetico;
import algoritmogenetico.individuo.nodo.terminales.TerminalConstante;
import algoritmogenetico.util.SimplificadorExpresion;

/**
 * Tests para SimplificadorExpresion: reglas x+0→x, x*1→x, 0*x→0 y que el arbol original no se muta.
 */
public class SimplificadorExpresionTest {

	@Test
	public void simplificar_null_devuelveNull() {
		assertNull(SimplificadorExpresion.simplificar(null));
	}

	@Test
	public void sumaConCeroDerecha_devuelveOtroHijo() {
		INodo x = new TerminalAritmetico("x");
		INodo cero = new TerminalConstante(0);
		FuncionSuma mas = new FuncionSuma("+", 2);
		mas.incluirDescendiente(x);
		mas.incluirDescendiente(cero);
		INodo res = SimplificadorExpresion.simplificar(mas);
		assertTrue(res instanceof TerminalAritmetico);
		assertEquals("x", res.getSimbolo());
	}

	@Test
	public void sumaConCeroIzquierda_devuelveOtroHijo() {
		INodo cero = new TerminalConstante(0);
		INodo x = new TerminalAritmetico("x");
		FuncionSuma mas = new FuncionSuma("+", 2);
		mas.incluirDescendiente(cero);
		mas.incluirDescendiente(x);
		INodo res = SimplificadorExpresion.simplificar(mas);
		assertTrue(res instanceof TerminalAritmetico);
		assertEquals("x", res.getSimbolo());
	}

	@Test
	public void multiplicacionPorUno_devuelveOtroHijo() {
		INodo x = new TerminalAritmetico("x");
		INodo uno = new TerminalConstante(1);
		FuncionMultiplicacion mul = new FuncionMultiplicacion("*", 2);
		mul.incluirDescendiente(x);
		mul.incluirDescendiente(uno);
		INodo res = SimplificadorExpresion.simplificar(mul);
		assertTrue(res instanceof TerminalAritmetico);
		assertEquals("x", res.getSimbolo());
	}

	@Test
	public void multiplicacionPorCero_devuelveConstanteCero() {
		INodo cero = new TerminalConstante(0);
		INodo x = new TerminalAritmetico("x");
		FuncionMultiplicacion mul = new FuncionMultiplicacion("*", 2);
		mul.incluirDescendiente(cero);
		mul.incluirDescendiente(x);
		INodo res = SimplificadorExpresion.simplificar(mul);
		assertTrue(res instanceof TerminalConstante);
		assertEquals(0.0, ((TerminalConstante) res).getValor(), 1e-12);
	}

	@Test
	public void arbolOriginalNoSeModifica() {
		INodo x = new TerminalAritmetico("x");
		INodo cero = new TerminalConstante(0);
		FuncionSuma mas = new FuncionSuma("+", 2);
		mas.incluirDescendiente(x);
		mas.incluirDescendiente(cero);
		INodo res = SimplificadorExpresion.simplificar(mas);
		assertNotSame(mas, res);
		assertEquals(2, mas.getDescendientes().size());
		assertTrue(mas.getDescendientes().get(0) instanceof TerminalAritmetico);
		assertTrue(mas.getDescendientes().get(1) instanceof TerminalConstante);
	}
}
