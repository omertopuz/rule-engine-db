package com.rules.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rules.exception.RuleEngineApiException;
import com.rules.model.RuleInventory;
import com.rules.model.entity.RuleContent;
import com.rules.repository.RuleRepository;
import lombok.extern.java.Log;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
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

    public Object fireRuleDeclaredType(int ruleId, String jsonPayLoad){
        return ruleInventory.getAllRules().entrySet().stream()
                .filter(p->p.getKey().getRuleId() == ruleId)
        .findFirst()
        .map(kc-> {

            Object mainObject = createFactInstance(kc.getValue().getKieBase()
                    ,kc.getKey().getMainObjectPackageName()
            ,kc.getKey().getMainObjectName(), jsonPayLoad);
            Object returnObject = createFactInstance(kc.getValue().getKieBase()
                    ,kc.getKey().getReturnObjectPackageName()
            ,kc.getKey().getReturnObjectName(), null);

            kc.getValue().execute(Arrays.asList(mainObject,returnObject));
            return returnObject;
        });
    }

    private Object createFactInstance(KieBase kb, String pack, String objName, String jsonStr){
        FactType mainObjectFact = kb.getFactType(pack, objName);
        Object factInstance;
        try {
            factInstance = mainObjectFact.newInstance();
        } catch (InstantiationException e) {
            throw new RuleEngineApiException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuleEngineApiException(e.getMessage());
        }

        if(jsonStr != null){
            ObjectMapper mapper = new ObjectMapper();
            try {
                factInstance = mapper.readValue(jsonStr, factInstance.getClass());
            } catch (JsonMappingException e) {
                throw new RuleEngineApiException(e.getMessage());
            } catch (JsonProcessingException e) {
                throw new RuleEngineApiException(e.getMessage());
            }
        }
        return factInstance;
    }

}
