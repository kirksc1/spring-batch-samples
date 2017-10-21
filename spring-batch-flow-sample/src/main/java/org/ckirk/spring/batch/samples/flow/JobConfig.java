package org.ckirk.spring.batch.samples.flow;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class JobConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new ResourcelessTransactionManager();
	}

	@Bean
	public JobRepository jobRepository() {
		try {
			return new MapJobRepositoryFactoryBean(transactionManager())
					.getJobRepository();
		} catch (Exception e) {
			return null;
		}
	}

	@Bean
	public JobLauncher jobLauncher() {
		final SimpleJobLauncher launcher = new SimpleJobLauncher();
		launcher.setJobRepository(jobRepository());
		return launcher;
	}

	@Bean
	public TaskExecutor taskExecutor(){
		return new SimpleAsyncTaskExecutor("spring_batch");
	}

//	NESTED JOB CONFIGURATION
//	@Bean
//	public Job job() {
//		return jobBuilderFactory.get("job")
//				.start(new FlowBuilder<SimpleFlow>("splitFlow")
//						.split(taskExecutor())
//						.add(new FlowBuilder<SimpleFlow>("flow1")
//										.start(step1())
//										.next(step2())
//										.build(),
//								new FlowBuilder<SimpleFlow>("flow2")
//										.start(step3())
//										.build())
//						.build())
//				.next(step4())
//				.build()
//				.build();
//	}

	@Bean
	public Job job() {
		return jobBuilderFactory.get("job")
			.start(splitFlow())
			.next(step4())
			.build()
			.build();
	}

	@Bean
	public Flow splitFlow() {
		return new FlowBuilder<SimpleFlow>("splitFlow")
			.split(taskExecutor())
			.add(flow1(), flow2())
			.build();
	}

	@Bean
	public Flow flow1() {
		return new FlowBuilder<SimpleFlow>("flow1")
			.start(step1())
			.next(step2())
			.build();
	}

	@Bean
	public Flow flow2() {
		return new FlowBuilder<SimpleFlow>("flow2")
			.start(step3())
			.build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
			.tasklet(new PrintlnTasklet("step1")).build();
	}

	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2")
			.tasklet(new PrintlnTasklet("step2")).build();
	}

	@Bean
	public Step step3() {
		return stepBuilderFactory.get("step3")
			.tasklet(new PrintlnTasklet("step3")).build();
	}

	@Bean
	public Step step4() {
		return stepBuilderFactory.get("step4")
			.tasklet(new PrintlnTasklet("step4")).build();
	}

}
