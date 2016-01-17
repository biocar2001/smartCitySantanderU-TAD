# smartCityProjectUTad
Proyecto fin de master experto en big data por la universidad U-tad sobre la monitorizacion de sensores en una smart city.

http://carlospalomares.weebly.com/

En este proyecto se ha desarrollado dos sistemas de analisis de datos extraidos de sensores colocados en la ciudad de Santander que se publican a través de una interfaz web(Web Service):
"http://maps.smartsantander.eu/getdata.php"
para un posterior tratamiento utilizando tecnologías Big Data.

Como resultado se ha construido 2 almacenes de datos, uno con Mysql donde me he centrado en el análisis de la temperatura de la ciudad, sacando la
temperatura media de la ciudad cada 30 minutos en base a los datos de todos los sensores, así como la media del día, pudiendo ser visualizados
estos datos a posterior mediante interface gráfica o consultada la información de los sensores de temperatura de un dia concreto, ya que al final 
del proceso se obtiene una tabla con todas las mediciones del dia de ayer a fin de hacer analisis mas claros por parte de un departamento de BI
o usuarios finales.
 
También se ha desarrollado  un sistema de streaming, de manera que cada 30 minutos se vuelca la informacion de cada uno de los sensores a una tabla
en cassandra, guardando la posicion exacta del sensor(Latitud y Longitud) así como la temperatura media del mismo.
De esta manera se podría representar esta información en un mapa con la ayuda de mapbox o cualquier otro sistema que eligiéramos.

En este reporsitorio podras encontrar cada uno de los proyectos utilizados para las diferentes funcionalidades dentro del sistema, asi como una explicacion
detallada de los pasos a realizar en caso de querer reproducirlo en tu equipo o cluster. Los diferentes proyectos que podras encontrar son:

SISTEMA BATCH
=============
1.
2.
3.
4.

SISTEMA STREAMING
=================
1.
2.



