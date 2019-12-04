package assabi.socket.message;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import assabi.socket.message.Message.Login;

public final class Interpretor {
	public static final ObjectMapper mapper = new ObjectMapper();

	public static Message read(String message) throws JsonParseException, JsonMappingException, IOException {
		MessageWrapper instance = mapper.readValue(message, MessageWrapper.class);
		return mapper.readValue(instance.getData(), instance.getDataClass());
	}

	public static String write(Message message) throws JsonProcessingException {
		String data = mapper.writeValueAsString(message);
		MessageWrapper instance = new MessageWrapper(message.getClass(), data);
		return mapper.writeValueAsString(instance);
	}

	public static void main(String[] args) {
		String email = "birutaibm@gmail.com";
		String password = "1234";
		
		Login login = new Login();
		login.setLogin(email);
		login.setPassword(password);
		
		try {
			String strMessage = write(login);
			System.out.println(strMessage);
			Message reconstructedMsg = read(strMessage);
			if (login.equals(reconstructedMsg)) {
				System.out.println(login + " equals " + reconstructedMsg);
			} else {
				System.err.println(login + " not equals " + reconstructedMsg);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
