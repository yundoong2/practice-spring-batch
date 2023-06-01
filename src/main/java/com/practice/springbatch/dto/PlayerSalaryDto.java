package com.practice.springbatch.dto;

import lombok.Data;

/**
 * 읽어온 Player 데이터를 가지고 Salary를 계산해서 ItemWriter를 통해 파일을 넘겨주기 위해 사용되는 Dto
 * @author cyh68
 * @since 2023-06-01
 **/
@Data
public class PlayerSalaryDto {
    private String ID;
    private String lastName;
    private String firstName;
    private String position;
    private int birthYear;
    private int debutYear;
    private int salary;

    public static PlayerSalaryDto of(PlayerDto playerDto, int salary) {
        PlayerSalaryDto playerSalary = new PlayerSalaryDto();
        playerSalary.setID(playerDto.getID());
        playerSalary.setLastName(playerDto.getLastName());
        playerSalary.setFirstName(playerDto.getFirstName());
        playerSalary.setPosition(playerDto.getPosition());
        playerSalary.setBirthYear(playerDto.getBirthYear());
        playerSalary.setDebutYear(playerDto.getDebutYear());
        playerSalary.setSalary(salary);

        return playerSalary;
    }
}
