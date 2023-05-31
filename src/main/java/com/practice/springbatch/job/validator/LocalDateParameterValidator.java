package com.practice.springbatch.job.validator;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Job Parameter Validator
 * @author cyh68
 * @since 2023-05-31
 **/
@AllArgsConstructor
public class LocalDateParameterValidator implements JobParametersValidator {

    private String parameterName;

    /**
     * Job Parameter에 대한 유효성 검증을 수행
     * <p>
     * String 형식의 LocalDate 파라미터 값에 대한 유효성을 검증한다.
     * @param parameters {@link JobParameters}
     * @author cyh68
     * @since 2023-05-31
     * @throws JobParametersInvalidException
     **/
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String localDate = parameters.getString(parameterName);

        if (!StringUtils.hasText(localDate)) {
            throw new JobParametersInvalidException(parameterName + "가 빈 분자열이거나 존재하지 않습니다.");
        }

        try {
            LocalDate.parse(localDate);
        } catch (DateTimeParseException e) {
            throw new JobParametersInvalidException(parameterName + "가 날짜 형식의 문자열이 아닙니다.");
        }
    }
}
