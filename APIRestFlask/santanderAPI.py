from flask import Flask, render_template, request, redirect, url_for
from flask_restful import Resource, Api
from flask_restful import reqparse
from flask_sqlalchemy import SQLAlchemy
from flask_restful.representations.json import output_json
from datetime import datetime
from flask.ext.cors import CORS
import yaml, json
from cassandra.cluster import Cluster

app = Flask(__name__)

app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://root:cloudera@localhost/scsantandersmart'
app.config['SQLALCHEMY_ECHO'] = True
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = True
db = SQLAlchemy(app)
api = Api(app)
CORS(app)
'''
Funcion para el formateo de las diferentes temperaturas registradas en base de datos
'''
def getLisJsonFloats(lista):
    listaFinal = []
    listaSinParentesis = lista[1:-2]
    
    arrayWithFloats = listaSinParentesis.split(",")
    for i in arrayWithFloats:
        if i.strip() is not "":
            newFloat = format(float(i),'.1f')
            listaFinal.append(newFloat)
    
    return listaFinal

''' Clases del modelo de base de datos - Utilizadas por SQLAlchemy'''
class TemperaturesSensor(db.Model):
    id_sensor = db.Column(db.String(20), unique=True, primary_key=True)
    dia_medicion = db.Column(db.DateTime, unique=True, primary_key=True)
    temperatura = db.Column(db.Float, unique=True)

    def __init__(self, id_sensor, diamedicion):
        self.id_sensor = idsensor
        self.dia_medicion = diamedicion

class MediasSensor(db.Model):
    sensor = db.Column(db.String(500), unique=True, primary_key=True)
    dia_medicion = db.Column(db.Date, unique=True, primary_key=True)
    media_dia = db.Column(db.Float, unique=True)
    medias_24 = db.Column(db.String(500), unique=True)

    def __init__(self, id_sensor, diamedicion):
        self.sensor = idsensor
        self.dia_medicion = diamedicion

''' Clases y consultas del API
Devuelve los datos de un sensor por su id '''
class TemperaturesSensorIdNode(Resource):
    
    def get(self,id):
        try:
            post = TemperaturesSensor.query.filter_by(id_sensor=id).first()
            i = {
                'id_sensor':post.id_sensor,
                'dia_medicion':post.dia_medicion,
                'temperatura':post.temperatura
            }
            return {'element':str(i)}
        except Exception as e:
            print "Exception no controlada en TemperaturesSensorIdNode"
            return {'error': str(e)}
'''
Devuelve toda la info de la tabla donde se registran todas las mediciones de temperatura de los sensores de temperatura
'''
class TemperaturesSensorAll(Resource):
    
    def get(self):
        try:
            data = TemperaturesSensor.query.all()
            items_list=[];
            for item in data:
                i = {
                    "id_sensor":item.id_sensor,
                    "dia_medicion":item.dia_medicion,
                    "temperatura":item.temperatura
                }
                items_list.append(i)
            return {'elements':str(items_list)}
        except Exception as e:
            print "Exception no controlada en TemperaturesSensorAll"
            return {'error': str(e)}
'''
Devuelve las medias de temepratura de cada hora, junto con la media general de un dia en concreto especificado
'''
class MediasSensorFechaDia(Resource):
    def get(self,fecha):
        try:

            post34= MediasSensor.query.filter_by(dia_medicion = fecha).first()
            if post34 is not None:
                i = {
                    "sensor":post34.sensor,
                    "dia_medicion":str(post34.dia_medicion),
                    "media_dia":format(post34.media_dia,'.1f'),
                    "medias_24":getLisJsonFloats(post34.medias_24)
                }
		
                
                response = api.make_response(i,200)
                response.headers['content-type'] = 'application/json'
                return response
            else:
                return {'element':'No hay elementos para esa fecha'}
        except Exception as e:
            print "Exception no controlada en MediasSensorFechaDia"
            return {'error': str(e)}
'''
Devuelve las medias de temperatura de cada hora, junto con la posicion del sensor en un mapa desde cassandra
'''
class MediasSensorFechaDiaHoraCassandra(Resource):
    def get(self,fecha):
        try:
		print fecha	
		listaDevuelta = []
		query = "SELECT * FROM scs_medidas_agr where measure='temperature' and time = '" + fecha +"'"
		print query
		cluster = Cluster()
		session = cluster.connect('scsutad')
            	rows= session.execute(query)
		if len(rows.current_rows) < 1:
			return {'element':'No hay elementos para esa fecha'}
            	for user_row in rows:
			i = {
				"sensor":user_row.measure,
				"latitude":format(float(user_row.latitude)),
				"longitude":format(float(user_row.longitude)),
				"media": user_row.value
			}
			listaDevuelta.append(i)
                
		response = api.make_response(listaDevuelta,200)
		response.headers['content-type'] = 'application/json'
		return response
         
        except Exception as e:
            print "Exception no controlada en MediasSensorFechaDia"
            return {'error': str(e)}

'''
Devuelve todas las medias registradas en la tabla
'''
class MediasSensorAll(Resource):
    
    def get(self):
        try:
            data = MediasSensor.query.all()
            items_list=[];
            for item in data:
                i = {
                'sensor':item.sensor,
                'dia_medicion':item.dia_medicion,
                'media_dia':item.media_dia,
                'medias_24':getLisJsonFloats(item.medias_24)
                }
                items_list.append(i)
            
            return {'elements':str(items_list)}
        except Exception as e:
            print "Exception no controlada en MediasSensorAll"
            return {'error': str(e)}

''' URLS y consultas del API'''
api.add_resource(MediasSensorFechaDiaHoraCassandra, '/MediasSensorFechaDiaHoraCassandra/<fecha>')
api.add_resource(MediasSensorFechaDia, '/MediasSensorFechaDia/<fecha>')
api.add_resource(MediasSensorAll, '/MediasSensorAll')

api.add_resource(TemperaturesSensorIdNode, '/TemperaturesSensorIdNode/<id>')
api.add_resource(TemperaturesSensorAll, '/TemperaturesSensorAll')

if __name__ == '__main__':
    app.run(host='localhost', port=5030)
    app.run(debug=True)

