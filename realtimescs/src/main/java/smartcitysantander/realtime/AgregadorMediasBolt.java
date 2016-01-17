package smartcitysantander.realtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

/**
 * Mediante agregacion añadiremos la media de cada uno de los valores de cada sensor en funcion de una clave
 * 
 * @author Carlos Benito Palomares Campos
 *
 */
public class AgregadorMediasBolt extends BaseRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3046573324012670685L;
	private OutputCollector collector;
	private HashMap<String,String> timeController;
	private HashMap<String,Double> valueController;
	private HashMap<String,Integer> measureCountController;
	public void initialize(){
		this.timeController = new HashMap<String,String>(); // "medida:latitud:longitud,tiempo"
		this.valueController = new HashMap<String,Double>(); // "medida:latitud:longitud,valor"
		this.measureCountController = new HashMap<String,Integer>(); // "medida:latitud:longitud,0"
	}
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		
		this.collector = collector;
		this.initialize();
	}

	@Override
	public void execute(Tuple input) {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		System.out.println("ESTAMOS AGREGANDO");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		String measure   = input.getStringByField("measure");
		String latitude  = input.getStringByField("latitude");
		String longitude = input.getStringByField("longitude");
		String tag = input.getStringByField("tag");
		String time = input.getStringByField("date");
		Double value = input.getDoubleByField("value"); 
		String key = measure + ":" + latitude + ":" + longitude;
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		System.out.println("KEY MEASURE\n");
		System.out.println(key);
		
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		
		// Comprobamos si existe ya la medida 
		if(!timeController.containsKey(key) || timeController.get(key) != time){
			// si no existe la añadiremos para ese intervalo de tiempo con valores cero
			timeController.put(key, time);
			valueController.put(key, 0.0);
			measureCountController.put(key, 0);
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
			System.out.println("KEY ADDED\n");
			System.out.println(key);

			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		}
		// Actualizamos la media del la medida para la posicion actual y emitimos para salvarlo en cassandra
		valueController.put(key, value + valueController.get(key));
		measureCountController.put(key, 1 + measureCountController.get(key));
		double mean = valueController.get(key) / measureCountController.get(key);
		  	List<Object> fields = new ArrayList<Object>();
	    	fields.add(latitude);
	    	fields.add(longitude);
	    	fields.add(time);
	    	fields.add(tag);// Añadimos a que tipoi de sensor pertence para en las viusta añadir una iamgen u otra
	    	fields.add(measure);
	    	fields.add(mean);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		System.out.println("EMITIMOS\n");
		System.out.println(fields);

		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
	    	collector.emit(fields);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("latitude","longitude", "date","tag", "measure", "value"));
	}

}
