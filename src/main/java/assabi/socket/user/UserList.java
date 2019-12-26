package assabi.socket.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.java_websocket.WebSocket;

import assabi.socket.message.Message.AppInfo;
import assabi.socket.utils.PingPongThread;

public class UserList {
	public static final long ADMIN_GROUP_ID = 0L;
	private final Map<Long, User> fromId;
	private final Map<WebSocket, User> fromConnection;
	private final Map<Long, Map<Long, List<Long>>> appUserGroups;
    private final Map<Long, AppInfo> appInfos;

	public UserList() {
		fromId = new HashMap<>();
		fromConnection = new HashMap<>();
		appUserGroups = new HashMap<>();
		appInfos = new HashMap<>();
		new PingPongThread(fromConnection::keySet);
	}

	public Optional<User> getFromId(Long id) {
		return Optional.ofNullable(fromId.get(id));
	}

	public Optional<User> getFromConnection(WebSocket connection) {
		return Optional.ofNullable(fromConnection.get(connection));
	}

	public void add(User user) {
		fromId.put(user.getUserId(), user);
		fromConnection.put(user.getConnection(), user);
	}

	public User remove(WebSocket conn) {
		User user = fromConnection.remove(conn);
		fromId.remove(user.getUserId());
		return user;
	}

	public Stream<User> getAppUsers(Long appId) {
		return appUserGroups.getOrDefault(appId, Collections.emptyMap())
				.values().stream().flatMap(List::stream)
					.map(this::getFromId)
					.filter(Optional::isPresent)
					.map(Optional::get);
	}

	public Stream<User> getGroupUsers(Long appId, Long groupId) {
		return appUserGroups.getOrDefault(appId, Collections.emptyMap())
				.getOrDefault(groupId, Collections.emptyList()).stream()
					.map(this::getFromId)
					.filter(Optional::isPresent)
					.map(Optional::get);
	}

	public Optional<User> setActor(Long appId, Long groupId, Long userId) {
		appUserGroups.get(appId)
			.compute(groupId, (k, v) -> (v == null) ? new ArrayList<>() : v)
			.add(userId);
		return getFromId(userId);
	}

	public void setAdmin(User user, AppInfo appInfo) {
		appInfos.put(appInfo.getId(), appInfo);
		List<Long> adminList = Collections.singletonList(user.getUserId());
		Map<Long, List<Long>> adminMap = new HashMap<>();
		adminMap.put(ADMIN_GROUP_ID, adminList);
		appUserGroups.put(appInfo.getId(), adminMap);
	}

	public Optional<User> getAdmin(Long actorId, Long groupId) {
		return appUserGroups.keySet().stream()
			.filter(app -> {
				Map<Long, List<Long>> groups = appUserGroups.get(app);
				List<Long> users = groups.get(groupId);
				return (users != null) && (users.contains(actorId));
			}).findAny()
			.map(app -> appUserGroups.get(app).get(ADMIN_GROUP_ID).get(0))
			.flatMap(this::getFromId);
	}

	public Stream<Long> getAppsAdministratedBy(long userId) {
		return appUserGroups.keySet().stream()
				.filter(app -> {
					Map<Long, List<Long>> groups = appUserGroups.get(app);
					List<Long> users = groups.get(ADMIN_GROUP_ID);
					return (users != null) && (users.contains(userId));
				});
	}
	public AppInfo getAppInfo(Long appId) {
		return appInfos.get(appId);
	}
}
