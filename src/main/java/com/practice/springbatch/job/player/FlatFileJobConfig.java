package com.practice.springbatch.job.player;

import com.practice.springbatch.core.service.PlayerSalaryService;
import com.practice.springbatch.dto.PlayerDto;
import com.practice.springbatch.dto.PlayerSalaryDto;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.adapter.ItemProcessorAdapter;
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

import java.io.File;
import java.io.IOException;

/**
 * FlatFileItemReader 사용을 위한 Job Config 클래스
 * @author cyh68
 * @since 2023-06-01
 **/
@Configuration
@AllArgsConstructor
public class FlatFileJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job flatFileJob(Step flatFileStep) {
        return jobBuilderFactory.get("flatFileJob")
                .incrementer(new RunIdIncrementer())
                .start(flatFileStep)
                .build();
    }

    /**
     * 파라미터로 FlatFileItemReader를 받아 파일 데이터를 읽어온다.
     * <p>
     * 읽은 데이터를 writer 에서 출력해준다.
     * @param playerFileItemReader {@link FlatFileItemReader<PlayerDto>}
     * @return Step {@link Step}
     * @author cyh68
     * @since 2023-06-01
     **/
    @JobScope
    @Bean
    public Step flatFileStep(FlatFileItemReader<PlayerDto> playerFileItemReader,
                             ItemProcessorAdapter<PlayerDto, PlayerSalaryDto> playerSalaryItemProcessorAdapter,
                             FlatFileItemWriter<PlayerSalaryDto> playerFileItemWriter) {
        return stepBuilderFactory.get("flatFileStep")
                .<PlayerDto, PlayerSalaryDto>chunk(5)
                .reader(playerFileItemReader)
                .processor(playerSalaryItemProcessorAdapter)
                .writer(playerFileItemWriter)
                .build();
    }

    /**
     * 읽어서(ItemReader) 가공한(ItemProcessor) 데이터를 파일에 원하는 형태로 쓴다.
     * @return FlatFileItemWriter {@link FlatFileItemWriter}
     * @author cyh68
     * @since 2023-06-01
     * @throws IOException
     **/
    @StepScope
    @Bean
    public FlatFileItemWriter<PlayerSalaryDto> playerFileItemWriter() throws IOException {

        BeanWrapperFieldExtractor<PlayerSalaryDto> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"ID", "firstName", "lastName", "salary"});
        fieldExtractor.afterPropertiesSet();

        DelimitedLineAggregator<PlayerSalaryDto> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter("\t");
        lineAggregator.setFieldExtractor(fieldExtractor);

        //기존의 파일을 덮어쓴다.
        new File("player-salary-list.txt").createNewFile();
        FileSystemResource fileSystemResource = new FileSystemResource("player-salary-list.txt");

        return new FlatFileItemWriterBuilder<PlayerSalaryDto>()
                .name("playerFileItemWriter")
                .resource(fileSystemResource)
                .lineAggregator(lineAggregator) // 쓴 값들을 어떻게 조합을 해줄지에 대한 설정
                .build();
    }

    /**
     * ItemProcessor 대신에 좀 더 간편하게 사용 가능한 ItemProcessorAdapter
     * <p>
     * ItemProcessor 인터페이스를 상속 받고 있음
     * @param playerSalaryService {@link PlayerSalaryService}
     * @return ItemProcessorAdapter {@link ItemProcessorAdapter}
     * @author cyh68
     * @since 2023-06-01
     **/
    @StepScope
    @Bean
    public ItemProcessorAdapter playerSalaryItemProcessorAdapter(PlayerSalaryService playerSalaryService) {
        ItemProcessorAdapter<PlayerDto, PlayerSalaryDto> adapter = new ItemProcessorAdapter<>();
        adapter.setTargetObject(playerSalaryService); //사용할 클래스
        adapter.setTargetMethod("calcSalary"); //사용할 클래스 내의 메소드 명
        return adapter;
    }

    /**
     * playerSalaryService를 파라미터로 받아 Salary를 계산 후 PlayerSalaryDto를 반환해주는 ItemProcessor
     * @param playerSalaryService {@link PlayerSalaryService}
     * @return ItemProcessor {@link ItemProcessor}
     * @author cyh68
     * @since 2023-06-01
     **/
    @StepScope
    @Bean
    public ItemProcessor<PlayerDto, PlayerSalaryDto> playerSalaryItemProcessor(PlayerSalaryService playerSalaryService) {
        return new ItemProcessor<PlayerDto, PlayerSalaryDto>() {
            @Override
            public PlayerSalaryDto process(PlayerDto item) throws Exception {
                return playerSalaryService.calcSalary(item);
            }
        };
    }

    /**
     * FlatFileItemReader를 통해 파일에서 데이터를 읽어온다.
     * @return FlatFileItemReader {@link FlatFileItemReader}
     * @author cyh68
     * @since 2023-06-01
     **/
    @StepScope
    @Bean
    public FlatFileItemReader<PlayerDto> playerFileItemReader() {
        return new FlatFileItemReaderBuilder<PlayerDto>()
                .name("playerFileItemReader")
                .lineTokenizer(new DelimitedLineTokenizer()) //각 데이터가 나뉘어져 있는 구분자. DelimitedLineTokenizer()의 기본 값은 콤마(,)임
                .linesToSkip(1)
                .fieldSetMapper(new PlayerFieldSetMapper())
                .resource(new FileSystemResource("player-list.txt"))
                .build();
    }
}
