package com.practice.springbatch.core.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@Setter
@DynamicUpdate //JPA 에서 Entity 의 일부 컬럼 값만 변경 되었을 때 변경된 값들에 대해서만 쿼리가 실행되도록 해주는 어노테이션
@Table(name = "plain_text")
public class PlainText {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String text;
}
