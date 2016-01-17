
import com.mysql.jdbc.PreparedStatement;
import org.joda.time.DateTime;

import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

public class HiveMysqlMain {
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";
    private static Utils util = new Utils();
    private static DateTime todayFromZero = new DateTime().withTimeAtStartOfDay();

    /**
     * Descripcion: Clase principal Java que conecta con el database de los sensores smartcities(), obteniendo los parametros necesarios
     * y los inserta en una tabla mysql para ser consumida a posteriori por nuestra api rest.
     * El proyecto no es un maven project debido a la version de la virtual machine de cloudera(CDH5) y la version de hive(Hive version 0.12)
     * que hacen que se produzcan errores de dependencias los cuales fueron solucionados en versiones posteriores.
     * Debido a la falta de tiempos para este proyecto obtamos por la inclusion de las librerias necesarias mediante el classpath de la maquina virtual;
     * Bien desde eclipse, inteliJ o cualqueir otro IDE o seteandolas en nuestro classpath:
     * Add /usr/lib/hive/lib/*.jar and /usr/lib/hadoop/*.jar to your classpath.
     *
     * Tal y como es explicado en la web oficial de cloudera a este respecto:
     * http://www.cloudera.com/content/www/en-us/documentation/enterprise/latest/topics/cdh_ig_hive_jdbc_install.html
     * @param args
     * @throws SQLException
     */
    public static void main(String[] args) throws SQLException {
        try {
            try {
                Class.forName(driverName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.exit(1);
            }

            //Conexion con nuestro database
            Connection con = DriverManager.getConnection("jdbc:hive2://localhost:10000/scsantander", "cloudera", "");
            Statement stmt = con.createStatement();
            String tableName = "scsSensoresTemp";

            //Añadimos el jar correspondiente al Serde que trata con objetos JSON
            stmt.execute("add jar /home/cloudera/temp/json-serde-1.3-jar-with-dependencies.jar");

            //Carga de la tabla con sobre escritura de los logs del hdfs
            stmt.execute("LOAD DATA INPATH '/user/cloudera/datos/*/*/*' INTO TABLE scsSensoresTemp");

            // Describe table Para comprobar la correcta creacion de los campos
            String sql = "describe " + tableName;
            System.out.println("Running: " + sql);
            ResultSet res = stmt.executeQuery(sql);
            while (res.next()) {
                System.out.println(res.getString(1) + "\t" + res.getString(2));
            }


            //Construimos el dia de hoy
            String Hoy = util.getDateHalfHour();
            System.out.println("Variable Hoy ----------- " + Hoy);
            String diaHoy = Hoy.substring(0,10);
            System.out.println("Variable diaHoy ----------- " + diaHoy);

            // Recuperamos todas las medidas de temperatura de dia de hoy y las insertamos en una tabla mysql previamente limpiada
            // A fin de tener una tabla con las medicioens dle ultimo dia en mysql
            sql = "select distinct id as sensorId, requesttime as HoraSensor, temperature as grados from scsSensoresTemp where tags = 'temp' and SUBSTR(requesttime,1,10)='"+diaHoy+"'";
            System.out.println("Running: " + sql);
            res = stmt.executeQuery(sql);
            ArrayList<RegisterFullMysql> nuevoTemp = new ArrayList<RegisterFullMysql>();
            while (res.next()) {
                System.out.print(res.getString(1) + "-----------");
                System.out.print(res.getString(2) + "-----------");
                System.out.println(res.getString(3) + "-----------");
                RegisterFullMysql nuevo = new RegisterFullMysql("temperature");
                String dateInString = res.getString(2);

                nuevo.setFecha(dateInString);
                nuevo.setId(res.getString(1));
                nuevo.setTemp(Float.parseFloat(res.getString(3)));
                nuevoTemp.add(nuevo);
            }
            if(insertAllRegisterInMysql(nuevoTemp)){
                System.out.println("MYSQL INSERCION MEDIDAS DIA: "+diaHoy+" CORRECTA");
            }else{
                System.out.println("MYSQL INSERCION MEDIDAS DIA: "+diaHoy+" ERRONEA");
            }

            RegisterMediasDiaMysql nuevoToday = new RegisterMediasDiaMysql("temperature");
            //Generacion de media del dia de hoy
            //drop view
            stmt.execute("DROP VIEW temp_today");
            System.out.println("Executing ----------- DROP VIEW temp_today");
            //create view
            stmt.execute("CREATE VIEW temp_today AS select distinct p.id as idSensor, p.temperature as grados from scsSensoresTemp p where p.tags = 'temp' and SUBSTR(p.requesttime,1,10)='"+diaHoy+"'");
            System.out.println("Executing -----------  "+"CREATE VIEW temp_today AS select distinct p.id as idSensor, p.temperature as grados from scsSensoresTemp p where p.tags = 'temp' and SUBSTR(p.requesttime,1,10)='"+diaHoy+"'");
            //Get media
            ResultSet resT = stmt.executeQuery("select avg(grados) as mediaGeneral from temp_today");
            System.out.println("Executing ----------- select avg(grados) as mediaGeneral from temp_today");
            while (resT.next()) {
                System.out.print(resT.getFloat(1) + "-----------");
                float floatInMedia = resT.getFloat(1);
                nuevoToday.setMedia(floatInMedia);
            }
            //Generacion de media de cada hora dia de hoy
            String horaDiaHoy;

            ArrayList<Float> valuesToday = new ArrayList<Float>();
            DateTime tomorrowDay1 = new DateTime().withTimeAtStartOfDay();
            DateTime tomorrowDay2 = tomorrowDay1.plusDays(1).withTimeAtStartOfDay();
            while(todayFromZero.isBefore(tomorrowDay2)){
                horaDiaHoy = util.formaterDate(todayFromZero);
                System.out.println("horaDiaHoy -------------- " + horaDiaHoy);
                //drop view
                stmt.execute("DROP VIEW temp_today_hour");
                System.out.println("Executing ----------- DROP VIEW temp_today_hour");
                //create view
                stmt.execute("CREATE VIEW temp_today_hour AS select distinct p.id as idSensor, p.temperature as grados from scsSensoresTemp p where p.tags = 'temp' and p.requesttime='"+horaDiaHoy+"'");
                System.out.println("Executing ----------- CREATE VIEW temp_today_hour AS select distinct p.id as idSensor, p.temperature as grados from scsSensoresTemp p where p.tags = 'temp' and p.requesttime='"+horaDiaHoy+"'");
                //Get media
                ResultSet resTD = stmt.executeQuery("select avg(grados) as mediaGeneral from temp_today_hour");
                System.out.println("Executing ----------- select avg(grados) as mediaGeneral from temp_today_hour");
                if(resTD==null || resTD.wasNull()){
                    todayFromZero =  todayFromZero.plusMinutes(30);
                    continue;
                }
                while (resTD.next()) {
                    System.out.println("horaDiaHoy -------------- " + horaDiaHoy);
                    System.out.println(resTD.getFloat(1) + "-----------");
                    float floatInMedia = resTD.getFloat(1);
                    valuesToday.add(floatInMedia);
                }
                todayFromZero =  todayFromZero.plusMinutes(30);
            }
            nuevoToday.setMedias24(valuesToday.toString());
            nuevoToday.setFecha(Hoy);

            if(insertMediasTodayRegisterInMysql(nuevoToday)){
                System.out.println("MYSQL INSERCION MEDIDAS MEDIAS DEL DIA: "+diaHoy+" CORRECTA");
            }else{
                System.out.println("MYSQL INSERCION MEDIDAS MEDIAS DEL DIA: "+diaHoy+" ERRONEA");
            }

        }catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }


    }

