package com.chikli.demo.legacy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

	@Before
	public void setup() {
		listener = new CustomJobExecutionListener();

		jobExecution = mock(JobExecution.class);
		jobInstance = mock(JobInstance.class);
		when(jobExecution.getJobInstance()).thenReturn(jobInstance);
		jobParameters = new JobParameters();
	}

	@Test
	public void blankFilePath() {
		when(jobInstance.getJobName()).thenReturn("Fred");
		String filePath = "";
		String processDate = "2015-10-10";

		String logfile = listener.setupLogfile(jobExecution, jobParameters, filePath, processDate);

		assertLogfile(logfile, "Fred2015-10-10");
	}

	@Test
	public void nonBlankFilePath() {
		when(jobInstance.getJobName()).thenReturn("Fred");
		String filePath = "/tmp/blah.txt";
		String processDate = "2015-10-10";

		String logfile = listener.setupLogfile(jobExecution, jobParameters, filePath, processDate);

		assertLogfile(logfile, "blah.txt");
	}

	private void assertLogfile(String logfile, String expectedFilename) {
		assertThat(logfile, is(expectedFilename));
		assertThat(MDC.get("inputFileName"), is(expectedFilename));
	}

}
