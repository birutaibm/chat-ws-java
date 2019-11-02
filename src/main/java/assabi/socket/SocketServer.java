package assabi.socket;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.jasofalcon.chat.ChatServer;

import assabi.socket.message.Message;
import assabi.socket.message.MessageWrapper;
import assabi.socket.message.processor.Processor;
import assabi.socket.message.processor.ProcessorRegistry;
import assabi.socket.user.User;
import assabi.socket.user.UserList;


public final class SocketServer extends WebSocketServer {
    private final static Logger logger = LogManager.getLogger(ChatServer.class);
    private final UserList users;
    private final Set<WebSocket> conns;
    private final ProcessorRegistry processors;

    private SocketServer(int port) {
        super(new InetSocketAddress(port));
        conns = new HashSet<>();
        users = new UserList();
        processors = new ProcessorRegistry(this);
        processors.registry(Message.Logged.class, new Processor.Logged());
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        conns.add(webSocket);

        logger.info("Connection established from: " + webSocket.getRemoteSocketAddress().getHostString());
        System.out.println("New connection from " + webSocket.getRemoteSocketAddress().getAddress().getHostAddress());
    }
    
    @Override
    public void onError(WebSocket conn, Exception ex) {
    	if (conn != null) {
    		conn.close();
    	}
    	assert conn != null;
    	System.out.println("ERROR from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        conns.remove(conn);
        users.remove(conn);

        logger.info("Connection closed to: " + conn.getRemoteSocketAddress().getHostString());
        System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
        	processors.process(MessageWrapper.read(message), conn);
        } catch (Exception e) {
            logger.error("Wrong message format.");
            // return error message to user
        }
    }

	public void addUser(User user) {
		users.add(user);
	}

	public static void main(String[] args) {
        int port;
        try {
            port = Integer.parseInt(System.getenv("PORT"));
        } catch (NumberFormatException nfe) {
            port = 9000;
        }
        new SocketServer(port).start();
	}
}
