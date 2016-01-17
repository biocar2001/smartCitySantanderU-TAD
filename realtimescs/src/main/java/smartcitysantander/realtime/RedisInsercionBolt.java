package smartcitysantander.realtime;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

public class RedisInsercionBolt extends BaseRichBolt {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Map<String, Integer> counters;
	private OutputCollector collector;
	private Jedis jedis;
	private String host;
	private int port;
	private int uniqueWords=0;
	private int totalWords=0;
	private long startTimer;

	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		
		this.collector = collector;
		host = stormConf.get(Conf.REDIS_HOST_KEY).toString();
		port = Integer.valueOf(stormConf.get(Conf.REDIS_PORT_KEY).toString());
		connectToRedis();
	}
	private void connectToRedis() {
		jedis = new Jedis(host, port);
		jedis.connect();
	}

	@Override
	public void execute(Tuple input) {
		String measure   = input.getStringByField("measure");
		String latitude  = input.getStringByField("latitude");
		String longitude = input.getStringByField("longitude");
		String time = input.getStringByField("date");
		String tag = input.getStringByField("tag"); 
		Double value = input.getDoubleByField("value"); 
		JSONObject json = new JSONObject();
		try {
			json.put("measure", measure);
			json.put("latitude", latitude);
			json.put("longitude",longitude );
			json.put("tag", tag);
			json.put("value", value);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		jedis.set(time, json.toString());
		
	}

	@Override
	public void cleanup() {
		
		
		long end = System.currentTimeMillis();
		System.out.println("Final Time:");
		System.out.println(((end - this.startTimer) / 1000));
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word","total"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}
}
