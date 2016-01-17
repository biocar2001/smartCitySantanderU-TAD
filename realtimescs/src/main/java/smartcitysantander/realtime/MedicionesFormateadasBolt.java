package smartcitysantander.realtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import smartcitysantander.realtime.utilities.MedidasExtraidasFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

/**
 * Emite las medidas de cada sensor de una manera simplificada, entendiendo cada medida en funcion de la hora de insercion
 * cada media hora.
 * 
 * @author Carlos Benito Palomares Campos
 *
 */
public class MedicionesFormateadasBolt extends BaseRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
	}

	@Override
	public void execute(Tuple input) {
		
		// obtenemos el json del anterior bolt
		String sensor = input.getStringByField("json");
	    JSONParser jParser = new JSONParser();
	    JSONObject jsonData = null;
		try {
			jsonData = (JSONObject) jParser.parse(sensor);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// Obtenemos los campos que nos interesan del json recuperado
	    String latitude = (String) jsonData.get("latitude");
	    String longitude = (String) jsonData.get("longitude");
	    String reqTime = (String) jsonData.get("requesttime");
	    
	    // calculamos la media hora a la que pertenece la medicion recuperando el timestamp en que fue insertado en kafka
	    DateTimeFormatter dateParser = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	    DateTime date = dateParser.parseDateTime(reqTime);
	    DateTime ndate = date.plusSeconds(date.getSecondOfMinute() * -1).plusMinutes((date.getMinuteOfHour() % 30) * -1);
	    String tag = (String) jsonData.get("tags");
	    
	    // Obtenemos las medidas que corresponden al sensor que ha llegado el json
	    HashMap<String, Double> measures = MedidasExtraidasFactory.getExtractorMedidasr(tag).getArrayMedidas(jsonData);
	    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		System.out.println("emitimos medidas");
		System.out.println(measures);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
	   
	    for(Entry<String, Double> measure : measures.entrySet()){
	    	List<Object> fields = new ArrayList<Object>();
	    	fields.add(latitude);
	    	fields.add(longitude);
	    	fields.add(tag);
	    	fields.add(dateParser.print(ndate));
	    	fields.add(measure.getKey());
	    	fields.add(measure.getValue());
	    	collector.emit(fields);
	    }		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("latitude","longitude", "tag", "date", "measure", "value"));
	}

}
