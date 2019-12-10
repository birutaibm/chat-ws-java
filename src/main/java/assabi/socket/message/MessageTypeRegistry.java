package assabi.socket.message;

import assabi.socket.utils.RegistryMap;

public final class MessageTypeRegistry {
	private static final MessageTypeRegistry instance = new MessageTypeRegistry();
	private final RegistryMap<String, Class<? extends Message>> registry;

	private MessageTypeRegistry() {
		super();
		registry = new RegistryMap<>();
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
		registry(Message.ParticipationIntension.class);
		registry(Message.Waiting.class);
		registry(Message.Weights.class);
	}

	public static MessageTypeRegistry getInstance() {
		return instance;
	}

	public void registry(String name, Class<? extends Message> type) {
		registry.registry(name, type);
	}

	public Class<? extends Message> get(String name) {
		return registry.get(name);
	}

	public void registry(Class<? extends Message> type) {
		registry(type.getSimpleName(), type);
	}
}
