package org.needle.bookingdiscount.utils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class DateUtils {
	
	static Logger logger = LoggerFactory.getLogger(DateUtils.class);
	
	public static String format(Date date, Locale locale) {
		String formatDate = null;
		try {
			formatDate = formatDateTime(date, locale);
		}
		catch(Exception e) {
			formatDate = formatDate(date, locale);
		}
		return formatDate;
	}
	
	public static Date parse(String date, Locale locale) {
		Date parsedDate = null;
		try {
			parsedDate = parseDateTime(date, locale);
		}
		catch(Exception e) {
			parsedDate = parseDate(date, locale);
		}
		return parsedDate;
	}
	
	public static String formatDateTime(Date date, Locale locale) {
		String pattern = getPattern(locale);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
		return localDateTime.format(formatter);
	}
	
	public static Date parseDateTime(String datetime, Locale locale) {
		datetime = datetime.replace("T", " ");
		DateTimeFormatter formatter = getDateTimeFormatter(locale);
		LocalDateTime localDateTime = LocalDateTime.parse(datetime, formatter);
		Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
		return Date.from(instant);
	}
	
	public static String formatDate(Date date, Locale locale) {
		DateTimeFormatter formatter = getDateFormatter(locale);
        LocalDate localDate = LocalDate.from(date.toInstant());
		return localDate.format(formatter);
	}
	
	public static Date parseDate(String date, Locale locale) {
		DateTimeFormatter formatter = getDateFormatter(locale);
		LocalDate localDate = LocalDate.parse(date, formatter);
		ZonedDateTime zdt = localDate.atStartOfDay(ZoneId.systemDefault());
		return Date.from(zdt.toInstant());
	}
	
	public static String formatDate(Date date, String pattern) {
		return new SimpleDateFormat(pattern).format(date);
	}
	
	public static Date parseDate(String date, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		try {
			return simpleDateFormat.parse(date);
		} 
		catch (ParseException e) {
			throw new RuntimeException("parseDate [" + date + ", " + 
					pattern + "] exception:" + e.getMessage());
		}
	}
	
	public static DateTimeFormatter getDateTimeFormatter(Locale locale) {
		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
				.appendPattern("yyyy-MM-dd[[ HH][:mm][:ss]]")
				.toFormatter(locale);
		return formatter;
	}
	
	public static DateTimeFormatter getDateFormatter(Locale locale) {
		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
				.appendPattern("yyyy-MM-dd[['T'HH][:mm][:ss]]")
				.parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
				.parseDefaulting(ChronoField.MINUTE_OF_DAY, 0)
				.parseDefaulting(ChronoField.SECOND_OF_DAY, 0)
				.parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
				.toFormatter(locale);
		return formatter;
	}
	
	public static String getPattern(Locale locale) {
		return getPattern(locale, DateFormat.MEDIUM);
	}
	
	public static String getPattern(Locale locale, int dateStyle) {
		DateFormat dateFormat = DateFormat.getDateInstance(dateStyle, locale);
		SimpleDateFormat simpleDateFormat = (SimpleDateFormat) dateFormat;
		String pattern = simpleDateFormat.toPattern();
		return pattern;
	}
	
	public static Date getDateNext(int days) {
		return getDateNext(new Date(), days);
	}
	
	public static Date getDateNext(Date date, int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, days);
		return calendar.getTime();
	}
	
	public static Date getDateNextYear(Date date, int years) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, years);
		return calendar.getTime();
	}
	
	public static Date getDate(Date date, int hour, int minute, int second) {
		Assert.notNull(date, "日期不正确");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	public static boolean dateEquals(Date date1, Date date2) {
		date1 = getDate(date1, 0, 0, 0);
		date2 = getDate(date2, 0, 0, 0);
		return date1.equals(date2);
	}
	
	public static String getYear(Date date) {
		return formatDate(date, "yyyy");
	}
	
	public static String getMonth(Date date) {
		return formatDate(date, "MM");
	}
	
	public static String getDay(Date date) {
		return formatDate(date, "dd");
	}
	
	public static double getHours(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		double hours = hour + ((minute) / 60.0);
		return new BigDecimal(hours).setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
	}
	
	public static long getMinuteMillis(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime().getTime();
	}
	
	public static Date getLastWeekMonday() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		while(c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			c.add(Calendar.DATE, -1);
		}
		return c.getTime();
	}
	
	public static void main(String[] args) throws Exception {
		String date = format(new Date(), Locale.getDefault());
		System.out.println(parse(date, Locale.getDefault()));
		System.out.println(parseDate("2020-10-28", Locale.getDefault()));
		System.out.println(parseDate("2020-10-28T00:00:00", Locale.getDefault()));
		System.out.println(parseDateTime("2020-10-28T10:32:40", Locale.getDefault()));
		System.out.println(parseDateTime("2020-10-28 10:32:40", Locale.getDefault()));
		System.out.println(parse("2020-10-28T10:32:40", Locale.getDefault()));
	}
	
}
