package smartcitysantander.realtime;

import java.util.UUID;


import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;

public class SCSRealtimeTopology{
	
	private TopologyBuilder builder = new TopologyBuilder();
	private LocalCluster cluster;
	private Config conf=new Config();
	public static final String DEFAULT_JEDIS_PORT = "6379";
	public SCSRealtimeTopology() {
		//Opcionalmente podriamos cambiar de cassandra a REDIS, como alternativa debase de datos.
		conf.put(Conf.REDIS_PORT_KEY, DEFAULT_JEDIS_PORT);

		BrokerHosts hosts = new ZkHosts("localhost:2181");
		SpoutConfig spoutConfig = new SpoutConfig(hosts, "streamingSA", "", UUID.randomUUID().toString());
		spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		spoutConfig.forceFromStart = true;
		spoutConfig.startOffsetTime = kafka.api.OffsetRequest.EarliestTime();
		KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);
		

		builder.setSpout("kafka-reader", kafkaSpout, 4);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		System.out.println("Kafka leido");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		builder.setBolt("json-formato", new JsonFormatterBolt(), 10).shuffleGrouping(
				"kafka-reader");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		System.out.println("Json formateado");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		builder.setBolt("formatea-medidas", new MedicionesFormateadasBolt(), 10).shuffleGrouping(
				"json-formato");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		System.out.println("medidas obtenidas");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		builder.setBolt("agregacion", new AgregadorMediasBolt(), 10).shuffleGrouping(
				"formatea-medidas");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		System.out.println("Agregacines hechas");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		builder.setBolt("cassandra-save", new CassandraInsercionBolt(), 1).globalGrouping(
				"agregacion");
		//Opcionalmente podriamos cambiar de cassandra a REDIS, como alternativa debase de datos.
		//builder.setBolt("redis-save", new RedisInsercionBolt(), 1).globalGrouping(
		//		"agregacion");
		
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		System.out.println("cassandra savlvado");
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
	}
	public TopologyBuilder getBuilder() {
		return builder;
	}
	
	public LocalCluster getLocalCluster() {
		return cluster;
	}
	public Config getConf() {
		return conf;
	}

	public void runLocal(int runTime) {
		// Ejecucion local
		conf.setDebug(true);
		//Opcionalmente podriamos cambiar de cassandra a REDIS, como alternativa debase de datos.
		conf.put(Conf.REDIS_HOST_KEY, "localhost");
		cluster = new LocalCluster();
		cluster.submitTopology("test", conf, builder.createTopology());
		if (runTime > 0) {
			Utils.sleep(runTime);
			shutDownLocal();
		}
	}

	public void shutDownLocal() {
		// muerte del proceso local
		if (cluster != null) {
			cluster.killTopology("test");
			cluster.shutdown();
		}
	}
	public void runCluster(String name)
			throws AlreadyAliveException, InvalidTopologyException {
		conf.setNumWorkers(20);
		conf.put(Conf.REDIS_HOST_KEY, "localhost");
		StormSubmitter.submitTopology(name, conf, builder.createTopology());
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// inicio and lunch
		SCSRealtimeTopology topology = new SCSRealtimeTopology();
		try {
			System.out.println("Ejecutandose en modo local.");
			topology.runLocal(20000);
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
			System.out.println("RunLocal lanzado");
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
