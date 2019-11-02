package assabi.socket.message.processor;

import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;

import assabi.socket.SocketServer;
import assabi.socket.message.Message;

public class ProcessorRegistry {
	private final SocketServer server;
	private final Map<Class<?>, Processor<?>> processors;
	public ProcessorRegistry(SocketServer server) {
		super();
		this.server = server;
		processors = new HashMap<>();
	}
	public <T extends Message> void registry(Class<T> type, Processor<T> processor) {
		processors.put(type, processor);
	}
	@SuppressWarnings("unchecked")
	public <T extends Message> void process(T message, WebSocket connection) {
		((Processor<T>) processors.get(message.getClass()))
			.process(message, connection, server);
	}
}