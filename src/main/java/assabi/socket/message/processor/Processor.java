package assabi.socket.message.processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.java_websocket.WebSocket;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import assabi.dto.DistanceOptionDTO;
import assabi.dto.ParticipationDTO;
import assabi.dto.ParticipationDTO.InterpretationDTO;
import assabi.dto.WeightCreationDTO;
import assabi.socket.SocketServer;
import assabi.socket.message.Interpretator;
import assabi.socket.message.Message;
import assabi.socket.restClient.RestClient;
import assabi.socket.user.User;
import assabi.socket.user.UserList;

public interface Processor<M extends Message> {
	public final RestClient api = new RestClient();
	void process(M message, WebSocket connection, SocketServer server);
	
	public final class Login implements Processor<Message.Login> {
		@Override
		public void process(Message.Login message, WebSocket connection, SocketServer server) {
			try {
				String response = api.post("/login", message.toJsonString());
				HashMap<String, Object> map = Interpretator.mapper.readValue(response, new HashMap<String, Object>().getClass());
				String name = message.getLogin();
				if (map.containsKey("adminId")) {
					User user = new User(connection,
							(Long) map.get("adminId"),
							name);
					server.addUser(user);
				} else {
					Long actorId = (Long) map.get("actor");
					Long appId = (Long) map.get("appId");
					
					User user = new User(connection,
							actorId,
							name);
					server.addUser(user);
					
					UserList userList = server.getUserList();
					Message.AppInfo appInfo = userList.getAppInfo(appId);
					connection.send(Interpretator.write(appInfo));
					
					Message.NewActor newUser = new Message.NewActor();
					newUser.setActorId(actorId);
					newUser.setActorName(name);
					String wrap = Interpretator.write(newUser);
					
					userList.getGroupUsers(appId, UserList.ADMIN_GROUP_ID)
						.findFirst()
						.map(User::getConnection)
						.ifPresent(c -> c.send(wrap));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public final class CreateApp implements Processor<Message.CreateApp> {
		@Override
		public void process(Message.CreateApp message, WebSocket connection, SocketServer server) {
			String response = api.post("/applications", message.toJsonString());
			server.getUserList().getFromConnection(connection).ifPresent(user -> {
				try {
					Message.AppInfo appInfo = Interpretator.mapper.readValue(response, Message.AppInfo.class);
					server.getUserList().setAdmin(user, appInfo);
					connection.send(Interpretator.write(appInfo));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		}
	}
	
	public final class ParticipationIntesion implements Processor<Message.ParticipationIntesion> {
		@Override
		public void process(Message.ParticipationIntesion message, WebSocket connection, SocketServer server) {
			try {
				String wrap = Interpretator.write(message);
				server.getUserList().getGroupUsers(message.getApplication(), UserList.ADMIN_GROUP_ID)
					.findFirst()
					.map(User::getConnection)
					.ifPresent(admin -> admin.send(wrap));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public final class ParticipationApproval implements Processor<Message.ParticipationApproval> {
		@Override
		public void process(Message.ParticipationApproval message, WebSocket connection, SocketServer server) {
			List<ParticipationDTO> dtos = approvedDTOs(message);
			try {
				String response = api.post("/participations", Interpretator.mapper.writeValueAsString(dtos));
				HashMap<String, String> map = Interpretator.mapper.readValue(response, new HashMap<String, String>().getClass());
				Map<Long, List<Message.ParticipationIntesion>> savedIntensionsByGroups = message
						.getApprove().stream()
						.collect(Collectors.groupingBy(Message.ParticipationIntesion::getGroupId));
				if (map.containsKey("fails")) {
					List<ParticipationDTO> fails = (List<ParticipationDTO>) Interpretator.mapper.readValue(map.get("fails"), dtos.getClass());
					fails.forEach(dto -> {
						Long group = dto.getGroup();
						savedIntensionsByGroups.get(group)
							.removeIf(intension -> sameInterpretation(dto, intension));
					});
				}
				List<Map<String, ?>> savedParticipations = Interpretator.mapper.readValue(map.get("success"), List.class);
				Map<Long, Long> actorToParticipationMap = savedParticipations.stream()
					.map(this::getActorParticipationIds)
					.flatMap(item -> item.entrySet().stream())
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
				savedIntensionsByGroups.values().forEach(intensions ->
					approveIntensions(server, actorToParticipationMap, intensions));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// FIXME implement this
			throw new RuntimeException("Not yet impemented");
		}

		private List<ParticipationDTO> approvedDTOs(Message.ParticipationApproval message) {
			return message.getApprove().stream()
				.map(intension -> {
					InterpretationDTO interpretation = new InterpretationDTO(intension.getActorId(), intension.getCharacterId());
					ParticipationDTO dto = new ParticipationDTO(intension.getGroupId(), interpretation);
					return dto;
				}).collect(Collectors.toList());
		}

		private boolean sameInterpretation(ParticipationDTO dto, Message.ParticipationIntesion intension) {
			InterpretationDTO interpretation = dto.getInterpretation();
			return (intension.getActorId() == interpretation.getActor()) &&
					(intension.getCharacterId() == interpretation.getCharacter());
		}

		private void approveIntensions(SocketServer server, Map<Long, Long> savedMap,
				List<Message.ParticipationIntesion> intensions) {
			Map<String, String> participants = getGroupPaticipants(intensions);
			intensions.stream().forEach(intension ->
				apprveIntesion(server, savedMap, intension.getGroupId(), participants, intension));
		}

		private Map<String, String> getGroupPaticipants(List<Message.ParticipationIntesion> intensions) {
			Map<String, String> participants = new HashMap<>();
			intensions.forEach(intension ->
				participants.put(intension.getActorName(), intension.getCharacterName()));
			return participants;
		}

		private Message.ParticipationApproved apprveIntesion(SocketServer server, Map<Long, Long> savedMap, Long group,
				Map<String, String> participants, Message.ParticipationIntesion intension) {
			Message.ParticipationApproved approved = new Message.ParticipationApproved();
			Long actorId = intension.getActorId();
			approved.setGroupId(group);
			approved.setGroupName(intension.getGroupName());
			approved.setParticipants(participants);
			approved.setId(savedMap.get(actorId));
			configureAndInform(server, group, approved, intension.getApplication(), actorId);
			return approved;
		}

		private void configureAndInform(SocketServer server, Long group,
				Message.ParticipationApproved approved, Long appId, Long actorId) {
			server.getUserList().setActor(appId, group, actorId)
				.ifPresent(user -> {
					user.getBuffer().setContent(approved);
					user.waiting();
					user.getBuffer().flush();
				});
		}
		
		private Map<Long, Long> getActorParticipationIds(Map<String, ?> participation) {
			Long participationId = (Long) participation.get("id");
			Map<String, ?> interpretation = (Map<String, ?>) participation.get("interpretation");
			Map<String, ?> actor = (Map<String, ?>) interpretation.get("actor");
			Long actorId = (Long) actor.get("id");
			return Collections.singletonMap(actorId, participationId);
		}
	}
	
	public final class Weights implements Processor<Message.Weights> {
		@Override
		public void process(Message.Weights message, WebSocket connection, SocketServer server) {
			try {
				String wrap = Interpretator.write(message);
				server.getUserList()
					.getAdmin(message.getUserId(), message.getWeights().getGroup())
					.map(User::getConnection)
					.ifPresent(admin -> admin.send(wrap));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public final class ApproveWeights implements Processor<Message.ApproveWeights> {
		@Override
		public void process(Message.ApproveWeights message, WebSocket connection, SocketServer server) {
			sendDistances(server,
					getDistance(postWeight(message.getWeights())),
					message.getUserId(),
					message.getWeights().getGroup());
		}

		private Long postWeight(WeightCreationDTO weights) {
			try {
				String response = api.post("/weights", Interpretator.mapper.writeValueAsString(weights));
				Map<String, ?> map = new HashMap<>();
				map = Interpretator.mapper.readValue(response, map.getClass());
				return (Long) map.get("id");
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		private Message.Distances getDistance(long weight) {
			try {
				String response = api.get("/weights/"+weight+"/distances");
				ArrayList<DistanceOptionDTO> list = new ArrayList<>();
				list = Interpretator.mapper.readValue(response, list.getClass());
				Message.Distances distances = new Message.Distances();
				distances.setDistances(list);
				return distances;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		private void sendDistances(SocketServer server, Message.Distances distance, Long adminId, long groupId) {
			try {
				UserList userList = server.getUserList();
				String wrap = Interpretator.write(distance);
				userList.getAppsAdministratedBy(adminId)
					.flatMap(app -> userList.getGroupUsers(app, groupId))
					.map(User::getConnection)
					.forEach(actor -> actor.send(wrap));
				userList.getFromId(adminId)
					.map(User::getConnection)
					.ifPresent(admin -> admin.send(wrap));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public final class Waiting implements Processor<Message.Waiting> {
		@Override
		public void process(Message.Waiting message, WebSocket connection, SocketServer server) {
			server.getUserList().getFromId(message.getUserId()).ifPresent(User::waiting);
		}
	}
	
	public final class GoAhead implements Processor<Message.GoAhead> {
		@Override
		public void process(Message.GoAhead message, WebSocket connection, SocketServer server) {
			UserList userList = server.getUserList();
			userList.getAppUsers(message.getAppId()).forEach(user -> user.getBuffer().flush());
		}
	}
	
	public final class GroupChange implements Processor<Message.GroupChange> {
		@Override
		public void process(Message.GroupChange message, WebSocket connection, SocketServer server) {
			try {
				postMixGroups(message).forEach(group ->
					configureGroup(message, server, (Map<String, ?>) group));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// FIXME implement this
			throw new RuntimeException("Not yet impemented");
		}

		private Collection<?> postMixGroups(Message.GroupChange message)
				throws JsonProcessingException, IOException, JsonParseException, JsonMappingException {
			String endPoint = "applications/"+message.getAppId()+"/mix_groups";
			Map<String, List<InterpretationDTO>> groups = message.getGroups();
			String body = Interpretator.mapper.writeValueAsString(groups);
			String response = api.post(endPoint, body);
			Map<String, ?> phase = new HashMap<>();
			phase = Interpretator.mapper.readValue(response, phase.getClass());
			return (Collection<?>) phase.get("groups");
		}

		private void configureGroup(Message.GroupChange message, SocketServer server, Map<String, ?> group) {
			HashMap<String, String> participants = new HashMap<>();
			Long groupId = (Long) group.get("id");
			String groupName = (String) group.get("name");
			Collection<?> participations = (Collection<?>) group.get("participations");
			participations.stream().map(participation ->
				extractParticipationInfo(server, participants, (Map<String, ?>) participation)
			).forEach(ids -> {
				Message.ParticipationApproved approved = new Message.ParticipationApproved();
				approved.setGroupId(groupId);
				approved.setGroupName(groupName);
				approved.setId(ids.get("participation"));
				approved.setParticipants(participants);
				server.getUserList().setActor(message.getAppId(), groupId, ids.get("actor"))
					.ifPresent(user -> {
						user.getBuffer().setContent(approved);
						user.getBuffer().flush();
					});
			});
		}

		private Map<String, Long> extractParticipationInfo(SocketServer server, HashMap<String, String> participants,
				Map<String, ?> participation) {
			Long participationId = (Long) participation.get("id");
			Map<String, ?> interpretation = (Map<String, ?>) participation.get("interpretation");
			Map<String, ?> map = (Map<String, ?>) interpretation.get("actor");
			Long actorId = (Long) map.get("id");
			server.getUserList().getFromId(actorId)
				.map(user -> {
					String actor = user.getName();
					String character = user.getBuffer().getContent().getParticipants().get(actor);
					return Collections.singletonMap(actor, character);
				})
				.ifPresent(participant -> participants.putAll(participant));
			Map<String, Long> ids = new HashMap<>();
			ids.put("participation", participationId);
			ids.put("actor", actorId);
			return ids;
		}
	}
	
}