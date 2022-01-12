package name.mlopatkin.andlogview.liblogcat;

import org.junit.Test;

import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.Locale;

import static name.mlopatkin.andlogview.liblogcat.LogRecordUtils.withTime;
import static name.mlopatkin.andlogview.liblogcat.TimeFormatUtils.SYSTEM_LOGCAT_DATE_FORMATTER;
import static org.junit.Assert.*;

public class TimeFormatUtilsTest {
    @Test
    public void getTime() throws ParseException {

        //DateTimeFormatter dtf = SYSTEM_LOGCAT_DATE_FORMATTER.get();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        String s = "01-12 17:15:36.178";
        String parsableDateTimeString = LocalDateTime.now(ZoneId.systemDefault()).getYear() + "-" + s;

        LocalDateTime ldt = LocalDateTime.parse(parsableDateTimeString, dtf);

        assertEquals(DayOfWeek.WEDNESDAY, ldt.getDayOfWeek());
        assertEquals(17, ldt.getHour());

        //ateTimeFormatterBuilder builder = new DateTimeFormatterBuilder()

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("EEE d MMM, HH:mm:ss.SSS", Locale.ENGLISH);
        String formatted = ldt.format(outputFormatter);
        assertEquals("Wed 12 Jan, 17:15:36.178", formatted);

        //ZoneId zone = ZoneId.of("America/Los_Angeles");


        //try {
        //    LocalDateTime ldt = TimeFormatUtils.getLocalDateTimeFromString("2022-01-12 16:21:39.731");
        //} catch (java.text.ParseException e) {
        //    throw new AssertionError("error", e);
        //}

        //assertEquals("BASE < later", -1, BASE.compareTo(later));
        //assertEquals("later < BASE", 1, later.compareTo(BASE));
    }
}
