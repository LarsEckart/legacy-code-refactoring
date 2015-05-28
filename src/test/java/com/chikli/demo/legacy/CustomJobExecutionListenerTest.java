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

	private void assertLogfile(String logfile, String expectedFilename) {
		assertThat(logfile, is(expectedFilename));
		assertThat(MDC.get("inputFileName"), is(expectedFilename));
	}

}
