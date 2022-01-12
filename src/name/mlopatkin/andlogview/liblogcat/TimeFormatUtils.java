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

    private static final ThreadLocal<DateFormat> SYSTEM_LOGCAT_DATE_FORMAT =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("MM-dd HH:mm:ss.SSS"));

    private static final ThreadLocal<DateTimeFormatter> My_SYSTEM_LOGCAT_DATE_FORMAT =
            ThreadLocal.withInitial(() -> DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.US));


    private static final ThreadLocal<DateFormat> MY_LOGCAT_DATE_FORMAT =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("EEE d MMM, HH:mm:ss.SSS"));


    private static final ThreadLocal<DateTimeFormatter> MY_LOGCAT_DATE_FORMATTER =
            ThreadLocal.withInitial(() -> DateTimeFormatter.ofPattern("EEE d MMM, HH:mm:ss.SSS", Locale.ENGLISH));


    public static LocalDateTime getTimeFromString(String s) throws ParseException {
        DateTimeFormatter inputFormatting = My_SYSTEM_LOGCAT_DATE_FORMAT.get();
        String parsableDateTimeString = LocalDateTime.now(ZoneId.systemDefault()).getYear() + "-" + s;
        LocalDateTime ldt = LocalDateTime.parse(parsableDateTimeString, inputFormatting);
        return ldt;
    }



    public static String convertTimeToString(LocalDateTime date) {
        return date.format(MY_LOGCAT_DATE_FORMATTER.get());
    }
}
