package assabi.socket.exceptions;

public class RegistryConflictException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final RegistryConflict<?, ?> data;

	RegistryConflictException(RegistryConflict<?, ?> data) {
		super();
		this.data = data;
	}
	
}
