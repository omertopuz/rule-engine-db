package com.rules.controller;


import com.rules.model.RuleModel;
import com.rules.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/rule-api")
@RestController
public class RuleController {

    @Autowired
    private RuleService ruleService;

    @PostMapping("/rules/{ruleId}")
    public void executeRule(@RequestBody RuleModel request, @PathVariable int ruleId){
        ruleService.fireRule(ruleId,request);
    }

    @PostMapping(value = "/rules/declared-type/{ruleId}", consumes = "text/plain")
    public Object executeDeclaredTypeRule(@RequestBody String payload, @PathVariable int ruleId){
        return ruleService.fireRuleDeclaredType(ruleId,payload);
    }
}
