package assabi.socket.user;

import org.java_websocket.WebSocket;

import lombok.Data;

@Data
public class User {
	private final WebSocket connection;
    private final Long userId;
    private final String name;
}
