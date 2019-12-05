package assabi.socket.message;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class Interpretator {
	public static final ObjectMapper mapper = new ObjectMapper();

	public static Message read(String message) throws JsonParseException, JsonMappingException, IOException {
		MessageWrapper instance = mapper.readValue(message, MessageWrapper.class);
		String strMsg = mapper.writeValueAsString(instance.getData());
		return mapper.readValue(strMsg, instance.getDataClass());
	}

	public static String write(Message message) throws IOException {
		String strMsg = mapper.writeValueAsString(message);
		Map<String, Object> mapMsg = mapper.readValue(strMsg, Map.class);
		MessageWrapper instance = new MessageWrapper(message.getClass(), mapMsg);
		return mapper.writeValueAsString(instance);
	}
}
