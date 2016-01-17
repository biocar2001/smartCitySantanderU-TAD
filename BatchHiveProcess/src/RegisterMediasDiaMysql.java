/**
 * Created by Carlos Benito Palomares Campos
 */
public class RegisterMediasDiaMysql {
    private String fecha;
    private String medida;
    private String medias24;
    private Float media;

    public RegisterMediasDiaMysql(String medida) {
        this.medida = medida;
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

    public String getMedias24() {
        return medias24;
    }

    public void setMedias24(String medias24) {
        this.medias24 = medias24;
    }

    public Float getMedia() {
        return media;
    }

    public void setMedia(Float media) {
        this.media = media;
    }
}
