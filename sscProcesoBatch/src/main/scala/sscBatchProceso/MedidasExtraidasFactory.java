package sscBatchProceso;

/**
 * Description: Clases factory que nos da el tipo de objeto que encapsuel la informacion del seesor detectado en el json
 * @author Carlos Benito Palomares Campos
 *
 */
public class MedidasExtraidasFactory {

    /**TODO: Implememntar un hashmap con string, new class para indentificar el objeto a devolver como alternativa al select case**/
    //Map<Class<?>, Object> values = new HashMap<>();

    public static ExtractorMedidas getExtractorMedidasr(String tag) {
        if(tag==null){
            System.out.println("tag NULL");
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