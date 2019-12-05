package assabi.socket.user;

import java.io.IOException;

import org.java_websocket.WebSocket;

import assabi.socket.message.Interpretator;
import assabi.socket.message.Message;
import lombok.Data;
import lombok.Getter;

@Data
public class User {
	private final WebSocket connection;
    private final Long userId;
    private final String name;
    private final Buffer buffer = new Buffer();
    public class Buffer {
    	@Getter
    	private Message.ParticipationApproved content;
    	private String text;
    	private WebSocket connection;
    	private boolean needSend;
    	
		public void consume(WebSocket connection) {
			if (needSend)
				connection.send(text);
			else
				this.connection = connection;
			needSend = false;
		}

		public void flush() {
			if (connection != null)
				connection.send(text);
			else
				needSend = true;
		}

		public void setContent(Message.ParticipationApproved content) {
			this.content = content;
			try {
				text = Interpretator.write(content);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }

    public void waiting() {
		buffer.consume(connection);
	}
}
