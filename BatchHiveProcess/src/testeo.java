import org.joda.time.DateTime;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by cloudera on 1/3/16.
 */
public class testeo {

    public static void main(String[] args) throws SQLException {
        try {
            DateTime today = new DateTime().withTimeAtStartOfDay();
            DateTime tomorrow = today.plusMinutes(30);
            System.out.println("Ptoday " + today.toString());


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date c = null;
            try {
                c = sdf.parse(tomorrow.toString());
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            String devuelto = output.format(c);
            System.out.println("Ptomorrow " + devuelto);

            if (today.isBefore(tomorrow)){
                System.out.println("ECHO");
            }


            ArrayList<Float> valuesToday = new ArrayList<Float>();
            valuesToday.add(new Float(0.2));
            valuesToday.add(new Float(0.2));
            valuesToday.add(new Float(0.2));
            valuesToday.add(new Float(0.2));
            valuesToday.add(new Float(0.2));
            valuesToday.add(new Float(0.2));
            valuesToday.add(new Float(0.2));
            valuesToday.add(new Float(0.2));
            valuesToday.add(new Float(0.2));
            valuesToday.add(new Float(0.2));
            valuesToday.add(new Float(0.2));
            valuesToday.add(new Float(0.2));
            System.out.println("Array float to string " + valuesToday.toString());
        } catch (Exception e) {

        }
    }
}
