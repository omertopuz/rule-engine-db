package com.rules.model.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Data
@Entity
public class RuleContent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int ruleId;
    private String ruleName;
    private String ruleExplanation;

    @Lob
    private String ruleContent;

    @CreatedDate
    private Date createdDate;
    @CreatedBy
    private String createdBy;
    @LastModifiedDate
    private Date lastModifiedDate;
    @LastModifiedBy
    private String modifiedBy;
}
