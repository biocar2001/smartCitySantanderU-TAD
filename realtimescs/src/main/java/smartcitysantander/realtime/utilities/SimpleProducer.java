package smartcitysantander.realtime.utilities;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * Clase utilizada para cargar el fichero que corresponda en la cola kafka para despues ser consumido*/
public class SimpleProducer {
	private static Producer<String, String> producer;
	public SimpleProducer() {
		Properties props = new Properties();
		// Set the broker list for requesting metadata to find the lead broker
		props.put("metadata.broker.list","localhost:9092, localhost:9093,localhost:9094");
		//This specifies the serializer class for keys
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		// 1 means the producer receives an acknowledgment once the lead replica
		// has received the data. This option provides better durability as the
		// client waits until the server acknowledges the request as successful.
		props.put("request.required.acks", "1");
		ProducerConfig config = new ProducerConfig(props);
		producer = new Producer<String, String>(config);
	}
	public static void main(String[] args) throws JSONException {
		
		String topic = "streamingSA";

		SimpleProducer simpleProducer = new SimpleProducer();
		simpleProducer.publishMessage(topic);
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}
	private void publishMessage(String topic) throws JSONException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String fecha = dateFormat.format(cal.getTime());
		JSONObject json = null;
		try {
			json = readJsonFromUrl("http://maps.smartsantander.eu/getdata.php");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(json.toString());
		JSONArray jsons = json.getJSONArray("markers");
		for (int i = 0, size = jsons.length(); i < size; i++)
		{
			JSONObject objectInArray = jsons.getJSONObject(i);
			System.out.println("Insertando en kafka Line:");
			System.out.println(objectInArray);
			// Creates a KeyedMessage instance
			KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, fecha+ "~" +objectInArray);
			// Publish the message
			producer.send(data);
			System.out.println("Insertada Line:");
			System.out.println(fecha+ "~" +objectInArray);

		}

		// Close producer connection with broker.
		producer.close();
	}
}