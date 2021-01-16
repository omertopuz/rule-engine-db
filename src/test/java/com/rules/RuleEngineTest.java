package com.rules;

import com.rules.model.Criminal;
import com.rules.model.RuleInventory;
import com.rules.model.RuleModel;
import com.rules.service.RuleService;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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

        assertThat(c.getGuiltinessRate()).isEqualTo(50);
        assertThat(c2.getGuiltinessRate()).isEqualTo(80);
    }

    @Test
    void test_DynamicRules(){

        RuleModel r = new RuleModel();
        Map<String,Object> fields = new HashMap<>();
        fields.put("price",100);
        fields.put("destination","A");
        fields.put("customerType","VIP");
        r.setFields(fields);

        ruleService.fireRule(65,r);

        assertThat(r.getFields().get("price")).isNotNull();
        assertThat(r.getFields().get("price")).isEqualTo(53454);
    }

    @Test
    void test_DynamicRules2(){
        RuleModel r = new RuleModel();
        Map<String,Object> fields = new HashMap<>();
        int price = 100;
        fields.put("price",price);
        fields.put("destination","A");
        fields.put("customerType","VIP");
        r.setFields(fields);

        String rule = "package com.genericrule import static com.rules.model.RuleModel.*; import com.rules.model.RuleModel; rule \"rule4\" no-loop true lock-on-active true salience 1 when $s : RuleModel(fields[\"customerType\"] == \"VIP\") then $s.put(\"price\",toInt($s.get(\"price\")) * 2); update($s); end;";
        KieSession ks = RuleInventory.loadContainerFromString(rule).newKieSession();

        ks.insert(r);
        int firedRuleCount = ks.fireAllRules();

        assertThat(r.getFields().get("price")).isEqualTo(price * 2);
    }

    @Test
    void test_DynamicRuleNestedObjects(){

        RuleModel r = new RuleModel();
        Map<String,Object> fields = new HashMap<>();
        Map<String,Object> customer = new HashMap<>();
        customer.put("cityName","AnyWhereElse");
        fields.put("customerInfo",customer);
        r.setFields(fields);

        String rule = "package com.genericrule import static com.rules.model.RuleModel.*; import com.rules.model.RuleModel; rule \"rule5\" no-loop true lock-on-active true salience 1 when $s : RuleModel(toMap(fields[\"customerInfo\"])[\"cityName\"].toString().startsWith(\"An\")) then $s.put(\"discount\",10); update($s); end;";
        KieSession ks = RuleInventory.loadContainerFromString(rule).newKieSession();

        ks.insert(r);
        int firedRuleCount = ks.fireAllRules();

        assertThat(r.getFields().get("discount")).isNotNull();
        assertThat(r.getFields().get("discount")).isEqualTo(10);
    }

}
