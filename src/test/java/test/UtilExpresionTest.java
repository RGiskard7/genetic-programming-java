package test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import algoritmogenetico.individuo.nodo.INodo;
import algoritmogenetico.individuo.nodo.terminales.TerminalAritmetico;
import algoritmogenetico.individuo.nodo.terminales.TerminalConstante;
import algoritmogenetico.util.UtilExpresion;

@DisplayName("UtilExpresion")
class UtilExpresionTest {

	@Test
	@DisplayName("isConstant con null devuelve true")
	void isConstant_null_returnsTrue() {
		assertTrue(UtilExpresion.isConstant(null));
	}

	@Test
	@DisplayName("isConstant con TerminalConstante devuelve true")
	void isConstant_terminalConstante_returnsTrue() {
		INodo c = new TerminalConstante(1.0);
		assertTrue(UtilExpresion.isConstant(c));
	}

	@Test
	@DisplayName("isConstant con TerminalAritmetico (variable) devuelve false")
	void isConstant_terminalAritmetico_returnsFalse() {
		INodo x = new TerminalAritmetico("x");
		assertFalse(UtilExpresion.isConstant(x));
	}
}
