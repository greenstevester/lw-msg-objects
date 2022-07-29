package net.greensill.lw.common;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Common {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("UTC"));

    public static final SimpleDateFormat DEFAULT_SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

    public static String getFormattedInstant(Instant instant) {
        return DATE_TIME_FORMATTER.format(instant);
    }

}
