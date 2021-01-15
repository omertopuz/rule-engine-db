package com.rules;

import com.rules.model.Criminal;
import com.rules.model.entity.RuleContent;
import com.rules.service.RuleService;
import org.drools.compiler.rule.builder.util.PackageBuilderUtil;
import org.drools.core.base.RuleNameEqualsAgendaFilter;
import org.drools.core.base.RuleNameStartsWithAgendaFilter;
import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.drools.core.command.runtime.rule.FireAllRulesCommand;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.io.Resource;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class RuleEngineTest {

    @Autowired
    private RuleService ruleService;

    @Test
    void test_RuleService(){
        Criminal c = Criminal.builder()
                .tall(170)
                .gender("F")
                .build();

                Criminal c2 = Criminal.builder()
                .ageInterval(new Criminal.Interval(30,33))
                .tallInterval(new Criminal.Interval(168,172))
                .gender("M")
                .hasGlasses(true)
                .build();

        ruleService.fireRule(33,c);
        ruleService.fireRule(1,c2);
    }
}
