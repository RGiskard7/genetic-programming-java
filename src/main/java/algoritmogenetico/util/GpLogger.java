package algoritmogenetico.util;

/**
 * Logger minimalista para el algoritmo genético.
 *
 * <ul>
 *   <li>{@link Nivel#SILENT} — sin salida (por defecto)</li>
 *   <li>{@link Nivel#INFO}   — progreso por generación y resultado final</li>
 *   <li>{@link Nivel#DEBUG}  — todo lo anterior más detalles adicionales</li>
 * </ul>
 *
 * Uso: {@code GpLogger.setNivel(GpLogger.Nivel.INFO);}
 */
public final class GpLogger {

	public enum Nivel { SILENT, INFO, DEBUG }

	private static Nivel nivel = Nivel.SILENT;

	private GpLogger() {}

	public static void setNivel(Nivel n) {
		nivel = n;
	}

	public static Nivel getNivel() {
		return nivel;
	}

	/** Mensaje visible en INFO y DEBUG. */
	public static void info(String msg) {
		if (nivel.ordinal() >= Nivel.INFO.ordinal())
			System.out.println(msg);
	}

	/** Mensaje visible solo en DEBUG. */
	public static void debug(String msg) {
		if (nivel == Nivel.DEBUG)
			System.out.println(msg);
	}

	/** Aviso de error: visible en INFO y DEBUG (nunca en SILENT). */
	public static void warn(String msg) {
		if (nivel != Nivel.SILENT)
			System.err.println(msg);
	}
}
