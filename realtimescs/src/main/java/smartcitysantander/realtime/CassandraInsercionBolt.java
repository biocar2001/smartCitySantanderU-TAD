package smartcitysantander.realtime;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import backtype.storm.utils.Utils;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.NoHostAvailableException;

/**
 * Bolt para salvar las medidas en una tabla de cassandra
 * 
 * @author Carlos Benito Palomares Campos
 *
 */
public class CassandraInsercionBolt extends BaseBasicBolt  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Integer id;
	String name;
	Map<String, Integer> counters;
	Cluster cluster ;
	Session session ;

	/**
	 * At the end of the spout (when the cluster is shutdown
	 * We will show the word counters
	 */
	@Override
	public void cleanup() {
		if(cluster != null){
			// Database disconnection
			cluster.close();
		}
	}
	 public static Session getSessionWithRetry(Cluster cluster, String keyspace) {
	        while (true) {
	            try {
	                return cluster.connect(keyspace);
	            } catch (NoHostAvailableException e) {

	                Utils.sleep(1000);
	            }
	        }

	    }
	public static Cluster setupCassandraClient() {
	    return Cluster.builder().addContactPoint("127.0.0.1").build();
	}
	/**
	 * On create 
	 */
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		System.out.println("BOLT EN CASSANDRA");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
	    this.counters = new HashMap<String, Integer>();
	    this.name = context.getThisComponentId();
	    this.id = context.getThisTaskId();
	    cluster = setupCassandraClient();
	    session = CassandraInsercionBolt.getSessionWithRetry(cluster,"scsutad");
	    System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		System.out.println("SESSION EN CASSANDRA");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
	   
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		// Obtains the input values
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
				System.out.println("PREPARANDO A CASSANDRA");
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
				String measure   = input.getStringByField("measure");
				String latitude  = input.getStringByField("latitude");
				String longitude = input.getStringByField("longitude");
				String time = input.getStringByField("date");
				String tag = input.getStringByField("tag"); 
				Double value = input.getDoubleByField("value"); 
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
				System.out.println("salvANDO A CASSANDRA");
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
				// Execution of the CQL INSERT query
				String qry =  
						  "INSERT INTO ss_measures_agr (measure, time, tag, latitude, longitude, value) " +
					      "VALUES (" +
				          "'"+measure+"'," +
				          "'"+time+"'," +
				          "'"+tag+"'," +
				          "'"+latitude+"'," +
				          "'"+longitude+"'," +
				          value +
				          ")" +
				          ";";
				
				session.execute(qry);
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
				System.out.println("ejecuto A CASSANDRA");
				System.out.println(qry);
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");



	}
	

}
