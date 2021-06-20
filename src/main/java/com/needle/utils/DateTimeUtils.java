package com.needle.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Date;

public class DateTimeUtils {
	/**
	 * For example: 2018-12-28
	 */
	public static final String DATE = "yyyy-MM-dd";
	/**
	 * For example: 2018-12-28 10:00:00
	 */
	public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
	/**
	 * Example: 10:00:00
	 */
	public static final String TIME = "HHmmss";
	/**
	 * Example: 10:00
	 */
	public static final String TIME_WITHOUT_SECOND = "HH:mm";

	/**
	 * For example: 2018-12-28 10:00
	 */
	public static final String DATE_TIME_WITHOUT_SECONDS = "yyyy-MM-dd HH:mm";

	/**
	 * Get the year
	 *
	 * @return year
	 */
	public static int getYear() {
		LocalTime localTime = LocalTime.now();
		return localTime.get(ChronoField.YEAR);
	}

	/**
	 * Get the month
	 *
	 * @return month
	 */
	public static int getMonth() {
		LocalTime localTime = LocalTime.now();
		return localTime.get(ChronoField.MONTH_OF_YEAR);
	}

	/**
	 * Get the first few days of a month
	 *
	 * @return number
	 */
	public static int getMonthOfDay() {
		LocalTime localTime = LocalTime.now();
		return localTime.get(ChronoField.DAY_OF_MONTH);
	}

	/**
	 * Formatted date as a string
	 *
	 * @param date    date
	 * @param pattern format
	 * @return date string
	 */
	public static String format(Date date, String pattern) {
		Instant instant = date.toInstant();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
	}

	/**
	 * Parse string date as Date
	 *
	 * @param dateStr date string
	 * @param pattern format
	 * @return Date
	 */
	public static Date parse(String dateStr, String pattern) {
		LocalDateTime localDateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
		Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
		return Date.from(instant);
	}

	/**
	 * Increase the minute for Date, reduce the negative number
	 *
	 * @param date        Date
	 * @param plusMinutes The number of minutes to increase
	 * @return new date
	 */
	public static Date addMinutes(Date date, Long plusMinutes) {
		LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
		LocalDateTime newDateTime = dateTime.plusMinutes(plusMinutes);
		return Date.from(newDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * increase time
	 *
	 * @param date date
	 * @param hour The number of hours to increase
	 * @return new date
	 */
	public static Date addHour(Date date, Long hour) {
		LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
		LocalDateTime localDateTime = dateTime.plusHours(hour);
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * @return returns the start time of the day
	 */
	public static Date getStartTime() {
		LocalDateTime now = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
		return localDateTime2Date(now);
	}

	/**
	 * @return returns the end time of the day
	 */
	public static Date getEndTime() {
		LocalDateTime now = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999);
		return localDateTime2Date(now);
	}

	/**
	 * month reduction
	 *
	 * @param monthsToSubtract month
	 * @return Date
	 */
	public static Date minusMonths(long monthsToSubtract) {
		LocalDate localDate = LocalDate.now().minusMonths(monthsToSubtract);
		return localDate2Date(localDate);
	}

	public static Date localDate2Date(LocalDate localDate) {
		ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
		return Date.from(zonedDateTime.toInstant());
	}

	public static Date localDateTime2Date(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}
}
