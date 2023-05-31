package com.practice.springbatch.job.player;

import com.practice.springbatch.dto.PlayerDto;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

/**
 * 파일에서 읽은 데이터를 가공하여 Dto를 반환하는 Mapper
 * @author cyh68
 * @since 2023-06-01
 **/
public class PlayerFieldSetMapper implements FieldSetMapper<PlayerDto> {

    @Override
    public PlayerDto mapFieldSet(FieldSet fieldSet) throws BindException {
        PlayerDto dto = new PlayerDto();
        dto.setID(fieldSet.readString(0));
        dto.setLastName(fieldSet.readString(1));
        dto.setFirstName(fieldSet.readString(2));
        dto.setPosition(fieldSet.readString(3));
        dto.setBirthYear(fieldSet.readInt(4));
        dto.setDebutYear(fieldSet.readInt(5));
        return dto;
    }
}
