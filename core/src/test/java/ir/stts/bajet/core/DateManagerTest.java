package ir.stts.bajet.core;

import ir.stts.bajet.core.date.DateManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateManagerTest {

    private DateManager dateManager;

    @BeforeEach
    void setUp() {
        dateManager = new DateManager();
    }

    @Test
    void testDateFromStringWithValidInput() throws ParseException {

        String dateString = "2024-11-22";
        String pattern = "yyyy-MM-dd";
        Date date = dateManager.dateFromString(dateString, pattern);
        assertThat(date).isNotNull();
    }

    @Test
    void testDateFromStringWithInvalidInput() {

        String invalidDateString = "invalid-date";
        String pattern = "yyyy-MM-dd";
        assertThrows(ParseException.class, () -> dateManager.dateFromString(invalidDateString, pattern));
    }

    @Test
    void testLocalDateTimeFromStringWithValidInput() throws ParseException {

        String dateString = "2024-11-22 10:15:30";
        String pattern = "yyyy-MM-dd HH:mm:ss";
        LocalDateTime localDateTime = dateManager.localDateTimeFromString(dateString, pattern);
        assertThat(localDateTime).isNotNull();
        assertThat(localDateTime.getYear()).isEqualTo(2024);
        assertThat(localDateTime.getMonthValue()).isEqualTo(11);
        assertThat(localDateTime.getDayOfMonth()).isEqualTo(22);
    }

    @Test
    void testLocalDateFromStringWithValidInput() throws ParseException {

        String dateString = "2024-11-22";
        String pattern = "yyyy-MM-dd";
        LocalDate localDate = dateManager.localDateFromString(dateString, pattern);
        assertThat(localDate).isNotNull();
        assertThat(localDate.getYear()).isEqualTo(2024);
        assertThat(localDate.getMonthValue()).isEqualTo(11);
        assertThat(localDate.getDayOfMonth()).isEqualTo(22);
    }


    @Test
    void testDate2LocalDateTime() {

        Date date = new Date();
        LocalDateTime localDateTime = dateManager.date2LocalDateTime(date);
        assertThat(localDateTime).isNotNull();
        int i = Calendar.getInstance().get(Calendar.YEAR) - 1900;
        assertThat(localDateTime.getYear()).isEqualTo(date.getYear() + 1900);
    }

    @Test
    void testLocalDateTime2Date() {

        LocalDateTime localDateTime = LocalDateTime.of(2024, 11, 22, 10, 15, 30);
        Date date = dateManager.localDateTime2Date(localDateTime);
        assertThat(date).isNotNull();
    }

    @Test
    void testDate2PersianDateString() {

        Date date = new Date();
        String pattern = "yyyy/MM/dd";
        String persianDate = dateManager.date2PersianDateString(date, pattern);
        assertThat(persianDate).isNotNull();
    }

    @Test
    void testPersianDateString2DateWithValidInput() throws ParseException {

        String persianDateString = "1402/09/02";
        String pattern = "yyyy/MM/dd";
        Date date = dateManager.persianDateString2Date(persianDateString, pattern);
        assertThat(date).isNotNull();
    }

    @Test
    void testPersianDateString2DateWithInvalidInput() {

        String invalidPersianDateString = "invalid-date";
        String pattern = "yyyy/MM/dd";
        assertThrows(ParseException.class, () -> dateManager.persianDateString2Date(invalidPersianDateString, pattern));
    }

    @Test
    void testLocalDateTime2PersianDateString() {

        LocalDateTime localDateTime = LocalDateTime.of(2024, 11, 22, 10, 15, 30);
        String pattern = "yyyy/MM/dd";
        String persianDate = dateManager.localDateTime2PersianDateString(localDateTime, pattern);
        assertThat(persianDate).isNotNull();
    }

    @Test
    void testPersianDateString2LocalDateTimeWithValidInput() throws ParseException {

        String persianDateString = "1402/09/02";
        String pattern = "yyyy/MM/dd";
        LocalDateTime localDateTime = dateManager.persianDateString2LocalDateTime(persianDateString, pattern);
        assertThat(localDateTime).isNotNull();
    }

    @Test
    void testPersianDateString2LocalDateTimeWithInvalidInput() {

        String invalidPersianDateString = "invalid-date";
        String pattern = "yyyy/MM/dd";
        assertThrows(ParseException.class, () -> dateManager.persianDateString2LocalDateTime(invalidPersianDateString, pattern));
    }
}