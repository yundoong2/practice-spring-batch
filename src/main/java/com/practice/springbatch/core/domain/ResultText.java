package com.practice.springbatch.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * ItemWriter를 통해 결과를 저장할 Entity
 * @author cyh68
 * @since 2023-05-31
 **/
@Entity
@Getter
@Setter
@DynamicUpdate //JPA 에서 Entity 의 일부 컬럼 값만 변경 되었을 때 변경된 값들에 대해서만 쿼리가 실행되도록 해주는 어노테이션
@Table(name = "result_text")
@AllArgsConstructor
@NoArgsConstructor
public class ResultText {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String text;
}
