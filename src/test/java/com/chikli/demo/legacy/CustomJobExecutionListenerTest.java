package com.chikli.demo.legacy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.MDC;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;

public class CustomJobExecutionListenerTest {

	private CustomJobExecutionListener listener;
	private JobExecution jobExecution;
	private JobInstance jobInstance;
	private JobParameters jobParameters;
	private String filePath;
	private String processDate;

	@Before
	public void setup() {
		listener = new CustomJobExecutionListener();

		jobExecution = mock(JobExecution.class);
		jobInstance = mock(JobInstance.class);
		when(jobExecution.getJobInstance()).thenReturn(jobInstance);
		jobParameters = mock(JobParameters.class);

		filePath = "/tmp/blah.txt";
		processDate = "2015-10-10";
	}

	@Test
	public void blankFilePath() {
		when(jobInstance.getJobName()).thenReturn("Fred");
		String filePath = "";

		String logfile = listener.setupLogfile(jobExecution, jobParameters, filePath, processDate);

		assertLogfile(logfile, "Fred2015-10-10");
	}

	@Test
	public void nonBlankFilePath() {
		when(jobInstance.getJobName()).thenReturn("Fred");

		String logfile = listener.setupLogfile(jobExecution, jobParameters, filePath, processDate);

		assertLogfile(logfile, "blah.txt");
	}

	@Test
	public void agencyDebtExtract() {
		when(jobInstance.getJobName()).thenReturn("agencyDebtExtract");
		when(jobParameters.getDate("processing.date")).thenReturn(new LocalDate("2015-10-10").toDate());
		when(jobParameters.getString("agencyId")).thenReturn("ABC");

		String logfile = listener.setupLogfile(jobExecution, jobParameters, filePath, processDate);

		assertLogfile(logfile, "agencyDebtExtractABC20151010");
	}

	@Test
	public void postMatchExtract() {
		when(jobInstance.getJobName()).thenReturn("postMatchExtract");
		when(jobParameters.getString("post.match.file.name")).thenReturn("pmf.txt");

		String logfile = listener.setupLogfile(jobExecution, jobParameters, filePath, processDate);

		assertLogfile(logfile, "pmf.txt");
	}

	@Test
	public void tpkExtract() {
		when(jobInstance.getJobName()).thenReturn("tpkExtract");
		when(jobParameters.getString("tpk.offsets.extract.filename")).thenReturn("tpk.txt");

		String logfile = listener.setupLogfile(jobExecution, jobParameters, filePath, processDate);

		assertLogfile(logfile, "tpk.txt");
	}
	@Test
	public void dnpExtract() {
		when(jobInstance.getJobName()).thenReturn("dnpExtract");
		when(jobParameters.getString("dnp.extract.filename")).thenReturn("dnp.txt");

		String logfile = listener.setupLogfile(jobExecution, jobParameters, filePath, processDate);

		assertLogfile(logfile, "dnp.txt");
	}

	@Test
	public void paymentExtract() {
		when(jobInstance.getJobName()).thenReturn("paymentExtract");
		when(jobParameters.getString("payment.extract.filename")).thenReturn("payment.txt");

		String logfile = listener.setupLogfile(jobExecution, jobParameters, filePath, processDate);

		assertLogfile(logfile, "payment.txt");
	}

	@Test
	public void creditElectExtract() {
		when(jobInstance.getJobName()).thenReturn("creditElectExtract");
		when(jobParameters.getString("credit.elect.extract.filename")).thenReturn("credit.txt");

		String logfile = listener.setupLogfile(jobExecution, jobParameters, filePath, processDate);

		assertLogfile(logfile, "credit.txt");
	}

	private void assertLogfile(String logfile, String expectedFilename) {
		assertThat(logfile, is(expectedFilename));
		assertThat(MDC.get("inputFileName"), is(expectedFilename));
	}

}
