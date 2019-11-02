package assabi.socket.message.processor;

import org.java_websocket.WebSocket;

import assabi.socket.SocketServer;
import assabi.socket.message.Message;
import assabi.socket.user.User;

public interface Processor<M extends Message> {
	void process(M message, WebSocket connection, SocketServer server);
	
	public final class Logged implements Processor<Message.Logged> {
		@Override
		public void process(Message.Logged message, WebSocket connection, SocketServer server) {
			server.addUser(new User(connection, message.getUserId(), message.getName()));
		}
	}
	
	public final class CreateApp implements Processor<Message.CreateApp> {
		@Override
		public void process(Message.CreateApp message, WebSocket connection, SocketServer server) {
			// FIXME implement this
			throw new RuntimeException("Not yet impemented");
		}
	}
	
	public final class ParticipationIntesion implements Processor<Message.ParticipationIntesion> {
		@Override
		public void process(Message.ParticipationIntesion message, WebSocket connection, SocketServer server) {
			// FIXME implement this
			throw new RuntimeException("Not yet impemented");
		}
	}
	
	public final class ParticipationApproval implements Processor<Message.ParticipationApproval> {
		@Override
		public void process(Message.ParticipationApproval message, WebSocket connection, SocketServer server) {
			// FIXME implement this
			throw new RuntimeException("Not yet impemented");
		}
	}
	
	public final class Weights implements Processor<Message.Weights> {
		@Override
		public void process(Message.Weights message, WebSocket connection, SocketServer server) {
			// FIXME implement this
			throw new RuntimeException("Not yet impemented");
		}
	}
	
	public final class ApproveWeights implements Processor<Message.ApproveWeights> {
		@Override
		public void process(Message.ApproveWeights message, WebSocket connection, SocketServer server) {
			// FIXME implement this
			throw new RuntimeException("Not yet impemented");
		}
	}
	
	public final class Waiting implements Processor<Message.Waiting> {
		@Override
		public void process(Message.Waiting message, WebSocket connection, SocketServer server) {
			// FIXME implement this
			throw new RuntimeException("Not yet impemented");
		}
	}
	
	public final class GoAhead implements Processor<Message.GoAhead> {
		@Override
		public void process(Message.GoAhead message, WebSocket connection, SocketServer server) {
			// FIXME implement this
			throw new RuntimeException("Not yet impemented");
		}
	}
	
	public final class GroupChange implements Processor<Message.GroupChange> {
		@Override
		public void process(Message.GroupChange message, WebSocket connection, SocketServer server) {
			// FIXME implement this
			throw new RuntimeException("Not yet impemented");
		}
	}
	
}