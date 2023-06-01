package com.practice.springbatch.job.parallel;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.SimplePartitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

/**
 * 단일 프로세스에서 Master Step 과 Work Step 을 두고,
 * Master Step 에서 생성해준 파티션 단위로 Step 을 병렬 처리한다.
 * @author cyh68
 * @since 2023-06-02
 **/
@Configuration
@AllArgsConstructor
public class PartitioningJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    //처리할 파티션 개수 정의
    private static final int PARTITION_SIZE = 100;

    /**
     * Partitioning Step 을 수행하는 Job
     * <p>
     *     master Step 을 실행한다.
     * </p>
     * @param masterStep {@link Step}
     * @return Job {@link Job}
     * @author cyh68
     * @since 2023-06-02
     **/
    @Bean
    public Job partitioningJob(Step masterStep) {
        return jobBuilderFactory.get("partitioningJob")
                .incrementer(new RunIdIncrementer())
                .start(masterStep)
                .build();
    }

    /**
     * Job 에서 호출하는 Master Step
     * <p>
     *     Partitioner 와 PartitionHandler 을 통해 step을 실행한다.
     * </p>
     * @param partitioner {@link Partitioner}
     * @param partitionHandler {@link TaskExecutorPartitionHandler}
     * @return Step {@link Step}
     * @author cyh68
     * @since 2023-06-02
     **/
    @JobScope
    @Bean
    public Step masterStep(Partitioner partitioner,
                           TaskExecutorPartitionHandler partitionHandler) {
        return stepBuilderFactory.get("masterStep")
                .partitioner("anotherStep", partitioner)
                .partitionHandler(partitionHandler)
                .build();
    }

    /**
     * Step에 적용할 Partitioner 정의
     * @return Partitioner {@link Partitioner}
     * @author cyh68
     * @since 2023-06-02
     **/
    @StepScope
    @Bean
    public Partitioner partitioner() {
        SimplePartitioner partitioner = new SimplePartitioner();
        partitioner.partition(PARTITION_SIZE);
        return partitioner;
    }

    /**
     * 사용할 Step 및 taskExecutor를 파라미터로 받아 실행할 Step 설정
     * @param anotherStep {@link Step}
     * @param taskExecutor {@link TaskExecutor}
     * @return TaskExecutorPartitionHandler {@link TaskExecutorPartitionHandler}
     * @author cyh68
     * @since 2023-06-02
     **/
    @StepScope
    @Bean
    public TaskExecutorPartitionHandler partitionHandler(Step anotherStep,
                                                         TaskExecutor taskExecutor) {
        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(anotherStep);
        partitionHandler.setGridSize(PARTITION_SIZE);
        partitionHandler.setTaskExecutor(taskExecutor);
        return partitionHandler;
    }
}

