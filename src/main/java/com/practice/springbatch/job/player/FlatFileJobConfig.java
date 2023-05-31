package com.practice.springbatch.job.player;

import com.practice.springbatch.dto.PlayerDto;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

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
    public Step flatFileStep(FlatFileItemReader<PlayerDto> playerFileItemReader) {
        return stepBuilderFactory.get("flatFileStep")
                .<PlayerDto, PlayerDto>chunk(5)
                .reader(playerFileItemReader)
//                .processor()
                .writer(new ItemWriter<>() {
                    @Override
                    public void write(List<? extends PlayerDto> items) throws Exception {
                        items.forEach(System.out::println);
                    }
                })
                .build();
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
