package com.practice.springbatch.dto;

import lombok.Data;

/**
 * 파일에서 텍스트를 읽어 데이터를 저장할 Player Dto
 * @author cyh68
 * @since 2023-06-01
 **/
@Data
public class PlayerDto {
    private String ID;
    private String lastName;
    private String firstName;
    private String position;
    private int birthYear;
    private int debutYear;
}
