package assabi.socket.message;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor
public class MessageWrapper {
	public static final ObjectMapper mapper = new ObjectMapper();
	private final Class<? extends Message> dataType;
	private final String data;

	public static Message read(String message) throws JsonParseException, JsonMappingException, IOException {
		MessageWrapper instance = mapper.readValue(message, MessageWrapper.class);
		return mapper.readValue(instance.data, instance.dataType);
	}

	public static String write(Message message) throws JsonProcessingException {
		String data = mapper.writeValueAsString(message);
		MessageWrapper instance = new MessageWrapper(message.getClass(), data);
		return mapper.writeValueAsString(instance);
	}
}
