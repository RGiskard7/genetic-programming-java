package excepciones;

/**
 * La clase ArgsDistintosFuncionesException.
 */
public class ArgsDistintosFuncionesException extends Exception {
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#toString()
	 */
	@Override
	public String toString() {
		return "El numero de funciones y el de argumentos por funcion no coinciden.\n";
	}

}
