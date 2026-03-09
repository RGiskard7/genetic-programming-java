package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import algoritmogenetico.individuo.nodo.INodo;
import algoritmogenetico.individuo.nodo.funciones.FuncionMultiplicacion;
import algoritmogenetico.individuo.nodo.terminales.TerminalAritmetico;
import algoritmogenetico.individuo.nodo.terminales.TerminalConstante;
import algoritmogenetico.util.ExportadorExpresion;

/**
 * Tests para ExportadorExpresion: toPrefija, toLatex, toPython con arboles conocidos.
 */
class ExportadorExpresionTest {

	private static INodo arbolXporX() {
		INodo x = new TerminalAritmetico("x");
		FuncionMultiplicacion mul = new FuncionMultiplicacion("*", 2);
		mul.incluirDescendiente(x);
		mul.incluirDescendiente(x.copy());
		return mul;
	}

	@Test
	void toPrefija_equivaleAtoString() {
		INodo arbol = arbolXporX();
		String prefija = ExportadorExpresion.toPrefija(arbol);
		assertNotNull(prefija);
		assertTrue(prefija.contains("*"));
		assertTrue(prefija.contains("x"));
		assertEquals(arbol.toString(), prefija);
	}

	@Test
	void toLatex_arbolXporX_formatoRazonable() {
		INodo arbol = arbolXporX();
		String latex = ExportadorExpresion.toLatex(arbol);
		assertNotNull(latex);
		assertTrue(latex.contains("\\cdot") || latex.contains("*"));
		assertTrue(latex.contains("x"));
	}

	@Test
	void toPython_arbolXporX_formatoRazonable() {
		INodo arbol = arbolXporX();
		String py = ExportadorExpresion.toPython(arbol);
		assertNotNull(py);
		assertTrue(py.contains("x"));
		assertTrue(py.contains("*"));
		// Debe ser una expresion valida tipo (x * x)
		assertTrue(py.startsWith("(") && py.endsWith(")"));
	}

	@Test
	void toPythonDef_contieneDefYReturn() {
		INodo arbol = arbolXporX();
		String def = ExportadorExpresion.toPythonDef(arbol);
		assertNotNull(def);
		assertTrue(def.contains("def f("));
		assertTrue(def.contains("return"));
		assertTrue(def.contains("x"));
	}

	@Test
	void toPrefija_null_devuelveCadenaVacia() {
		assertEquals("", ExportadorExpresion.toPrefija(null));
	}

	@Test
	void toLatex_null_devuelveCadenaVacia() {
		assertEquals("", ExportadorExpresion.toLatex(null));
	}

	@Test
	void toPython_null_devuelveCadenaVacia() {
		assertEquals("", ExportadorExpresion.toPython(null));
	}
}
