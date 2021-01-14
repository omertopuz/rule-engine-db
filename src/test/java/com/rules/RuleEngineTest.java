package com.rules;

import com.rules.model.Criminal;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RuleEngineTest {

    @Autowired
    private KieSession kieSession;

    @Test
    void test_crimeObject(){
        Criminal c = Criminal.builder()
                .ageInterval(new Criminal.Interval(30,33))
                .tallInterval(new Criminal.Interval(168,172))
                .gender("M")
                .hasGlasses(true)
                .build();

        kieSession.insert(c);
        int firedRuleCount = kieSession.fireAllRules();
    }

    @Test
    void test_rule2(){
        Criminal c = Criminal.builder()
                .ageInterval(new Criminal.Interval(30,33))
                .tallInterval(new Criminal.Interval(168,172))
                .tall(170)
                .gender("F")
                .build();

        kieSession.insert(c);
        int firedRuleCount = kieSession.fireAllRules(match -> match.getRule().getName().startsWith("suspect is 165 cm tall"));
    }
}
