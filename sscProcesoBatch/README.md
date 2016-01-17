# smartCityProjectUTad
Proceso Batch para la ingesta de datos del servicio web en HDFS

He desarrollado un job en Spark con Scala, invoca la url del web service, lo formatea a un formato json
especial con la ayuda de clases java utilizadas en el proceso streaming para finalmente salvarlo en HDFS.

En un entorno de produccion programariamos este job para ser lanzado cada 30 minutos, dejando cada ejecucion
en la carpeta que correspondiese a la media hora y dia correspondiente.

Para probarel proceso, al ser un proyecto maven bastaria con:

mvn clean
mvn install

y Ejecutar la clase principal bien desde el IDE o desde la consola:

SCSIngestionProceso.scala