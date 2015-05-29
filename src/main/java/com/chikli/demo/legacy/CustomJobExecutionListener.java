package com.chikli.demo.legacy;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;

public class CustomJobExecutionListener implements JobExecutionListener {
	private static final Logger LOG = LoggerFactory.getLogger(CustomJobExecutionListener.class);

	LocalDate initialDate = new LocalDate();

	public static final String DEFAULT_PROCESSED_FILE_PATH = FileUtil.rootDirectory() + "/PROCESSED/";
	public static final String DEFAULT_PROCESSING_FILE_PATH = FileUtil.rootDirectory() + "/STARTED/";
	public static final String DEFAULT_INPUT_FILE_PATH = FileUtil.rootDirectory() + "/ARRIVED/";

	public static final String BLANK = "";

	@Override
	public void beforeJob(final JobExecution jobExecution) {
		final JobParameters jobParameters = jobExecution.getJobParameters();
		final String filePath = jobParameters.getString("input.file.path");

		final String processingFilePath = jobParameters.getString("processing.file.path");
		final String processDate = jobParameters.getDate("process.date") != null ? DateTimeUtil.formatDateMdyNoSlashes(jobParameters.getDate("process.date"))
				: CustomJobExecutionListener.BLANK;
		String logFileName = setupLogfile(jobExecution, jobParameters, filePath, processDate);

		if (StringUtils.isBlank(filePath)) {
			return;
		}

		moveFiles(jobParameters, filePath, processingFilePath, logFileName);
	}

	String setupLogfile(final JobExecution jobExecution, final JobParameters jobParameters, final String filePath, final String processDate) {
		String logFileName = generateLogfileName(jobExecution, jobParameters, filePath, processDate);

		LOG.info("Log file name :" + logFileName);
		MDC.remove("inputFileName");
		MDC.put("inputFileName", logFileName);

		return logFileName;
	}

	private String generateLogfileName(final JobExecution jobExecution, final JobParameters jobParameters, final String filePath, final String processDate) {
		if (jobExecution.getJobInstance().getJobName().equals("agencyDebtExtract")) {
			final LocalDate processingExtractDate = DateTimeUtil.convertToLocalDate(jobParameters.getDate("processing.date"));
			return jobExecution.getJobInstance().getJobName() + jobParameters.getString("agencyId") + DateTimeUtil.formatDateYmdNoDashes(processingExtractDate);
		} else if (jobExecution.getJobInstance().getJobName().equals("postMatchExtract")) {
			return jobParameters.getString("post.match.file.name");
		} else if (jobExecution.getJobInstance().getJobName().equals("tpkExtract")) {
			return jobParameters.getString("tpk.offsets.extract.filename");
		} else if (jobExecution.getJobInstance().getJobName().equals("dnpExtract")) {
			return jobParameters.getString("dnp.extract.filename");
		} else if (jobExecution.getJobInstance().getJobName().equals("paymentExtract")) {
			return jobParameters.getString("payment.extract.filename");
		} else if (jobExecution.getJobInstance().getJobName().equals("creditElectExtract")) {
			return jobParameters.getString("credit.elect.extract.filename");
		} else if (StringUtils.isBlank(filePath)) {
			return jobExecution.getJobInstance().getJobName().concat(processDate);
		} else {
			return FileUtil.getFileNameFromPath(filePath);
		}
	}

	private void moveFiles(final JobParameters jobParameters, final String filePath, final String processingFilePath, String logFileName) {
		FileUtil.move(filePath, processingFilePath);

		final String icfp = jobParameters.getString("input.control.file.path");

		if (!StringUtils.isBlank(icfp)) {
			FileUtil.move(icfp, CustomJobExecutionListener.DEFAULT_PROCESSING_FILE_PATH + FileUtil.getFileNameFromPath(icfp));
		}

		final String mainCTXFile = getMainCTXFileIfCTXProcess(logFileName);
		if (!StringUtils.isBlank(mainCTXFile)) {
			FileUtil.move(CustomJobExecutionListener.DEFAULT_INPUT_FILE_PATH + mainCTXFile, CustomJobExecutionListener.DEFAULT_PROCESSING_FILE_PATH + mainCTXFile);
		}
	}

	private String getMainCTXFileIfCTXProcess(final String inputFileName) {
		if (FileUtil.isCTXPaymentProcess(inputFileName) && inputFileName.contains("CTXSPLIT")) {
			return FileUtil.retrieveCtxMainFileNameFromSplitFileName(inputFileName);
		}
		return null;
	}

	@Override
	public void afterJob(final JobExecution jobExecution) {
		final JobParameters jobParameters = jobExecution.getJobParameters();
		final String processingFilePath = jobParameters.getString("processing.file.path");
		String inputControlFilePath = jobParameters.getString("input.control.file.path");

		if (StringUtils.isBlank(processingFilePath)) {
			return;
		}

		final String processedFilePath = CustomJobExecutionListener.DEFAULT_PROCESSED_FILE_PATH + FileUtil.getFileNameFromPath(processingFilePath);
		String processedControlFilePath = null;
		if (!StringUtils.isBlank(inputControlFilePath)) {
			processedControlFilePath = CustomJobExecutionListener.DEFAULT_PROCESSED_FILE_PATH + FileUtil.getFileNameFromPath(inputControlFilePath);
		}

		if (jobExecution.getStatus().equals(BatchStatus.COMPLETED)) {
			FileUtil.move(processingFilePath, processedFilePath);
			if (!StringUtils.isBlank(processedControlFilePath)) {
				inputControlFilePath = CustomJobExecutionListener.DEFAULT_PROCESSING_FILE_PATH + FileUtil.getFileNameFromPath(inputControlFilePath);
				FileUtil.move(inputControlFilePath, processedControlFilePath);
			}
		}

	}

}
