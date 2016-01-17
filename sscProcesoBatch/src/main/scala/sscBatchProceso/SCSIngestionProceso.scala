package sscBatchProceso

import com.sun.xml.internal.ws.resources.UtilMessages
import org.apache.hadoop.fs.FileSystem
import org.apache.spark.{SparkContext, SparkConf}
import org.joda.time.DateTime

import scala.Console._


/**
  * Created by Carlos Benito Palomares Campos
  */
object SCSIngestionProceso {
  private val utilsBatchProceso =  new UtilsBatchProceso()

  def main(args: Array[String]): Unit = {

    // Configuration of the job environment
    val sc = new SparkContext("local", "Simple", "$SPARK_HOME"
      , List("target/sscBatchProceso-1.0-SNAPSHOT.jar"))


    // JSON para ser formateado
    val url = "http://maps.smartsantander.eu/getdata.php"
    val result = scala.io.Source.fromURL(url).mkString
    val JsonFinal = utilsBatchProceso.formatToJSON(result)
    /*println("Acabado")
    println(JsonFinal)*/
    println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
    println("Salvando al HDFS")
    println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
    val dataArray= JsonFinal.split("\n")
    //Construimos la ruta de la carpeta que corresponda
    val ndateS: String = utilsBatchProceso.getDateHalfHour()
    val rutaAnio = ndateS.substring(0,10)
    val rutaHour = ndateS.substring(11,16)
    val rutaAnioEnd = rutaAnio.replace("-","")
    val rutaHourEnd = rutaHour.replace(":","")

    val distData = sc.parallelize(dataArray)
    distData.saveAsTextFile("hdfs://quickstart.cloudera:8020/user/cloudera/datos/"+rutaAnioEnd+"/"+rutaHourEnd)
    println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
    println("Salvado en HDFS")
    println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
  }


}
