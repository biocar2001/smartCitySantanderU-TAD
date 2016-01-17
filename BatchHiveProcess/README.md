# smartCityProjectUTad

Se trata del proceso Batch principal el cual mediante el cliente de hive realiza todo el tratamiento de los datos y los inserta en mysql.
Como particularidad de este proceso he de indicar lo siguiente:

La Clase principal Java que conecta con el database de los sensores smartcities(), obteniendo los parametros necesarios
e inserta en una tabla mysql para ser consumida a posteriori por nuestra api rest.

El proyecto no es un maven project debido a la version de la virtual machine de cloudera(CDH5) y la version de hive(Hive version 0.12)
que hacen que se produzcan errores de dependencias los cuales fueron solucionados en versiones posteriores.

Debido a la falta de tiempos para este proyecto obtamos por la inclusion de las librerias necesarias mediante el classpath de la maquina virtual;
Bien desde eclipse, inteliJ o cualqueir otro IDE o seteandolas en nuestro classpath:
Add /usr/lib/hive/lib/*.jar and /usr/lib/hadoop/*.jar to your classpath.

Tal y como es explicado en la web oficial de cloudera a este respecto:
http://www.cloudera.com/content/www/en-us/documentation/enterprise/latest/topics/cdh_ig_hive_jdbc_install.html

Para probarlo deberemos realizar unas cuantas tareas previamente:

1º Instalar mysql

2º Crear el database y tablas correspondientes: lanzar lo que esta en mysql.txt

3º Obtener el jar necesario para el serde json de hive:
wget http://www.congiu.net/hive-json-serde/1.3/cdh5/json-serde-1.3-jar-with-dependencies.jar

4º Crear en hive el database y la tabla correspondiente:
create database scsantander;
use scsantander;
Creo la tabla con los campos que tiene el json:
=================================================
CREATE TABLE scsSensoresTemp (
 tags string,
 id string,
 requesttime string,
 title string,
 longitude string,
 latitude string,
 temperature string
 )
 ROW FORMAT SERDE 'org.openx.data.jsonserde.JsonSerDe'
 LOCATION '/user/cloudera/tablas_sensores';

5º Lanzar el proceso correspondiente de la clase principal: HiveMysqlMain.java, habiendo añadido previamente los jars correspondientes
 en el classpath. Que tambien incluyo en el respositorio de manera comprimida.
 
 