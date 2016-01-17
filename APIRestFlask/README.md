# smartCityProjectUTad
API REST desarrollada con el framework de python flask-api-rest.

Mediante el presente API podremos recuperar tanto la informacion almacenada en nuestra base de datos MYSQL, como aquella que esta
almacenada en nuestra base de datos cassandra.

Para arrancar el servidor del API y ejecutar el codigo aconsejo los siguientes pasos:

- Instalar Python
- Instalar pip
- sudo pip install -r requirements.txt(Fichero con las dependencias necesarias de python)
- Arancar el servicio: python santanderAPI.py

Pudiera ser que encontreis algun error de dependencias, ya que a veces pip no funciona como quisieramos, en ese caso todas las dependencias estan dentro del archivo requeriments.txt, y podriamos ir instalandolas una por una con pip.

Para arrancar este API y visualizar los resultados de una manera rapida aconsejo, crear las base de datos correspondientes de manera manual,y una insercion de datos en las mismas a modo fake, para verificar el correcto fucnionamiento del API.

1. Base de datos MYSQL:
- Instalar mysql
- Entrar en mysql: mysql -u root -p
- Crear el database correspondiente: 
	
- CREATE DATABASE scsantandersmart;
	
- Creacion de tablas(Esta operacion sera necesaria para el proceo Batch):
	
- CREATE TABLE temperatures_sensor (
	  id_sensor varchar(20) NOT NULL,
	  dia_medicion datetime NOT NULL,
	  temperatura float NOT NULL,
	  PRIMARY KEY (id_sensor,dia_medicion)
	);
	
- CREATE TABLE medias_sensor (
	  sensor varchar(500) NOT NULL,
	  dia_medicion date NOT NULL,
	  media_dia float NOT NULL,
	  medias_24 varchar(1000) NOT NULL,
	  PRIMARY KEY (sensor,dia_medicion)
	);
	
- Inserts de prueba:
		insert into medias_sensor (sensor, dia_medicion, media_dia, medias_24) values ("temperature", '2016-01-05', 18.3013 , "[17.215637,17.215637,17.215637,18.215637,18.215637,17.215637,16.888546,16.884363,24.215637,14.215637,12.215637,14.215637,14.215637,34.215637,14.215637,14.215637,24.215637,14.215637,14.215637,16.15637,11.215637,11.215637,11.215637,11.215637,11.215637,11.215637,11.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,]");

		insert into medias_sensor (sensor, dia_medicion, media_dia, medias_24) values ("temperature", '2016-01-06', 28.3013 , "[17.215637,17.215637,17.215637,18.215637,18.215637,17.215637,20.888546,16.884363,24.215637,14.215637,13.215637,14.215637,17.215637,34.215637,14.215637,14.215637,24.215637,14.215637,14.215637,16.15637,11.215637,11.215637,11.215637,11.215637,11.215637,11.215637,11.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,]");

		insert into medias_sensor (sensor, dia_medicion, media_dia, medias_24) values ("temperature", '2016-01-07', 15.3013 , "[17.215637,17.215637,17.215637,18.215637,18.215637,17.215637,19.888546,16.884363,24.215637,14.215637,12.215637,22.215637,14.215637,34.215637,14.215637,14.215637,24.215637,14.215637,14.215637,16.15637,11.215637,11.215637,11.215637,11.215637,11.215637,11.215637,11.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,]");

		insert into medias_sensor (sensor, dia_medicion, media_dia, medias_24) values ("temperature", '2016-01-08', 18.3013 , "[17.215637,17.215637,17.215637,18.215637,18.215637,17.215637,18.888546,16.884363,24.215637,14.215637,12.215637,14.215637,14.215637,34.215637,14.215637,14.215637,24.215637,14.215637,14.215637,16.15637,11.215637,11.215637,11.215637,11.215637,11.215637,11.215637,11.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,]");

		insert into medias_sensor (sensor, dia_medicion, media_dia, medias_24) values ("temperature", '2016-01-09', 16.3013 , "[17.215637,17.215637,17.215637,18.215637,18.215637,17.215637,15.888546,16.884363,24.215637,14.215637,12.215637,2.215637,14.215637,34.215637,14.215637,14.215637,24.215637,14.215637,14.215637,16.15637,11.215637,11.215637,11.215637,11.215637,11.215637,11.215637,11.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,]");

		insert into medias_sensor (sensor, dia_medicion, media_dia, medias_24) values ("temperature", '2016-01-10', 11.3013 , "[17.215637,17.215637,17.215637,18.215637,18.215637,17.215637,13.888546,16.884363,24.215637,14.215637,12.215637,1.215637,14.215637,34.215637,14.215637,14.215637,24.215637,14.215637,14.215637,16.15637,11.215637,11.215637,11.215637,11.215637,11.215637,11.215637,11.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,]");

		insert into medias_sensor (sensor, dia_medicion, media_dia, medias_24) values ("temperature", '2016-01-11', 10.3013 , "[17.215637,17.215637,17.215637,18.215637,18.215637,17.215637,12.888546,16.884363,24.215637,14.215637,12.215637,34.215637,14.215637,34.215637,14.215637,14.215637,24.215637,14.215637,14.215637,16.15637,11.215637,11.215637,11.215637,11.215637,11.215637,11.215637,11.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,8.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,6.215637,]");
	
- Un test una vez arrancado el servidor y tener datos en la tabla, seria ir al navegador y picar esta URL:
		http://localhost:5030/MediasSensorFechaDia/2016-01-05
		
2. Base de datos CASSANDRA:
	
- Lanzar el archivo de creacion de schema en cassandra databaseCreationSchema.cql
	
- Lanzar el insertCassandra.cql
	
- Un test una vez arrancado el servidor y tener datos en la tabla, seria ir al navegador y picar esta URL:
	http://localhost:5030/MediasSensorFechaDiaHoraCassandra/2016-01-05 2012:30:00
	
		
		
	
Toda la logica del API implementada esta en el archivo santanderAPI.py, donde hago uso de cassandra, mysql, SQLALchemy, etc,etc.



