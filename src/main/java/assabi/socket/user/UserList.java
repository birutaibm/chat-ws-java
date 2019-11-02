package assabi.socket.user;

import java.util.HashMap;
import java.util.Optional;

import org.java_websocket.WebSocket;

public class UserList {
	private final HashMap<Long, User> fromId;
	private final HashMap<WebSocket, User> fromConnection;

	public UserList() {
		fromId = new HashMap<>();
		fromConnection = new HashMap<>();
	}

	public Optional<User> getFromId(Long id) {
		return Optional.ofNullable(fromId.get(id));
	}

	public Optional<User> getFromConnection(WebSocket connection) {
		return Optional.ofNullable(fromConnection.get(connection));
	}

	public void add(User user) {
		fromId.put(user.getUserId(), user);
		fromConnection.put(user.getConnection(), user);
	}

	public User remove(WebSocket conn) {
		User user = fromConnection.remove(conn);
		fromId.remove(user.getUserId());
		return user;
	}
}
