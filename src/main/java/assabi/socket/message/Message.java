package assabi.socket.message;

import java.util.List;
import java.util.Map;

import assabi.dto.CharacterDTO;
import assabi.dto.DistanceOptionDTO;
import assabi.dto.ScenarioDTO;
import assabi.dto.WeightCreationDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public interface Message {
	@Getter @Setter @RequiredArgsConstructor @ToString
	public class Logged implements Message {
		private Long userId;
		private String name;
	}
	@Getter @Setter @RequiredArgsConstructor @ToString
	public class CreateApp implements Message {
		private Long userId;
		private Long scenario;
		private String name;
		private Map<String, Integer> phases;
	}
	@Getter @Setter @RequiredArgsConstructor @ToString
	public class AppInfo implements Message {
		private Long appId;
		private ScenarioDTO scenario;
		private List<Group> groups;
		@Getter @Setter @RequiredArgsConstructor @ToString
		private class Group {
			private String name;
			private Long id;
			private List<CharacterDTO> characters; // Character do assabi-back
		}
	}
	@Getter @Setter @RequiredArgsConstructor @ToString
	public class NewActor implements Message {
		private Long actorId;
		private String actorName;
	}
	@Getter @Setter @RequiredArgsConstructor @ToString
	public class ParticipationIntesion implements Message {
		private Long group;
		private Long character;
		private Long actor;
	}
	@Getter @Setter @RequiredArgsConstructor @ToString
	public class ParticipationApproval implements Message {
		private Long userId;
		private List<Message.ParticipationIntesion> approve;
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
		private DistanceOptionDTO distances;
	}
	@Getter @Setter @RequiredArgsConstructor @ToString
	public class Waiting implements Message {
		private Long userId;
	}
	@Getter @Setter @RequiredArgsConstructor @ToString
	public class GoAhead implements Message {
		private Long userId;
	}
	@Getter @Setter @RequiredArgsConstructor @ToString
	public class GroupChange implements Message {
		private Long userId;
		private Map<String, long[]> groups; // $groupName => [$characterIds]
	}
}