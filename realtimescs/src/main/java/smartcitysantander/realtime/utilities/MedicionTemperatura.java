package smartcitysantander.realtime.utilities;

import java.util.HashMap;

import org.json.simple.JSONObject;

/**
 * Calse wrapper para extraer la informacion de sensores de temperatura
 * @author Carlos Benito Palomares Campos
 *
 */
public class MedicionTemperatura extends ExtractorMedidas {

	public MedicionTemperatura() {
		super();
		expresionRegularMedidas.put("temperature", ".*Temperature: ([^<]*)");
	}

	@Override
	public HashMap<String, Double> getArrayMedidas(JSONObject jsonContent) {
		HashMap<String,Double> i = new HashMap<String,Double>();
		
		rellenaMedidas("temperature", jsonContent, realPattern, i);

		return i;
	}

}
