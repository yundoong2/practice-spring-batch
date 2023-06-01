package com.practice.springbatch.job;

import com.practice.springbatch.job.validator.LocalDateParameterValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

/**
 * JobParameterValidator 를 사용하기 위한 Config
 * @author cyh68
 * @since 2023-05-31
 **/
@Configuration
@AllArgsConstructor
@Slf4j
public class AdvancedJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    /**
     * Job 실행 시 validator() 를 추가하여 파라미터에 대한 유효성 검증을 수행한다.
     * @param advancedStep {@link Step}
     * @return Job {@link Job}
     * @author cyh68
     * @since 2023-05-31
     **/
    @Bean
    public Job advancedJob(JobExecutionListener jobExecutionListener, Step advancedStep) {
        return jobBuilderFactory.get("advancedJob")
                .incrementer(new RunIdIncrementer())
                .validator(new LocalDateParameterValidator("targetDate"))
                .listener(jobExecutionListener)
                .start(advancedStep)
                .build();
    }

    /**
     * Job이 실행되기 전과 후의 상태를 확인할 수 있는 JobExecutionListener
     * @return JobExecutionListener {@link JobExecutionListener}
     * @author cyh68
     * @since 2023-06-01
     **/
    @JobScope
    @Bean
    public JobExecutionListener jobExecutionListener() {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                log.info("[JobExecutionListener#beforeJob] jobExecution is " + jobExecution.getStatus());
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                if (jobExecution.getStatus() == BatchStatus.FAILED) {
                    log.error("[JobExecutionListener#afterJob] jobExecution is FAILED!!! RECOVER ASAP");
                }
            }
        };
    }

    @JobScope
    @Bean
    public Step advancedStep(StepExecutionListener stepExecutionListener, Tasklet advancedTasklet) {
        return stepBuilderFactory.get("advancedStep")
                .listener(stepExecutionListener)
                .tasklet(advancedTasklet)
                .build();
    }

    /**
     * Step이 실행되기 전과 후의 상태를 확인할 수 있는 StepExecutionListener
     * <p>
     * 로깅 용도로는 사용하지 않으며, 특별히 처리해야할 상황이 있을 때 사용한다.
     * @return StepExecutionListener {@link StepExecutionListener}
     * @author cyh68
     * @since 2023-06-01
     * @throws
     **/
    @StepScope
    @Bean
    public StepExecutionListener stepExecutionListener() {
        return new StepExecutionListener() {
            @Override
            public void beforeStep(StepExecution stepExecution) {
                log.info("[StepExecutionListener#beforeStep] stepExecution is " + stepExecution.getStatus());
            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                log.info("[StepExecutionListener#afterStep] stepExecution is " + stepExecution.getStatus());
                return stepExecution.getExitStatus();
            }
        };
    }

    /**
     * Step에서 수행될 Tasklet 정의
     * <p>
     * 파라미터로 JobParameter의 targetDate를 받아온다.
     * @param targetDate {@link String}
     * @return Tasklet {@link Tasklet}
     * @author cyh68
     * @since 2023-05-31
     **/
    @StepScope
    @Bean
    public Tasklet advancedTasklet(@Value("#{jobParameters['targetDate']}") String targetDate) {
        return (contribution, chunkContext) -> {
            log.info("[AdvancedJobConfig] JobParameter - targetDate = " + targetDate);
            LocalDate executionDate = LocalDate.parse(targetDate);
            log.info("[AdvancedJobConfig] executed advancedTasklet");
            return RepeatStatus.FINISHED;
        };
    }
}
