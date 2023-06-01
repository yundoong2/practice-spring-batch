package com.practice.springbatch.job.parallel;

import com.practice.springbatch.dto.AmountDto;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

/**
 * 단일 프로세스 멀티 쓰레드에서 Flow를 사용해서 Step을 동시에 실행한다.
 *
 * @author cyh68
 * @since 2023-06-01
 **/
@Configuration
@AllArgsConstructor
public class ParallelStepJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    /**
     * Step 단위로 병렬 처리하는 Job
     * <p>
     * 파라미터로 Flow 를 받아서 start()에 넣어준다.
     * @param splitFlow {@link Flow}
     * @return Job {@link Job}
     * @author cyh68
     * @since 2023-06-01
     **/
    @Bean
    public Job parallelJob(Flow splitFlow) {
        return jobBuilderFactory.get("parallelJob")
                .incrementer(new RunIdIncrementer())
                .start(splitFlow)
                .build()
                .build();
    }

    /**
     * Step 병렬 처리를 하기 위한 Flow 정의
     * <p>
     *     Bean으로 등록한 taskExecutor 및 추가할 Flow를 파라미터로 받는다.
     * </p>
     * @param taskExecutor {@link TaskExecutor}
     * @param flowAmountFileStep {@link Flow}
     * @param flowAnotherStep {@link Flow}
     * @return Flow {@link Flow}
     * @author cyh68
     * @since 2023-06-01
     **/
    @Bean
    public Flow splitFlow(TaskExecutor taskExecutor,
                          Flow flowAmountFileStep,
                          Flow flowAnotherStep) {
        return new FlowBuilder<SimpleFlow>("splitFlow")
                .split(taskExecutor)
                .add(flowAmountFileStep, flowAnotherStep)
                .build();
    }

    /**
     * amountFileStep Step 을 실행할 Flow 정의
     * @param amountFileStep {@link Step}
     * @return Flow {@Link Flow}
     * @author cyh68
     * @since 2023-06-01
     **/
    @Bean
    public Flow flowAmountFileStep(Step amountFileStep) {
        return new FlowBuilder<SimpleFlow>("flowAmountFileStep")
                .start(amountFileStep)
                .build();
    }

    /**
     * MultiThreadStepJobConfig 에서 사용했던 Step 재활용
     * <p>
     *     대신, Multi-Threaded Step으로 설정하지 않음
     * </p>
     * @param amountFileItemReader {@link FlatFileItemReader}
     * @param amountFileItemProcessor {@link ItemProcessor}
     * @param amountFileItemWriter {@link FlatFileItemWriter}
     * @return Step {@link Step}
     * @author cyh68
     * @since 2023-06-01
     **/
    @Bean
    public Step amountFileStep(FlatFileItemReader<AmountDto> amountFileItemReader,
                               ItemProcessor<AmountDto, AmountDto> amountFileItemProcessor,
                               FlatFileItemWriter<AmountDto> amountFileItemWriter) {
        return stepBuilderFactory.get("multiThreadStep")
                .<AmountDto, AmountDto>chunk(10)
                .reader(amountFileItemReader)
                .processor(amountFileItemProcessor)
                .writer(amountFileItemWriter)
                .build();
    }

    /**
     * anotherStep Step 을 실행할 Flow 정의
     * @param anotherStep {@link Step}
     * @return Flow {@link Flow}
     * @author cyh68
     * @since 2023-06-01
     **/
    @Bean
    public Flow flowAnotherStep(Step anotherStep) {
        return new FlowBuilder<SimpleFlow>("anotherStep")
                .start(anotherStep)
                .build();
    }

    /**
     * 멀티 쓰레드로 여러 Step이 동시에 동작하고 있음을 확인하기 위한 Step
     * <p>
     *     Sleep을 주고 텍스트 출력
     * </p>
     * @return Step {@link Step}
     * @author cyh68
     * @since 2023-06-01
     **/
    @Bean
    public Step anotherStep() {
        return stepBuilderFactory.get("anotherStep")
                .tasklet((contribution, chunkContext) -> {
                    Thread.sleep(200);
                    System.out.println("Another Step Completed. Thread = " + Thread.currentThread().getName());
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
