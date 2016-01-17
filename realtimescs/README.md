# realtimescs

Este proyecto implementa una topologia en storm que realiza el tratamiento de los datos explicados en el sitio web del proyecto:
http://carlospalomares.weebly.com/

Para el testeo del mismo una vez descargado el proyecto, deberemos realizar los siguientes pasos:

1º Descargar e instalar kafka_2.10-0.8.2.0:

- En funcion de que distribucion estemos utilizando arrancaremos zookeeper de kafka o no, ya que la distribucion de cloudera ya incluye un zookeper con el que no seria necesario arrancar dicho servicio. En el caso de estar en un ubuntu donde hubiesemos instalado todo desde cero:
	
	sudo bin/zookeeper-server-start.sh config/zookeeper.properties
	
- Arrancar kafka:
	sudo bin/kafka-server-start.sh config/server.properties
	
- Crear topic donde cargaremos la informacion:
	bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic streamingSA

2º Lanzar SimpleProducer.java, el cual cargara la informacion a tratar en kafka. En un entorno real prodriamos empaquetar esta clase en un jar y ejecutarlo cada 30 minutos con el cron de linux o herramientas como quartz en java.

3º Arrancar cassandra en tu sistema:

- sudo cassandra -f

4º Crear el database de los sensores, ejecutando en cassandra el fichero databaseCreationSchema.cql

NOTA: Para el caso de querer guardar en redis, deberiamos instalar y arrancar REDIS.

5º Bajar e instalar storm:

- arrancar:

- sudo ./storm nimbus
- sudo ./storm supervisor
- sudo ./storm ui

6º Lanzar la topologia:
- mvn clean
- mvn install
- mvn compile exec:java -Dexec.classpathScope=compile -Dexec.mainClass=smartcitysantander.realtime.SCSRealtimeTopology
