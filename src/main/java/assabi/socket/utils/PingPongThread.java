package assabi.socket.utils;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Supplier;

import org.java_websocket.WebSocket;

import assabi.socket.message.Interpretator;
import assabi.socket.message.Message;

public class PingPongThread {
	public class Ping implements Message {}

	private final Supplier<Collection<WebSocket>> connections;
	private final String ping;

	public PingPongThread(Supplier<Collection<WebSocket>> connections) {
		super();
		this.connections = connections;
		try {
			ping = Interpretator.write(new Ping());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		new Thread(infinityLoop(this::sendPings), "PingPong").start();
	}

	private Runnable infinityLoop(Runnable action) {
		return () -> {
			while (true) {
				action.run();
			}
		};
	}

	private void sendPings() {
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		connections.get().forEach(this::sendPing);
	}

	private void sendPing(WebSocket connection) {
		connection.send(ping);
	}

}
