package ir.stts.bajet.core.date;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.ULocale;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class DateManager {

    private static final ULocale PERSIAN_LOCALE = new ULocale("fa_IR@calendar=persian");
    private static final ULocale PERSIAN_EN_LOCALE = new ULocale("en@calendar=persian");

    public Date dateFromString(String value, String pattern) throws ParseException {

        DateFormat dateFormat = new SimpleDateFormat(pattern, ULocale.getDefault());
        return dateFormat.parse(value);
    }

    public LocalDateTime localDateTimeFromString(String value, String pattern) throws ParseException {

        Date date = dateFromString(value, pattern);
        return date2LocalDateTime(date);
    }

    public LocalDate localDateFromString(String value, String pattern) throws ParseException {

        Date date = dateFromString(value, pattern);
        return date2LocalDate(date);
    }

    public LocalDateTime date2LocalDateTime(Date value) {

        return value.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public LocalDate date2LocalDate(Date value) {

        return value.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }


    public Date localDateTime2Date(LocalDateTime value) {

        return Date.from(value.atZone(ZoneId.systemDefault()).toInstant());
    }

    public Date localDate2Date(LocalDate value) {

        return Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public String date2PersianDateString(Date value, String pattern) {

        return new SimpleDateFormat(pattern, PERSIAN_EN_LOCALE).format(value);
    }

    public Date persianDateString2Date(String value, String pattern) throws ParseException {

        return new SimpleDateFormat(pattern, PERSIAN_EN_LOCALE).parse(value);
    }

    public String localDateTime2PersianDateString(LocalDateTime value, String pattern) {

        Date date = localDateTime2Date(value);
        return new SimpleDateFormat(pattern, PERSIAN_EN_LOCALE).format(date);
    }

    public String localDate2PersianDateString(LocalDate value, String pattern) {

        Date date = localDate2Date(value);
        return new SimpleDateFormat(pattern, PERSIAN_EN_LOCALE).format(date);
    }

    public LocalDateTime persianDateString2LocalDateTime(String value, String pattern) throws ParseException {

        Date date = new SimpleDateFormat(pattern, PERSIAN_EN_LOCALE).parse(value);
        return date2LocalDateTime(date);
    }

    public LocalDate persianDateString2LocalDate(String value, String pattern) throws ParseException {

        Date date = new SimpleDateFormat(pattern, PERSIAN_EN_LOCALE).parse(value);
        return date2LocalDate(date);
    }
}