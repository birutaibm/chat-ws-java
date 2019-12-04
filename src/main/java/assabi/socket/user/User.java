package assabi.socket.user;

import org.java_websocket.WebSocket;

import com.fasterxml.jackson.core.JsonProcessingException;

import assabi.socket.message.Interpretor;
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
				text = Interpretor.write(content);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
    }

    public void waiting() {
		buffer.consume(connection);
	}
}
