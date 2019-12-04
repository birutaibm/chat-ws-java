package assabi.socket.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageWrapper {
	private static final String classPath = Message.class.getCanonicalName() + "$";
	@Getter
	@Setter(AccessLevel.PRIVATE)
	@JsonIgnore
	private Class<? extends Message> dataClass;
	@Getter
	@Setter(AccessLevel.PRIVATE)
	private String data;
	
	public MessageWrapper(Class<? extends Message> dataClass, String data) {
		super();
		this.dataClass = dataClass;
		this.data = data;
	}

	private void setDataType(String dataType) {
		try {
			String className = classPath + dataType;
			setDataClass((Class<? extends Message>) Class.forName(className));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getDataType() {
		return dataClass.getSimpleName();
	}
}
