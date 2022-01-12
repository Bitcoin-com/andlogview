/*
 * Copyright 2011 Mikhail Lopatkin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package name.mlopatkin.andlogview.liblogcat;

import com.sun.tools.sjavac.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Thread-safe routines that perform reading and writing of the timestamps in
 * the logcat format.
 */
public class TimeFormatUtils {
    private TimeFormatUtils() {}

    public static final ThreadLocal<DateFormat> SYSTEM_LOGCAT_DATE_FORMAT =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("MM-dd HH:mm:ss.SSS"));

    public static final ThreadLocal<DateTimeFormatter> SYSTEM_LOGCAT_DATE_FORMATTER =
            ThreadLocal.withInitial(() -> DateTimeFormatter.ofPattern("MM-dd HH:mm:ss.SSS", Locale.US));


    private static final ThreadLocal<DateFormat> MY_LOGCAT_DATE_FORMAT =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("EEE d MMM, HH:mm:ss.SSS"));


    private static final ThreadLocal<DateTimeFormatter> MY_LOGCAT_DATE_FORMATTER =
            ThreadLocal.withInitial(() -> DateTimeFormatter.ofPattern("EEE d MMM, HH:mm:ss.SSS", Locale.ENGLISH));

    public static LocalDateTime getLocalDateTimeFromString(String s) throws ParseException {
        //Log.error("BJD Error");

        ZoneId zone = ZoneId.of("America/Los_Angeles");
        return LocalDateTime.now(zone);
    }

    public static Date getTimeFromString(String s) throws ParseException {
        //Log.error("BJD Error");


        DateFormat df = SYSTEM_LOGCAT_DATE_FORMAT.get();
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        //df.setTimeZone(TimeZone.getDefault());

        Date date = df.parse(s);
        return date;
    }

    /*
    public static Instant getTimeFromString(String s) throws ParseException {
        Log.error("BJD Error");


        DateFormat df = SYSTEM_LOGCAT_DATE_FORMAT.get();
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        //df.setTimeZone(TimeZone.getDefault());

        Date date = df.parse(s);



        //LocalDateTime ldt = LocalDateTime.parse(s);


        Instant instant = ldt.toInstant(ZoneId.of("Pacific/Auckland").getRules().getOffset(ldt));


        //LocalDateTime.of

        Instant instant = date.toInstant();

        //System.out.println();



        date.setTimeZone
        Instant instant = LocalDateTime.parse(
                s,
                SYSTEM_LOGCAT_DATE_FORMATTER.get()
        )
        .atZone(ZoneId.of("Pacific/Auckland"))
        .toInstant();


        return instant;
    }
    */

    /*
    public static String convertTimeToString(Instant instant) {
        return "test";
        /*
        ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());

        LocalDateTime ldt = LocalDateTime.from(zdt);
        return MY_LOGCAT_DATE_FORMATTER.get().format(ldt);
        *//*
    }
    */

    public static String convertTimeToString(Date date) {
        //Instant instant = date.toInstant();
        //ZoneId zone = ZoneId.systemDefault();
        //ZonedDateTime zDate = ZonedDateTime.ofInstant(instant, zone);
        return MY_LOGCAT_DATE_FORMAT.get().format(date);
    }

}
