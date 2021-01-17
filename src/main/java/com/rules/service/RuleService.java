package com.rules.service;

import com.rules.exception.RuleEngineApiException;
import com.rules.model.RuleInventory;
import com.rules.model.entity.RuleContent;
import com.rules.repository.RuleRepository;
import lombok.extern.java.Log;
import org.kie.api.definition.type.FactType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

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
        .ifPresent(kc-> kc.getValue().execute(rulableObject));
    }

    public Object fireRuleDeclaredType(int ruleId, Map<String,Object> keyValue){
        return ruleInventory.getAllRules().entrySet().stream()
                .filter(p->p.getKey().getRuleId() == ruleId)
        .findFirst()
        .map(kc-> {
            FactType mainObjectFact = kc.getValue().getKieBase()
                    .getFactType(kc.getKey().getMainObjectPackageName()
                            , kc.getKey().getMainObjectName());

            Object mainObject = null;
            try {
                mainObject = mainObjectFact.newInstance();
            } catch (InstantiationException e) {
                throw new RuleEngineApiException(e.getMessage());
            } catch (IllegalAccessException e) {
                throw new RuleEngineApiException(e.getMessage());
            }

            mainObjectFact.setFromMap(mainObject,keyValue);

            kc.getValue().execute(mainObject);
            return mainObject;
        });
    }


}
