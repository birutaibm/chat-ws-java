package assabi.socket.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import assabi.socket.message.Message.Login;

public class InterpretatorTest {
	@Test
	public void desserializedMessageIsEqualsOriginal() {
		Message original = createMessage();
		
		try {
			String strMessage = Interpretator.write(original);
			Message reconstructedMsg = Interpretator.read(strMessage);
			assertEquals(original, reconstructedMsg);
		} catch (IOException e) {
			fail("throws IOException");
		}
	}

	private Message createMessage() {
		String email = "birutaibm@gmail.com";
		String password = "1234";
		
		Login login = new Login();
		login.setLogin(email);
		login.setPassword(password);
		return login;
	}
}
