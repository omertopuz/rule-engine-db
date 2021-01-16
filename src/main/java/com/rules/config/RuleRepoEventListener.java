package com.rules.config;

import com.rules.model.RuleInventory;
import com.rules.model.entity.RuleContent;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

@Log
@Service
public class RuleRepoEventListener{

    @Autowired
    private RuleInventory ruleInventory;

    @PostPersist
    @PostUpdate
    @PostRemove
    private void afterAnyUpdate(RuleContent rule) {
        log.info(String.format("Rule update/create, Rule Id:%s, Rule Explanation: %s ",rule.getRuleId(),rule.getRuleExplanation()));
        ruleInventory.reloadRule(rule);
        log.info("Rule context updated ");
    }

}
