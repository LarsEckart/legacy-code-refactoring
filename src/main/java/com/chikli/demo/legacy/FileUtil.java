package com.chikli.demo.legacy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import org.apache.commons.io.FileUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This resource facade supports out-bound file operations.
 * 
 * @author admin
 */
public final class FileUtil {

	private static final String FILE_NAME_BEGIN_INDICATOR = "FDM";
	private static final String CTXSPLIT = ".CTXSPLIT";
	public static final String DATE_PATTERN = "yyMMdd";

	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
	public static final String PAM_PROCESS = "PAMINP";
	public static final String RRB_PROCESS = "RRBINP";
	public static final String OPM_PROCESS = "OPMINP";

	/**
	 * Constructor
	 */
	private FileUtil() {
	}

	public static String getFileNameFromPath(final String name) {
		if (name == null) {
			return null;
		}
		final int index = name.lastIndexOf('/');

		if (index > 0) {
			return name.substring(index + 1);
		}

		return name;
	}

	/**
	 * gets the CTX main file name from split file name
	 * 
	 * @param name
	 *            file name
	 * @return file name
	 */
	public static String setupFile(String name) {
		if (name == null) {
			return null;
		}
		int index = name.lastIndexOf('/');

		if (index > 0) {
			name = name.substring(index + 1);
		}

		index = name.lastIndexOf(CTXSPLIT);
		if (index > 0) {
			return name.substring(0, index);
		}

		return name;
	}

	public static BufferedReader utf8Reader(final String filePath) {
		return utf8Reader(lookupPath(filePath));
	}

	public static BufferedReader utf8Reader(final File file) {
		return utf8Reader(file, 0);
	}

	private static BufferedReader utf8Reader(final File file, final int size) {

		try {
			final InputStreamReader in = new InputStreamReader(new FileInputStream(file), Constants.DEFAULT_ENCODING);
			if (size == 0) {
				return new BufferedReader(in);
			}

			return new BufferedReader(in, size);

		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			throw new IllegalArgumentException("Could not create reader for [" + file.getAbsoluteFile() + "]", e);
		}
	}

	public static BufferedWriter utf8Writer(final File file, final boolean append) {
		try {
			return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), Constants.DEFAULT_ENCODING));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			throw new IllegalArgumentException("Could not create writer for [" + file.getAbsoluteFile() + "]", e);
		}
	}

	private static String getProcessName(final String fileName) {

		final String[] fileNameArray = fileName.split(Constants.SPILT_REGEX);
		if (fileNameArray.length > 2) {
			return fileNameArray[1];
		}
		return Constants.UNKNOWN;
	}

	public static String getFileNameForAccountingExtract(final LocalDate processingDate, final LocalDate runDate, final String environmentName) {
		final String fileNameTemplate = "FTO" + environmentName + "H.FACDR.PyyMMdd.RyyMMdd.HRC.DAT405.I";
		final String fileName = fileNameTemplate.replaceAll("PyyMMdd", "P" + DateTimeUtil.formatDateWithPattern(processingDate, DATE_PATTERN));
		return fileName.replaceAll("RyyMMdd", "R" + DateTimeUtil.formatDateWithPattern(runDate, DATE_PATTERN));
	}

	public static String getFileNameForPotentialMatchExtract(final LocalDate processingDate, final String environmentName, final String fileNameSecondNode) {
		final String fileNameTemplate = FILE_NAME_BEGIN_INDICATOR + environmentName + "." + fileNameSecondNode + "PMATCH.MERGED.DyyMMdd";
		final String fileName = fileNameTemplate.replaceAll("PyyMMdd", "P" + DateTimeUtil.formatDateWithPattern(processingDate, DATE_PATTERN));
		return fileName.replaceAll(DATE_PATTERN, DateTimeUtil.formatDateWithPattern(processingDate, DATE_PATTERN));
	}

	public static boolean isCTXPaymentProcess(final String name) {
		return getProcessName(name).contains("CTX");
	}

	public static void move(final String originalPath, final String targetPath) {
		try {
			FileUtils.moveFile(lookupPath(originalPath), lookupPath(targetPath));
		} catch (final IOException e) {
			logger.error(String.format("Could not move file from [%s] to [%s] - %s", originalPath, targetPath, e.getMessage()));
		}
	}

	public static String rootDirectory() {
		return System.getProperty("top.root", "/theroot");
	}

	public static File lookupPath(final String filePath) {
		return lookupPath(filePath, false);
	}

	public static File lookupPath(final String filePath, final boolean fileMustExist) {
		final File file = new File(filePath);

		if (fileMustExist && !file.exists()) {
			throw new IllegalArgumentException("Non existent file path: " + filePath);
		}

		return file;
	}

}
