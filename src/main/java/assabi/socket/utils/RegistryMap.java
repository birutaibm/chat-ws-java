package assabi.socket.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import assabi.socket.exceptions.RegistryConflict;

public final class RegistryMap<K, V> {
	private final Map<K, V> registry = new HashMap<>();

	public void registry(K key, V value) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(value);
		if (registry.containsKey(key) && !value.equals(registry.get(key)))
			new RegistryConflict(key, value, registry.get(key));
		registry.put(key, value);
	}

	public V get(K name) {
		Objects.requireNonNull(name);
		return Objects.requireNonNull(registry.get(name));
	}

}
