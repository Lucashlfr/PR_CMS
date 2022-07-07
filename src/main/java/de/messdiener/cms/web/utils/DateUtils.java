package de.messdiener.cms.web.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public enum DateType{
        ENGLISH("yyyy-MM-dd"),
        GERMAN("dd.MM.yyyy"),
        GERMAN_WITH_TIME("dd.MM.yyyy HH:mm"),
        GERMAN_WITH_DAY_NAME("EEEE, dd.MM.yyyy");

        private final String pattern;

        DateType(String pattern){
            this.pattern = pattern;
        }

        public String getPattern() {
            return pattern;
        }
    }

    public static String convertLongToDate(long millis, DateType dateType){
        Date date = new Date(millis);
        DateFormat dateFormat = new SimpleDateFormat(dateType.getPattern());
        return dateFormat.format(date);
    }

    public static long convertDateToLong(String date, DateType dateType) {
        DateFormat dateFormat = new SimpleDateFormat(dateType.getPattern());

        try {
            return dateFormat.parse(date).getTime();
        } catch (ParseException e) {
            return -1;
        }
    }

}
