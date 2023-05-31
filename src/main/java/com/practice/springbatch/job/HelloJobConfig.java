package com.practice.springbatch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HelloJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean("helloJob")
    public Job helloJob(Step helloStep) {
        return jobBuilderFactory.get("helloJob")
                .incrementer(new RunIdIncrementer()) //job을 실행할 때 횟수를 일정하게 증가시켜줌
                .start(helloStep) //실행할 Step
                .build();
    }


    @JobScope //관련 job이 실행되는 동안에만 해당 Bean이 실행되도록 설정하는 어노테이션
    @Bean("helloStep")
    public Step helloStep(Tasklet tasklet) {
        return stepBuilderFactory.get("helloStep")
                .tasklet(tasklet)
                .build();
    }

    @StepScope //관련 Step이 실행되는 동안에만 Tasklet이 실행되도록 설정
    @Bean
    public Tasklet tasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("Hello Spring Batch");
            return RepeatStatus.FINISHED;
        };
    }
}
