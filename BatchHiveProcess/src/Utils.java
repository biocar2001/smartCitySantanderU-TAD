import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cloudera on 1/2/16.
 */
public class Utils {

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
    public String formaterDate(DateTime tt){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date c = null;
        try {
            c = sdf.parse(tt.toString());
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        String devuelto = output.format(c);
        return devuelto;
    }
}
