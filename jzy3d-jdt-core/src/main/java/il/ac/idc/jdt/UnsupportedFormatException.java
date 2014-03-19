package il.ac.idc.jdt;

@SuppressWarnings("serial")
public class UnsupportedFormatException extends RuntimeException {

	public UnsupportedFormatException() {
		super();
	}

	public UnsupportedFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedFormatException(String message) {
		super(message);
	}

	public UnsupportedFormatException(Throwable cause) {
		super(cause);
	}

}
