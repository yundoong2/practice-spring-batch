package com.practice.springbatch.core.repository;

import com.practice.springbatch.core.domain.PlainText;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlainTextRepository extends JpaRepository<PlainText, Integer> {
    //페이징을 통해 데이터를 읽음. 페이지 사이즈 만큼의 데이터를 읽어옴
    Page<PlainText> findBy(Pageable pageable);
}
