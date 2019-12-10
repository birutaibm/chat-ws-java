package assabi.socket.message.processor;

import org.java_websocket.WebSocket;

import assabi.socket.SocketServer;
import assabi.socket.message.Message;
import assabi.socket.utils.RegistryMap;

public class ProcessorRegistry {
	private final SocketServer server;
	private final RegistryMap<Class<?>, Processor<?>> processors;
	public ProcessorRegistry(SocketServer server) {
		super();
		this.server = server;
		processors = new RegistryMap<>();
        registry(Message.ApproveWeights.class, new Processor.ApproveWeights());
        registry(Message.CreateApp.class, new Processor.CreateApp());
        registry(Message.GoAhead.class, new Processor.GoAhead());
        registry(Message.GroupChange.class, new Processor.GroupChange());
        registry(Message.Login.class, new Processor.Login());
        registry(Message.ParticipationApproval.class, new Processor.ParticipationApproval());
        registry(Message.ParticipationIntension.class, new Processor.ParticipationIntension());
        registry(Message.Waiting.class, new Processor.Waiting());
        registry(Message.Weights.class, new Processor.Weights());
	}
	public <T extends Message> void registry(Class<T> type, Processor<T> processor) {
		processors.registry(type, processor);
	}
	@SuppressWarnings("unchecked")
	public <T extends Message> void process(T message, WebSocket connection) {
		((Processor<T>) processors.get(message.getClass()))
			.process(message, connection, server);
	}
}