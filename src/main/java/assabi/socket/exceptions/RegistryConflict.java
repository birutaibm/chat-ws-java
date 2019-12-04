package assabi.socket.exceptions;

public class RegistryConflict<K, V> {
	private final K key;
	private final V oldValue;
	private final V newValue;
	
	public RegistryConflict(K key, V oldValue, V newValue) {
		super();
		this.key = key;
		this.oldValue = oldValue;
		this.newValue = newValue;
		throw new RegistryConflictException(this);
	}
}
