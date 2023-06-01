package com.practice.springbatch.job.parallel;

import com.practice.springbatch.dto.AmountDto;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

/**
 * AmountDto 를 위한 FieldSetMapper
 * @author cyh68
 * @since 2023-06-01
 **/
public class AmountFieldSetMapper implements FieldSetMapper<AmountDto> {

    @Override
    public AmountDto mapFieldSet(FieldSet fieldSet) throws BindException {
        AmountDto amount = new AmountDto();
        amount.setIndex(fieldSet.readInt(0));
        amount.setName(fieldSet.readString(1));
        amount.setAmount(fieldSet.readInt(2));
        return amount;
    }
}
