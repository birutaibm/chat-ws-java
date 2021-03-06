package assabi.socket.message;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

import assabi.dto.CharacterDTO;
import assabi.dto.DistanceOptionDTO;
import assabi.dto.ParticipationDTO.InterpretationDTO;
import assabi.dto.ScenarioDTO;
import assabi.dto.WeightCreationDTO;
import assabi.dto.ExistingApplicationDTO;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public interface Message {
	default String toJsonString() {
		try {
			return Interpretator.mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
	@Data
	public class Login implements Message {
		private String login;
		private String password;
	}
	@Getter @Setter @RequiredArgsConstructor @ToString
	public class CreateApp implements Message {
		private Long admin;
		private Long scenario;
		private String name;
		private Map<String, Integer> phases;
	}
	public class AppInfo extends ExistingApplicationDTO implements Message {
	}
	@Getter @Setter @RequiredArgsConstructor @ToString
	public class NewActor implements Message {
		private Long actorId;
		private String actorName;
	}
	@Getter @Setter @RequiredArgsConstructor @ToString
	public class ParticipationIntension implements Message {
		private Long application;
		private Long groupId;
		private String groupName;
		private Long characterId;
		private String characterName;
		private Long actorId;
		private String actorName;
	}
	@Getter @Setter @RequiredArgsConstructor @ToString
	public class ParticipationApproval implements Message {
		private List<Message.ParticipationIntension> approve;
	}
	@Getter @Setter @RequiredArgsConstructor @ToString
	public class ParticipationApproved implements Message {
		private Long groupId;
		private String groupName;
		private Long id;
		private Map<String, String> participants; // $actorName => $characterName
	}
	@Getter @Setter @RequiredArgsConstructor @ToString
	public class Weights implements Message {
		private Long userId;
		private WeightCreationDTO weights;
	}
	@Getter @Setter @RequiredArgsConstructor @ToString
	public class ApproveWeights implements Message {
		// Difference to Weights is that the userId here will be of an admin
		private Long userId;
		private WeightCreationDTO weights;
	}
	@Getter @Setter @RequiredArgsConstructor @ToString
	public class Distances implements Message {
		private int step;
		private long group;
		private List<DistanceOptionDTO> distances;
	}
	@Getter @Setter @RequiredArgsConstructor @ToString
	public class Waiting implements Message {
		private Long userId;
	}
	@Getter @Setter @RequiredArgsConstructor @ToString
	public class GoAhead implements Message {
		private Long appId;
	}
	@Getter @Setter @RequiredArgsConstructor @ToString
	public class GroupChange implements Message {
		private Long appId;
		private Map<String, List<InterpretationDTO>> groups; // $groupName => [$interpretationDTOs(com actorId)]
	}
}