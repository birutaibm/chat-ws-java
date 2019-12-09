package assabi.socket.message;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;

import assabi.socket.message.Message.CreateApp;
import assabi.socket.restClient.RestClient;

public class ApplicationCreationTest {
	@Test
	public void createApplication() {
		try {
			String createApp = createAppMessage().toJsonString();
			String strInfo = new RestClient().post("/applications", createApp);
			System.out.println(strInfo);
			Message.AppInfo appInfo = Interpretator.mapper.readValue(strInfo, Message.AppInfo.class);
			System.out.println(Interpretator.write(appInfo));
		} catch (IOException e) {
			fail("throws IOException");
		}
	}

	private CreateApp createAppMessage() {
		CreateApp app = new CreateApp();
		app.setAdmin(1L);
		app.setScenario(1L);
		app.setName(System.currentTimeMillis()+"");
		HashMap<String, Integer> phases = new HashMap<>();
		phases.put("stakeholder", 3);
		phases.put("mix", 2);
		app.setPhases(phases);
		return app;
	}
}
