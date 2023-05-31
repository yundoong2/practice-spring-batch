package com.practice.springbatch.core.repository;

import com.practice.springbatch.core.domain.ResultText;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 결과 값 저장 용도의 Repository
 * @author cyh68
 * @since 2023-05-31
 **/
public interface ResultTextRepository extends JpaRepository<ResultText, Integer> {

}
