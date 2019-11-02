package assabi.socket.message;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor
public class MessageWrapper {
	private static final ObjectMapper mapper = new ObjectMapper();
	private final Class<? extends Message> dataType;
	private final String data;

	public static Message read(String message) throws JsonParseException, JsonMappingException, IOException {
		MessageWrapper instance = mapper.readValue(message, MessageWrapper.class);
		return mapper.readValue(instance.data, instance.dataType);
	}
}
