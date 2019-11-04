package assabi.socket.restClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

import assabi.socket.message.Message;

public class RestClient {
	private final String baseURL;

	public RestClient() {
		super();
		baseURL = "http://assabi-back.herokuapp.com";
	}
	
	public boolean postSuccess(String endPoint, String body) {
		try {
			HttpURLConnection conn = doPost(endPoint, body);
			int code = conn.getResponseCode();
			return (200 <= code) && (code < 300);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public String post(String endPoint, String body) {
		try {
			HttpURLConnection conn = doPost(endPoint, body);
			return read(conn);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private HttpURLConnection doPost(String endPoint, String body) {
		try {
			HttpURLConnection conn = connect(endPoint);
			conn.setDoOutput(true);
			conn.getOutputStream().write(body.getBytes(Charset.forName("UTF-8")));
			return conn;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public String get(String endPoint) {
		try {
			return read(connect(endPoint));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String read(HttpURLConnection connection) throws IOException {
		String response = new BufferedReader(new InputStreamReader(connection.getInputStream()))
				.lines()
				.collect(Collectors.joining("\n"));
		connection.disconnect();
		return response;
	}

	private HttpURLConnection connect(String endPoint) throws IOException {
		try {
			URL url = new URL(baseURL + endPoint);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			return conn;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
			Message.Login login = new Message.Login();
			login.setLogin("solangegarcia@fearp.usp.br");
			login.setPassword("1234");
			String msgStr = login.toJsonString();
			String response = new RestClient().post("/login", msgStr);
			System.out.println(msgStr + " => " + response);
	}
}
