package com.practice.springbatch.job;

import com.practice.springbatch.BatchTestConfig;
import com.practice.springbatch.core.domain.PlainText;
import com.practice.springbatch.core.repository.PlainTextRepository;
import com.practice.springbatch.core.repository.ResultTextRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBatchTest
@SpringBootTest
@ExtendWith(SpringExtension.class) //스프링 환경에서 테스트 할 수 있도록
@ActiveProfiles("test")
@ContextConfiguration(classes = {PlainTextJobConfig.class, BatchTestConfig.class})
public class PlainTextJobConfigTest {
    //Job이 여러개 있을 경우 @ContextConfiguration(classes = {HelloJobConfig.class, BatchTestConfig.class}) 와 같이
    //테스트할 Job을 설정해줘야 오류가 발생하지 않음
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private PlainTextRepository plainTextRepository;

    @Autowired
    private ResultTextRepository resultTextRepository;

//    @AfterEach
//    public void tearDown() {
//        plainTextRepository.deleteAll();
//        resultTextRepository.deleteAll();
//    }

    @Test
    public void success_givenNoPlainText() throws Exception {
        // given
        // no givenText

        //when
        JobExecution execution = jobLauncherTestUtils.launchJob();

        //then
        assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
        assertEquals(resultTextRepository.count(), 0);
    }

    @Test
    public void success_givenPlainText() throws Exception {
        // given
        givenPlainText(12);

        //when
        JobExecution execution = jobLauncherTestUtils.launchJob();

        //then
        assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
        assertEquals(resultTextRepository.count(), 12);
    }

    private void givenPlainText(Integer count) {
        IntStream.range(0, count)
                .forEach(
                        num -> plainTextRepository.save(new PlainText(null, "text" + num))
                );
    }
}
