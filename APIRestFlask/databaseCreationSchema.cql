CREATE KEYSPACE scsutad WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 } ;

USE scsutad;

CREATE TABLE scs_medidas_agr (
   measure text,
   time text,
   tag text,
   latitude text,
   longitude text,
   value double,
   PRIMARY KEY((measure, time), latitude, longitude)
);
