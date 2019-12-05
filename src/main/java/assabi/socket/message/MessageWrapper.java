package assabi.socket.message;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageWrapper {
	private static final MessageTypeRegistry knownTypes = MessageTypeRegistry.getInstance();
	@Getter
	@Setter(AccessLevel.PRIVATE)
	@JsonIgnore
	private Class<? extends Message> dataClass;
	@Getter
	@Setter(AccessLevel.PRIVATE)
	private Map<String, Object> data;
	
	public MessageWrapper(Class<? extends Message> dataClass, Map<String, Object> data) {
		super();
		knownTypes.registry(dataClass);
		this.dataClass = dataClass;
		this.data = data;
	}

	@SuppressWarnings("unused") // used by jackson
	private void setType(String dataType) {
		System.out.println("Search for "+dataType+" in "+knownTypes);
		setDataClass(knownTypes.get(dataType));
	}

	public String getType() {
		return dataClass.getSimpleName();
	}
}
