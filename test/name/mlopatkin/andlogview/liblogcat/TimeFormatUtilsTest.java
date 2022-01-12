package name.mlopatkin.andlogview.liblogcat;

import org.junit.Test;

import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


import static org.junit.Assert.*;

public class TimeFormatUtilsTest {
    @Test
    public void parseAndFormatTime() throws ParseException {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        String s = "01-12 17:15:36.178";
        String parsableDateTimeString = LocalDateTime.now(ZoneId.systemDefault()).getYear() + "-" + s;

        LocalDateTime ldt = LocalDateTime.parse(parsableDateTimeString, dtf);

        assertEquals(DayOfWeek.WEDNESDAY, ldt.getDayOfWeek());
        assertEquals(17, ldt.getHour());


        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("EEE d MMM, HH:mm:ss.SSS", Locale.ENGLISH);
        String formatted = ldt.format(outputFormatter);
        assertEquals("Wed 12 Jan, 17:15:36.178", formatted);
    }
}
