package smartcitysantander.realtime.utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * Clase que maneja como obtener los valores encapsulados en un json devuelto por el sensor de la ciudad
 *
 * @author Carlos Benito Palomares Campos
 *
 */
public class ExtractorMedidas {

	// Coleccion para meter cada un a de las expresion en funcion edel objeto recuperado
	protected Map<String,String> expresionRegularMedidas;
	
	protected Pattern realPattern = Pattern.compile("(-?[\\d\\.]*)");

	/**
	 * Constructor donde setteamos 2 patrones comunes que vemos que aparecen en cada uno de los sensores
	 */
	public ExtractorMedidas(){
		expresionRegularMedidas = new HashMap<String,String>();
		
		// Medidas comunes
		expresionRegularMedidas.put("last-update", ".*Last update: ([^<]*)");
		expresionRegularMedidas.put("battery-level", ".*Battery level: ([^<]*)");
	}

	/**
	 *Obtiene un hashmap con las medidas establecidas para el sensor que estamos midiendo
	 *
	 * @author Carlos Benito Palomares Campos
	 *
	 */
	public HashMap<String,Double> getArrayMedidas(JSONObject jsonContent){
		HashMap<String,Double> i = new HashMap<String,Double>();
		return i;
	}
	
	/**
	 * Nos da un un Array con las medidas que hemos recuperado del objecto JSON enviado en el string
	 * 
	 * @author Carlos Benito Palomares Campos
	 *
	 */
	public JSONArray getJSONMedidas(String content){
		JSONArray ja = new JSONArray();
		
		for(Entry<String, String> medidas : expresionRegularMedidas.entrySet()){
			Pattern r = Pattern.compile(medidas.getValue());
			Matcher m = r.matcher(content);
			if(m.find()){
				JSONObject jsonMedidas = new JSONObject();
				jsonMedidas.put(medidas.getKey(), m.group(1));
				ja.add(jsonMedidas);
			}
		}
		System.out.println("JA  en getJSONMeasures : " + ja.toString());
		return ja;
	}


	/**
	 * Extrae el patron dado desde un valor localizado en el json y lo almacena a un hashmap si es que lo encuentra
	 *
	 * @author Carlos Benito Palomares Campos
	 * @param measure Medida a encontrar dentro del json
	 * @param jsonContent Json o contenido que enviamos, donde buscaremos
	 * @param pattern patron a seguir para identificar la medida
	 * @param i hashmap donde devolveremos el valor
	 */
	public void rellenaMedidas(String measure, JSONObject jsonContent, Pattern pattern, HashMap<String,Double> i){
		String tem = (String)jsonContent.get(measure);
		if(tem != null){
			Matcher tM = pattern.matcher(tem);
			if(tM.find() && !tM.group(1).isEmpty()){
				try{
					i.put(measure, Double.valueOf(tM.group(1)));
				}catch(NumberFormatException e){
					e.printStackTrace();
				}
			}
		}
	}
}
