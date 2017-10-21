package org.ckirk.spring.batch.samples.flow;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class PrintlnTasklet implements Tasklet {

	private final String text;
	
	public PrintlnTasklet(String text){
		this.text = text;
	}
	
	
	public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
		System.out.println(text);
		return RepeatStatus.FINISHED;
	}

}
