package com.chikli.demo.legacy;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FileUtil {

	private static final String CTXSPLIT = ".CTXSPLIT";

	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

	public static final String SPILT_REGEX = "\\.";

	public static final String UNKNOWN = "UNKNOWN";

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

	public static String retrieveCtxMainFileNameFromSplitFileName(String name) {
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

	private static String getProcessName(final String fileName) {

		final String[] fileNameArray = fileName.split(FileUtil.SPILT_REGEX);
		if (fileNameArray.length > 2) {
			return fileNameArray[1];
		}
		return FileUtil.UNKNOWN;
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
