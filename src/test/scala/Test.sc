import java.text.SimpleDateFormat
import java.util.Calendar

val format = new SimpleDateFormat("dd-MMM-yyyy")
val date = Calendar.getInstance.getTime
format.format(date)