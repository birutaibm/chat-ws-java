package assabi.socket.message;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import assabi.socket.exceptions.RegistryConflict;
import assabi.socket.exceptions.RegistryConflictException;

public final class MessageTypeRegistry {
	private static final MessageTypeRegistry instance = new MessageTypeRegistry();
	private final Map<String, Class<? extends Message>> registry;

	private MessageTypeRegistry() {
		super();
		registry = new HashMap<>();
		registry(Message.AppInfo.class);
		registry(Message.ApproveWeights.class);
		registry(Message.CreateApp.class);
		registry(Message.Distances.class);
		registry(Message.GoAhead.class);
		registry(Message.GroupChange.class);
		registry(Message.Login.class);
		registry(Message.NewActor.class);
		registry(Message.ParticipationApproval.class);
		registry(Message.ParticipationApproved.class);
		registry(Message.ParticipationIntesion.class);
		registry(Message.Waiting.class);
		registry(Message.Weights.class);
	}

	public static MessageTypeRegistry getInstance() {
		return instance;
	}

	public void registry(String name, Class<? extends Message> type) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(type);
		if (registry.containsKey(name) && !type.equals(registry.get(name)))
			new RegistryConflict(name, type, registry.get(name));
		registry.put(name, type);
	}

	public Class<? extends Message> get(String name) {
		Objects.requireNonNull(name);
		return Objects.requireNonNull(registry.get(name));
	}

	public void registry(Class<? extends Message> type) throws RegistryConflictException {
		registry(type.getSimpleName(), type);
	}
}
