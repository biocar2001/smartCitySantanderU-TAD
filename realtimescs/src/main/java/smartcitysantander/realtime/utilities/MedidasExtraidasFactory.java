package smartcitysantander.realtime.utilities;



/**
 * Description: Clases factory que nos da el tipo de objeto que encapsula la informacion del sensor detectado en el json
 * @author Carlos Benito Palomares Campos
 *
 */
public class MedidasExtraidasFactory {

	
	
	public static ExtractorMedidas getExtractorMedidasr(String tag) {
			
			if(tag==null){
				return null;
			}
			if(tag.equals("temp")){
				return new MedicionTemperatura();
			}else{
				System.out.println("tag " + tag + " no registrado.");
				return null;
			}
				
	
	}

}
