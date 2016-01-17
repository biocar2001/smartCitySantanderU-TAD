package sscBatchProceso;

import java.text.SimpleDateFormat;
import java.util.*;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by Carlos Benito Palomares Campos.
 */
public class UtilsBatchProceso{

    private HashMap<String, Integer> sensorStatus = new HashMap<String, Integer> ();
    private DateTime dt = DateTime.now().toDateTimeISO();

    /*
    * Metodo que nos devuelve el dia y la hora correspondiente a la media hora que corresponda
    *
    * */
    public String getDateHalfHour(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = null;
        try {
            d = sdf.parse(dt.toString());
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        String formattedTime = output.format(d);


        DateTimeFormatter dateParser = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

        DateTime date = dateParser.parseDateTime(formattedTime);
        DateTime ndate = date.plusSeconds(date.getSecondOfMinute() * -1).plusMinutes((date.getMinuteOfHour() % 30) * -1);

        Date c = null;
        try {
            c = sdf.parse(ndate.toString());
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        String devuelto = output.format(c);
        return devuelto;
    }

    public String formatToJSON(String content) {
        ArrayList<String> sensorsData = new ArrayList<String>();

        JSONParser jparser = new JSONParser();
        JSONObject jsonData = null;

        try {
            jsonData = (JSONObject) jparser.parse(content);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONArray markers = (JSONArray) jsonData.get("markers");

        // Prepares the iteration for each sensor
        Iterator<JSONObject> iterator = markers.iterator();
        int i = 0;
        while (iterator.hasNext()) {

            JSONObject sensorData = iterator.next();
            // Obtenemos valores caracteristicos del sensor
            String tag = (String) sensorData.get("tags");
            String title = (String) sensorData.get("title");
            String sensorContent = (String) sensorData.get("content")
                    + (String) sensorData.get("image");
            int contentHash = sensorContent.hashCode();

            // Comprobamos si la medicion es la misma o ha cambiado de valor
            if (sensorStatus.containsKey(title)
                    && sensorStatus.get(title).equals(contentHash)) {
            } else {

                // Actualizamos el hashcontent, hay entradas nuevas
                sensorStatus.put(title, contentHash);
                ExtractorMedidas me = MedidasExtraidasFactory.getExtractorMedidasr(tag);
                if (me == null) {
                    // No hacemos nada, es el mismo sensor con los mismos valores
                } else {

                    // Extraccion de las medidas
                    JSONArray newContent = me.getJSONMedidas(sensorContent);
                    Iterator<JSONObject> it = newContent.iterator();

                    // para cada medida escribimos su valor en un JSON Array Object
                    while (it.hasNext()) {
                        sensorData.putAll(it.next());
                    }
                    sensorData.remove("content");
                    sensorData.remove("image");
                    sensorData.remove("last-update");
                    sensorData.remove("battery-level");

                    /*Obtenemos momento de la llamada para detectar franja de media hora en la que se registra la medida*/
                    String ndate = getDateHalfHour();

                    sensorData.put("requesttime", ndate);
                    // Obtenemos las medidas que corresponden al sensor que ha llegado el json
                    HashMap<String, Double> measures = MedidasExtraidasFactory.getExtractorMedidasr(tag).getArrayMedidas(sensorData);
                    for(Map.Entry<String, Double> measure : measures.entrySet()){
                        sensorData.put(measure.getKey().toString(),measure.getValue().toString());
                    }
                    //Ignoramos aquellos sensores de temperatura que no marcan temperatura
                    if(tag.equals("temp") && !sensorData.get("temperature").toString().equals(" ºC")){
                        sensorsData.add(sensorData.toJSONString());
                    }

                }
            }
        }

        // Returns the JSON object
        String finalData = "";
        for (String sensor : sensorsData) {
            if (finalData.length() > 0) {
                finalData = finalData + "\n";
            }
            finalData = finalData + sensor;
        }
        return finalData;
    }


}
