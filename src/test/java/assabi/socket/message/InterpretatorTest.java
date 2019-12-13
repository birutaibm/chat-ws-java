package assabi.socket.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import assabi.dto.ParticipationDTO;
import assabi.dto.ParticipationDTO.InterpretationDTO;
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

	@Test
	public void desserializedDTOIsEqualsOriginal() {
		ParticipationDTO original = createDTO();
		
		try {
			String str = Interpretator.mapper.writeValueAsString(original);
			System.out.println(str);
			ParticipationDTO reconstructed = Interpretator.mapper.readValue(str, ParticipationDTO.class);
			assertEquals(original, reconstructed);
		} catch (IOException e) {
			e.printStackTrace();
			fail("throws IOException");
		}
	}

	private ParticipationDTO createDTO() {
		InterpretationDTO interpretation = new InterpretationDTO(2L, 3L);
		ParticipationDTO dto = new ParticipationDTO(1L, interpretation);
		return dto;
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
