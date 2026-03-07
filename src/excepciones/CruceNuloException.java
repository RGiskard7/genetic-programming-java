package excepciones;

/**
 * La clase CruceNuloException.
 */
public class CruceNuloException extends Exception {
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#toString()
	 */
	@Override
	public String toString() {
		return "No se ha podido realizar el cruce.\n";
	}
}
