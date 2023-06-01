package com.practice.springbatch.core.service;

import com.practice.springbatch.dto.PlayerDto;
import com.practice.springbatch.dto.PlayerSalaryDto;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
public class PlayerSalaryService {

    public PlayerSalaryDto calcSalary(PlayerDto playerDto) {
        int salary = (Year.now().getValue() - playerDto.getBirthYear()) * 1000000;
        return PlayerSalaryDto.of(playerDto, salary);
    }
}
