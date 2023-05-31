package com.practice.springbatch.job;

import com.practice.springbatch.core.domain.PlainText;
import com.practice.springbatch.core.domain.ResultText;
import com.practice.springbatch.core.repository.PlainTextRepository;
import com.practice.springbatch.core.repository.ResultTextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class PlainTextJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final PlainTextRepository plainTextRepository;
    private final ResultTextRepository resultTextRepository;

    @Bean("plainTextJob")
    public Job plainTextJob(Step plainTextStep) {
        return jobBuilderFactory.get("plainTextJob")
                .incrementer(new RunIdIncrementer()) //job을 실행할 때 횟수를 일정하게 증가시켜줌
                .start(plainTextStep) //실행할 Step
                .build();
    }


    @JobScope //관련 job이 실행되는 동안에만 해당 Bean이 실행되도록 설정하는 어노테이션
    @Bean("plainTextStep")
    public Step plainTextStep(ItemReader plainTextReader,
                          ItemProcessor plainTextProcessor,
                          ItemWriter plainTextWriter) {
        return stepBuilderFactory.get("plainTextStep")
                .<PlainText, String>chunk(5) //<읽어올 타입, Processing 할 타입>
                .reader(plainTextReader)
                .processor(plainTextProcessor)
                .writer(plainTextWriter)
                .build();
    }

    @StepScope
    @Bean
    public RepositoryItemReader<PlainText> plainTextReader() {
        return new RepositoryItemReaderBuilder<PlainText>()
                .name("plainTextReader")
                .repository(plainTextRepository)
                .methodName("findBy") //Repository 에서 사용할 메소드 명 지정
                .pageSize(5) //데이터를 읽게되는 Commit Interval
                .arguments(List.of()) //데이터를 읽을 때 어떤 조건이나 파라미터가 있을 경우 넘겨줄 리스트
                .sorts(Collections.singletonMap("id", Sort.Direction.DESC)) //데이터를 읽을 때 정렬 순서 지정
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<PlainText, String> plainTextProcessor() {
        /* 이렇게 리턴할수도 있지만 아래와 같이 람다식으로 간단하게 작성 가능
        return new ItemProcessor<PlainText, String>() {
            @Override
            public String process(PlainText item) throws Exception {
                return null;
            }
        }
        */
        return item -> "processed " + item.getText();
    }

    @StepScope
    @Bean
    public ItemWriter<String> plainTextWriter() {
        return items -> {
            //결과 값을 result_text 테이블에 저장
            items.forEach(item -> resultTextRepository.save(new ResultText(null, item)));
            System.out.println("=== chunk is finished");
        };
    }
}
