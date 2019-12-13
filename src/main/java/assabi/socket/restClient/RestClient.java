package assabi.socket.restClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import assabi.socket.message.Interpretator;
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

	public Map<String, ?> getMapFromPost(String endPoint, Object content) {
		String response = post(endPoint, content);
		try {
			return Interpretator.mapper.readValue(response, new HashMap<String, Object>().getClass());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	public String post(String endPoint, Object content) {
		try {
			String body = Interpretator.mapper.writeValueAsString(content);
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
			System.out.println("Posting "+body+" to "+endPoint);
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
}
