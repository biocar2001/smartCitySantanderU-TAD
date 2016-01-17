package smartcitysantander.realtime.utilities;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
public class Tester {

	/**
	 * Clase desarrollada con la intencion de hacer testing rapidos de algunas funciones implementadas en los diferentes bolts que usamos para el proyecto
	 * @param args
	 */
	public static void main(String[] args) {
		DateTimeFormatter dateParser = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
	    DateTime date = dateParser.parseDateTime("2015-12-07T11:36:26.209264");
	    DateTime ndate = date.plusSeconds(date.getSecondOfMinute() * -1).plusMinutes((date.getMinuteOfHour() % 30) * -1);
	    System.out.println("HORA FIN: " + ndate.toString());

	}

}
