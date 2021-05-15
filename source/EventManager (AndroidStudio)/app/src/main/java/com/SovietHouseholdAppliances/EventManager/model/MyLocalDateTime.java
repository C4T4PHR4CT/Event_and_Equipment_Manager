package com.SovietHouseholdAppliances.EventManager.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import static java.time.LocalDateTime.ofEpochSecond;

public class MyLocalDateTime {
    public LocalDateTime dateTime;

    public MyLocalDateTime() {
        dateTime = LocalDateTime.now();
    }

    public MyLocalDateTime(LocalDateTime localDateTime) {
        dateTime = localDateTime;
    }

    public MyLocalDateTime(LocalDate localDate) {
        dateTime = ofEpochSecond(localDate.toEpochDay() * 24 * 60 * 60, 0, ZoneOffset.ofHours(0));
    }

    public MyLocalDateTime(long epochMilis) {
        dateTime = ofEpochSecond(epochMilis / 1000, 0, ZoneOffset.ofHours(0));
        dateTime.plus(epochMilis % 1000, ChronoUnit.MILLIS);
    }

    public long getEpochMilis() {
        return dateTime.toEpochSecond(ZoneOffset.ofHours(0)) * 1000 + dateTime.get(ChronoField.MILLI_OF_SECOND);
    }

    public String getDate() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(dateTime);
    }

    public String getTime() {
        return DateTimeFormatter.ofPattern("HH:mm").format(dateTime);
    }

    public String getDateTime() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(dateTime);
    }

    public String getDay() {
        return dateTime.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }
}