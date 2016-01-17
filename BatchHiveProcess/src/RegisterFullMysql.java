/**
 * Bean para encapsular la onformacion referente a un registro de la tabla mysql
 */
public class RegisterFullMysql {

    private String Id;
    private String fecha;
    private String medida;
    private Float temp;

    public RegisterFullMysql(String medida) {
        this.medida = medida;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getMedida() {
        return medida;
    }

    public void setMedida(String medida) {
        this.medida = medida;
    }

    public Float getTemp() {
        return temp;
    }

    public void setTemp(Float temp) {
        this.temp = temp;
    }
}