    /**
     * Insertamos todas registro de medias de hoy con:
     *  1.Media temperatura del dia de hoy
     *  2.Medias de cada media hora del dia en un Array
     * @return boolean with true or false incfunction of the resutl of the operation
     */
    public static boolean insertMediasTodayRegisterInMysql(RegisterMediasDiaMysql nuevoRegistro){
        try
        {
            // create a mysql database connection
            String myDriver = "org.gjt.mm.mysql.Driver";
            String myUrl = "jdbc:mysql://localhost/scsantandersmart";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "root", "cloudera");

            // the mysql insert statement
            String query = " insert into medias_sensor (sensor, dia_medicion, media_dia, medias_24)"
                    + " values (?, ?, ?, ?)";

            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = (PreparedStatement) conn.prepareStatement(query);
            preparedStmt.setString(1, nuevoRegistro.getMedida());
            preparedStmt.setString(2, nuevoRegistro.getFecha());
            preparedStmt.setFloat(3, nuevoRegistro.getMedia());
            preparedStmt.setString(4, nuevoRegistro.getMedias24());


            // execute the preparedstatement
            preparedStmt.execute();


            conn.close();
        }
        catch (Exception e)
        {
            System.err.println("Got an exception!");
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }
    /**
     * Insertamos todas las temperaturas del dia de hoy y sus horas
     * @return boolean with true or false incfunction of the resutl of the operation
     */
    public static boolean insertAllRegisterInMysql(ArrayList<RegisterFullMysql> nuevosRegistros){
        try
        {
            // create a mysql database connection
            String myDriver = "org.gjt.mm.mysql.Driver";
            String myUrl = "jdbc:mysql://localhost/scsantandersmart";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "root", "cloudera");

            //Truncate tabla para cargar solo el dia de hoy
            String truncate = "TRUNCATE TABLE temperatures_sensor";
            PreparedStatement truncateStmt = (PreparedStatement) conn.prepareStatement(truncate);
            truncateStmt.execute();

            for (RegisterFullMysql p:nuevosRegistros) {

                // the mysql insert statement
                String query = " insert into temperatures_sensor (id_sensor, dia_medicion, temperatura)"
                        + " values (?, ?, ?)";

                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt = (PreparedStatement) conn.prepareStatement(query);
                preparedStmt.setString(1, p.getId());
                preparedStmt.setString(2, p.getFecha());
                preparedStmt.setFloat(3, p.getTemp());

                // execute the preparedstatement
                preparedStmt.execute();
            }
            conn.close();

        }
        catch (Exception e)
        {
            System.err.println("Got an exception!");
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }
}