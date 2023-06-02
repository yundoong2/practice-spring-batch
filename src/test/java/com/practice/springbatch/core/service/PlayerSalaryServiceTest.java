package com.practice.springbatch.core.service;

import com.practice.springbatch.dto.PlayerDto;
import com.practice.springbatch.dto.PlayerSalaryDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Year;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * PlayerSalaryService 로직 테스트
 * @author cyh68
 * @since 2023-06-02
 **/
public class PlayerSalaryServiceTest {

    private PlayerSalaryService playerSalaryService;

    @BeforeEach
    public void setup() {
        playerSalaryService = new PlayerSalaryService();
    }

    /**
     * PlayerSalaryService:calcSalary 메소드 Test
     * @author cyh68
     * @since 2023-06-02
     **/
    @Test
    public void calcSalary() {
        //given
        Year mockYear = mock(Year.class);
        when(mockYear.getValue()).thenReturn(2023);
        Mockito.mockStatic(Year.class).when(Year::now).thenReturn(mockYear);

        PlayerDto mockPlayer = mock(PlayerDto.class);
        when(mockPlayer.getBirthYear()).thenReturn(1980);

        //when
        PlayerSalaryDto result = playerSalaryService.calcSalary(mockPlayer);

        //then
        Assertions.assertEquals(result.getSalary(), 43000000);
    }
}
