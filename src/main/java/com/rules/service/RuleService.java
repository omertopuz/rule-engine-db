package com.rules.service;

import com.rules.model.RuleInventory;
import com.rules.model.entity.RuleContent;
import com.rules.repository.RuleRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Log
@Service
public class RuleService {

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private RuleInventory ruleInventory;

    @PostConstruct
    private void loadAllRules(){
        List<RuleContent> allRules = ruleRepository.findAll();
        allRules.forEach(ruleInventory::reloadRule);
    }



    public <T> void fireRule(int ruleId, T rulableObject){
        ruleInventory.getAllRules().entrySet().stream()
                .filter(p->p.getKey().getRuleId() == ruleId)
        .findFirst()
        .ifPresent(kc->{
            kc.getValue().insert(rulableObject);
            int ruleCount = kc.getValue().fireAllRules();
            if (ruleCount>0){
                log.info(+ruleCount+" rule fired: "
                        + kc.getKey().getRuleExplanation());
                kc.getValue().dispose();
            }

        });

    }


}
