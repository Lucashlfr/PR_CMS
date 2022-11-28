package de.messdiener.cms.web.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public enum MonthNumberName {
        JANUAR(1, "Januar"),
        FEBRUARY(2, "Februar"),
        MARCH(3, "März"),
        APRIL(4, "April"),
        MAY(5, "Mai"),
        JUNE(6, "Juni"),
        JULY(7, "Juli"),
        AUGUST(8, "August"),
        SEPTEMBER(9, "September"),
        OCTOBER(10, "Oktober"),
        NOVEMBER(11, "November"),
        DECEMBER(12, "Dezember");

        private final int number;
        private final String name;

        MonthNumberName(int number, String name) {
            this.number = number;
            this.name = name;
        }

        public int getNumber() {
            return number;
        }

        public String getName() {
            return name;
        }
    }

    public enum DateType{
        ENGLISH("yyyy-MM-dd"),
        GERMAN("dd.MM.yyyy"),
        GERMAN_WITH_TIME("dd.MM.yyyy HH:mm"),
        GERMAN_WITH_DAY_NAME("EEEE, dd.MM.yyyy"),
        ENGLISH_DATETIME("yyyy-MM-dd'T'HH:mm");

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

    public static MonthNumberName getMonthNumberName(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int month = cal.get(Calendar.MONTH)+1;

        for (MonthNumberName v: MonthNumberName.values()) {
            if(v.getNumber() == month)
                return v;
        }
        return null;
    }

    public static int getCurrentMonthNumber(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(System.currentTimeMillis()));

        return cal.get(Calendar.MONTH)+1;
    }

    public static int getAge(long birthday) {
        LocalDate birthdayDate = createLocalDate(birthday);
        LocalDate current = createLocalDate(System.currentTimeMillis());

        return Period.between(birthdayDate, current).getYears();

    }

    private static LocalDate createLocalDate(long millis){
        String[] bArray = convertLongToDate(millis, DateType.ENGLISH).split("-");
        int[] iArray = new int[bArray.length];
        for (int i = 0; i < bArray.length; i++) {
            iArray[i] = Integer.parseInt(bArray[i]);
        }
        return LocalDate.of(iArray[0], iArray[1], iArray[2]);
    }


}
