package com.rules;

import com.rules.model.Criminal;
import com.rules.model.RuleInventory;
import com.rules.model.RuleModel;
import com.rules.service.RuleService;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
    void test_DynamicRuleNestedObjects_WithStatefulKieSession(){

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

    @Test
    void test_StatelessKieSession_DynamicRuleNestedObjects(){

        RuleModel r = new RuleModel();
        Map<String,Object> fields = new HashMap<>();
        Map<String,Object> customer = new HashMap<>();
        customer.put("cityName","AnyWhereElse");
        fields.put("customerInfo",customer);
        r.setFields(fields);

        String rule = "package com.genericrule import static com.rules.model.RuleModel.*; import com.rules.model.RuleModel; rule \"rule5\" no-loop true lock-on-active true salience 1 when $s : RuleModel(toMap(fields[\"customerInfo\"])[\"cityName\"].toString().startsWith(\"An\")) then $s.put(\"discount\",10); update($s); end;";

        StatelessKieSession sks = RuleInventory.loadContainerFromString(rule).newStatelessKieSession();

        sks.execute(r);

        assertThat(r.getFields().get("discount")).isNotNull();
        assertThat(r.getFields().get("discount")).isEqualTo(10);
    }

    @Test
    void test_StatelessKieSession_DynamicRuleNestedObjects_MultipleRules(){

        RuleModel r = new RuleModel();
        Map<String,Object> fields = new HashMap<>();
        Map<String,Object> customer = new HashMap<>();
        customer.put("cityName","AnyWhereElse");
        fields.put("customerInfo",customer);
        fields.put("discount",0);
        fields.put("product","notebook");
        r.setFields(fields);

        String rule = "package com.genericrule import static com.rules.model.RuleModel.*; import com.rules.model.RuleModel; rule \"rule5\" no-loop true lock-on-active true salience 10 when $s : RuleModel(toMap(fields[\"customerInfo\"])[\"cityName\"].toString().startsWith(\"An\")) then $s.put(\"discount\",10); update($s); end; rule \"rule6\" no-loop true lock-on-active true salience 1 when $s : RuleModel(fields[\"product\"] == \"notebook\") then $s.put(\"discount\",toInt($s.get(\"discount\")) / 2 ); update($s); end;";

        StatelessKieSession sks = RuleInventory.loadContainerFromString(rule).newStatelessKieSession();

        sks.execute(r);

        assertThat(r.getFields().get("discount")).isNotNull();
        assertThat(r.getFields().get("discount")).isEqualTo(5);
    }

    @Test
    void test_StatelessKieSession_DynamicRuleNestedObjects_DeclaredVariables(){

        Map<String,Object> fields = new HashMap<>();
        Map<String,Object> server = new HashMap<>();
        server.put("name","server001");
        server.put("processors",4);
        server.put("memory",8192);
        server.put("diskSpace",128);
        server.put("cpuUsage",3);
        List<Map<String,Object>> virtualizationList = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            Map<String,Object> v = new HashMap<>();
            v.put("name","virtualization - " + i);
            v.put("diskSpace",4 * i);
            v.put("memory",1024 * i);
            virtualizationList.add(v);
        }


        server.put("virtualizations ",virtualizationList);
        fields.put("Server",server);

        String packageName = "com.genericrule";
        String mainObjectName = "Server";

        String rule = "package com.genericrule import java.util.List; declare Server name : String processors : int memory : int diskSpace : int virtualizations : List cpuUsage : int ruleMessage : String end; declare Virtualization name : String diskSpace : int memory : int end; rule \"check minimum server configuration\" dialect \"mvel\" when $server : Server(processors < 2 || memory <= 1024 || diskSpace <= 250 ) then $server.ruleMessage = $server.name + \" was rejected by don't apply the minimum configuration.\"; end;";

        StatelessKieSession sks = RuleInventory.loadContainerFromString(rule).newStatelessKieSession();

        FactType mainObjectFact = sks.getKieBase()
                .getFactType(packageName,mainObjectName);

        Object mainObject = null;
        try {
            mainObject = mainObjectFact.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        mainObjectFact.setFromMap(mainObject,fields);

        sks.execute(mainObject);

        String msg = (String) mainObjectFact.get(mainObject,"ruleMessage");
        assertThat(msg).isNotNull();
        assertThat(msg).contains("rejected");
    }

    @Test
    void test_StatelessKieSession_DynamicRuleNestedObjects_DeclaredVariables_Standalone() throws Exception{
        Map<String,Object> fields = new HashMap<>();
        fields.put("Server",
            Stream.of(new Object[][] {
                {"name","server001"},
                {"processors",4},
                {"memory",8192},
                {"diskSpace",128},
                {"cpuUsage",3},
                {"virtualizations"
                        ,Stream.of(
                                new HashMap<String,Object>(){{
                                    put("name","virtualization - 1");
                                    put("diskSpace",4);
                                    put("memory",1024);}},
                        new HashMap<String,Object>(){{
                            put("name","virtualization - 2");
                            put("diskSpace",8);
                            put("memory",2048);}}
                                    ).collect(Collectors.toList())}
        }).collect(Collectors.toMap(key -> (String)key[0], val -> val[1])));

        String packageName = "com.genericrule";
        String mainObjectName = "Server";

        String ruleStr = "package com.genericrule import java.util.List; declare Server name : String processors : int memory : int diskSpace : int virtualizations : List cpuUsage : int ruleMessage : String end; declare Virtualization name : String diskSpace : int memory : int end; rule \"check minimum server configuration\" dialect \"mvel\" when $server : Server(processors < 2 || memory <= 1024 || diskSpace <= 250 ) then $server.ruleMessage = $server.name + \" was rejected by don't apply the minimum configuration.\"; end;";

        KieServices kieServices = KieServices.Factory.get();

        KieRepository kr = kieServices.getRepository();
        KieFileSystem kfs = kieServices.newKieFileSystem();

        kfs.write("src/main/resources/genericRule.drl", ruleStr);

        KieBuilder kb = kieServices.newKieBuilder(kfs);
        kb.buildAll();

        KieContainer kContainer = kieServices.newKieContainer(kr.getDefaultReleaseId());

        StatelessKieSession sks = kContainer.newStatelessKieSession();

        FactType mainObjectFact = sks.getKieBase()
                .getFactType(packageName,mainObjectName);

        Object mainObject = mainObjectFact.newInstance();

        mainObjectFact.setFromMap(mainObject,fields);
        sks.execute(mainObject);

        assertThat((String) mainObjectFact.get(mainObject,"ruleMessage")).contains("rejected");
    }

}
