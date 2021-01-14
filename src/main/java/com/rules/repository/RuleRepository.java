package com.rules.repository;

import com.rules.model.entity.RuleContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "rules",collectionResourceRel = "rule-contents")
public interface RuleRepository extends JpaRepository<RuleContent,Integer> {
}
