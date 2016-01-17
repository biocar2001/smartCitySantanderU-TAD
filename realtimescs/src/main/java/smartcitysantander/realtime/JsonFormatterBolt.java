package smartcitysantander.realtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import smartcitysantander.realtime.utilities.ExtractorMedidas;
import smartcitysantander.realtime.utilities.MedidasExtraidasFactory;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

/**
 * Bolt que forma objeto JSON valido para registrar las medidas apartir del json recuperado de kafka
 * 
 * @author Carlos Benito Palomares Campos
 *
 */
public class JsonFormatterBolt extends BaseRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	private HashMap<String, Integer> sensorStatus;
	
	public void initialize() {
		sensorStatus = new HashMap<String, Integer>();
	}
	
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
		this.initialize();
	}

	@Override
	public void execute(Tuple input) {
		// Obtenemos contenido y lo formateamos
		String originalContent = input.getStringByField("str");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		System.out.println("Obtenemos JSON");
		System.out.println(originalContent);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		String formattedContent = this.formatToJSON(originalContent);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		System.out.println("Formateado JSON");
		System.out.println(formattedContent);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		// si hay valores en el nuevo json lo emitimos al siguiente bolt para hacer un mesnaje mas legible
		if (formattedContent != "") {
			ArrayList<Object> a = new ArrayList<Object>();
			a.add(formattedContent);
			collector.emit(a);
		}
		
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("json"));

	}

	/**
	 * Devuelve un json formateado con la medida encontrada, en la entrada recuperada de kafka
	 * @param content
	 * @return String con la infor necesaria
	 */
	public String formatToJSON(String content) {
		ArrayList<String> sensorsData = new ArrayList<String>();
		
		JSONObject sensorData = new JSONObject();
		JSONParser jparser = new JSONParser();
		String requestTs = null;
		String JSONContent = null;
		
		// Separacion del contenido del json y la fecha de insercion en kafka
		try {
			String[] splittedContent = content.split("~");
			requestTs = splittedContent[0];
			JSONContent = splittedContent[1];
			sensorData = (JSONObject) jparser.parse(JSONContent);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// Obtenemos valores caracteristicos del sensor
		
		String tag = (String) sensorData.get("tags");
		String title = (String) sensorData.get("title");
		String sensorContent = (String) sensorData.get("content")
				+ (String) sensorData.get("image");
		int contentHash = sensorContent.hashCode();
		
		// Comprobamos si la medicion es la misma o ha cambiado de valor
		if (sensorStatus.containsKey(title)
				&& sensorStatus.get(title).equals(contentHash)) {
		} else {
			
			// Actualizamos el hashcontent, hay entradas nuevas
			sensorStatus.put(title, contentHash);
			ExtractorMedidas me = MedidasExtraidasFactory.getExtractorMedidasr(tag);
			if (me == null) {
				// No hacemos nada, es el mismo sensor con los mismos valores
			} else {
				
				// Extraccion de las medidas
				JSONArray newContent = me.getJSONMedidas(sensorContent);
				Iterator<JSONObject> it = newContent.iterator();
				
				// para cada medida escribimos su valor en un JSON Array Object
				while (it.hasNext()) {
					sensorData.putAll(it.next());
				}
				sensorData.remove("content");
				//sensorData.remove("image");
				sensorData.put("requesttime", requestTs);
				sensorsData.add(sensorData.toJSONString());
			}
		}
		
		
		// Returns the JSON object
		String finalData = "";
		for (String sensor : sensorsData) {
			if (finalData.length() > 0) {
				finalData = finalData + "\n";
			}
			finalData = finalData + sensor;
		}
		System.out.println("FINAL DATA: " + finalData.toString());
		return finalData;
	}
}
