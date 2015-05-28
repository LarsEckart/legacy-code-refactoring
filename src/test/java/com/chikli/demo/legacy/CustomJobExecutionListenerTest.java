package com.chikli.demo.legacy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.swing.JPanel;

import org.junit.Test;
import org.slf4j.MDC;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;

public class CustomJobExecutionListenerTest {

	@Test
	public void blankFilePath() {
		CustomJobExecutionListener listener = new CustomJobExecutionListener();

		JobExecution jobExecution = mock(JobExecution.class);
		JobInstance jobInstance = mock(JobInstance.class);
		when(jobExecution.getJobInstance()).thenReturn(jobInstance);
		when(jobInstance.getJobName()).thenReturn("Fred");
		JobParameters jobParameters = new JobParameters();
		String filePath = "";
		String processDate = "2015-10-10";

		String logfile = listener.setupLogfile(jobExecution, jobParameters, filePath, processDate);

		assertThat(logfile, is("Fred2015-10-10"));
		assertThat(MDC.get("inputFileName"), is("Fred2015-10-10"));
	}

}
