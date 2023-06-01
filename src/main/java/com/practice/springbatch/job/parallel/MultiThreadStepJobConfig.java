package com.practice.springbatch.job.parallel;

import com.practice.springbatch.dto.AmountDto;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import java.io.File;
import java.io.IOException;

/**
 * 싱글 프로세스에서 chunk 단위로 병렬 처리한다.
 * @author cyh68
 * @since 2023-06-01
 **/
@Configuration
@AllArgsConstructor
public class MultiThreadStepJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job multiThreadStepJob(Step multiThreadStep) {
        return jobBuilderFactory.get("multiThreadStepJob")
                .incrementer(new RunIdIncrementer())
                .start(multiThreadStep)
                .build();
    }

    /**
     * multi-thread 를 적용한 Step
     * @param amountFileItemReader {@link FlatFileItemWriter}
     * @param amountFileItemProcessor {@link ItemProcessor}
     * @param amountFileItemWriter {@link FlatFileItemWriter}
     * @param taskExecutor {@link TaskExecutor}
     * @return Step {@link Step}
     * @author cyh68
     * @since 2023-06-01
     **/
    @JobScope
    @Bean
    public Step multiThreadStep(FlatFileItemReader<AmountDto> amountFileItemReader,
                                ItemProcessor<AmountDto, AmountDto> amountFileItemProcessor,
                                FlatFileItemWriter<AmountDto> amountFileItemWriter,
                                TaskExecutor taskExecutor) {
        return stepBuilderFactory.get("multiThreadStep")
                .<AmountDto, AmountDto>chunk(10)
                .reader(amountFileItemReader)
                .processor(amountFileItemProcessor)
                .writer(amountFileItemWriter)
                //Multi-threaded로 동작하기 위해 taskExecutor 를 추가해준다.
                .taskExecutor(taskExecutor)
                .build();
    }

    /**
     * multi-thread 를 Step 에 적용하기 위한 TaskExecutor
     * @return TaskExecutor {@link TaskExecutor}
     * @author cyh68
     * @since 2023-06-01
     **/
    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor =
                new SimpleAsyncTaskExecutor("spring-batch-task-executor");
        return taskExecutor;
    }

    /**
     * 파일을 읽기 위한 Reader
     * @return FlatFileItemReader {@link FlatFileItemReader}
     * @author cyh68
     * @since 2023-06-01
     **/
    @StepScope
    @Bean
    public FlatFileItemReader<AmountDto> amountFileItemReader() {
        return new FlatFileItemReaderBuilder<AmountDto>()
                .name("amountFileItemReader")
                .fieldSetMapper(new AmountFieldSetMapper())
                .lineTokenizer(new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB))
                .resource(new FileSystemResource("data/input.txt"))
                .build();
    }

    /**
     * 읽어온 데이터를 가공하기 위한 ItemProcessor
     * <p>
     * amount 를 증폭시키고, 현재 쓰레드를 출력한다.
     * @return ItemProcessor {@link ItemProcessor}
     * @author cyh68
     * @since 2023-06-01
     **/
    @StepScope
    @Bean
    public ItemProcessor<AmountDto, AmountDto> amountFileItemProcessor() {
        return item -> {
            System.out.println(item + "\tThread = " + Thread.currentThread().getName());
            item.setAmount(item.getAmount() * 100);
            return item;
        };
    }

    /**
     * 읽어서 가공된 데이터를 파일에 쓰기위한 ItemWriter
     * @return FlatFileItemWriter {@link FlatFileItemWriter}
     * @author cyh68
     * @since 2023-06-01
     * @throws IOException
     **/
    @StepScope
    @Bean
    public FlatFileItemWriter<AmountDto> amountFileItemWriter() throws IOException {

        BeanWrapperFieldExtractor<AmountDto> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"index", "name", "amount"});
        fieldExtractor.afterPropertiesSet();

        DelimitedLineAggregator lineAggregator = new DelimitedLineAggregator();
        lineAggregator.setFieldExtractor(fieldExtractor);

        String filePath = "data/output.txt";
        new File(filePath).createNewFile();

        return new FlatFileItemWriterBuilder<AmountDto>()
                .name("amountFileItemWriter")
                .resource(new FileSystemResource(filePath))
                .lineAggregator(lineAggregator)
                .build();
    }
}
