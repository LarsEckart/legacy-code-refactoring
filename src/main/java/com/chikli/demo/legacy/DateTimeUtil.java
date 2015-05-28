package com.chikli.demo.legacy;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class DateTimeUtil {

	private static final DateTimeFormatter FORMATTER_YMD_NO_SLASHES = DateTimeFormat.forPattern("yyyyMMdd");
	private static final DateTimeFormatter FORMATTER_MDY_NO_SLASHES = DateTimeFormat.forPattern("MMddyyyy");

	private DateTimeUtil() {
	}

	public static LocalDate convertToLocalDate(final java.util.Date inputDate) {
		if (inputDate == null) {
			return null;
		}

		return new LocalDate(inputDate);
	}

	public static boolean isBusinessDate(final LocalDate inputDate, final List<LocalDate> holidays) {
		return !isWeekend(inputDate) && !isHoliday(inputDate, holidays);
	}

	private static boolean isHoliday(final LocalDate inputDate, final List<LocalDate> holidays) {
		for (final LocalDate holiday : holidays) {
			if (inputDate.isEqual(holiday)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isWeekend(final LocalDate date) {
		final int dayOfWeek = date.getDayOfWeek();
		return dayOfWeek == DateTimeConstants.SATURDAY || dayOfWeek == DateTimeConstants.SUNDAY;
	}

	public static String formatDateMdyNoSlashes(final java.util.Date date) {
		return FORMATTER_MDY_NO_SLASHES.print(new DateTime(date));
	}

	public static String formatDateYmdNoDashes(final LocalDate input) {
		return FORMATTER_YMD_NO_SLASHES.print(input);
	}

	public static LocalDate parseLocalDateFrom(final String input) {
		return DateTimeFormat.forPattern("yyyy-MM-dd").parseLocalDate(input);
	}

	public static String formatDateWithPattern(final LocalDate date, final String pattern) {
		return DateTimeFormat.forPattern(pattern).print(date);
	}

	public static LocalDate parseLocalDateWithPattern(final String date, final String pattern) {
		return date == null ? null : DateTimeFormat.forPattern(pattern).parseLocalDate(date);
	}

}
