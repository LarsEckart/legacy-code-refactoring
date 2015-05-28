package com.chikli.demo.legacy;

import org.joda.time.DateTime;
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

	public static String formatDateMdyNoSlashes(final java.util.Date date) {
		return FORMATTER_MDY_NO_SLASHES.print(new DateTime(date));
	}

	public static String formatDateYmdNoDashes(final LocalDate input) {
		return FORMATTER_YMD_NO_SLASHES.print(input);
	}

}
