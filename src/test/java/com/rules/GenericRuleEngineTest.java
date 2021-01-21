package com.rules;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.io.ResourceFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class GenericRuleEngineTest {

    @Test
    void test_StatelessKieSession_DynamicRuleNestedObjects_DeclaredVariables_Standalone_WithStringRule() throws Exception{

        Map<String,Object> Server =
                Stream.of(new Object[][] {
                        {"name","server001"},
                        {"processors",4},
                        {"memory",8192},
                        {"diskSpace",1281},
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
                }).collect(Collectors.toMap(key -> (String)key[0], val -> val[1]));


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

        FactType mainObjectFact = sks.getKieBase().getFactType(packageName,mainObjectName);

        Object mainObject = mainObjectFact.newInstance();

        mainObjectFact.setFromMap(mainObject,Server);
        sks.execute(mainObject);

        assertThat(mainObjectFact.get(mainObject,"ruleMessage")).isNull();
    }

    @Test
    void test_StatelessKieSession_DynamicRuleNestedObjects_DeclaredVariables_Standalone_WithDrlFile() throws Exception{
        Map<String,Object> request =
                Stream.of(new Object[][] {
                        {"programsList",   //new ArrayList<>()}}
                                Stream.of(Stream.of(new Object[][]
                                                {{"ProgramStateId",2},{"IsActive",true},{"YDProgramId",15}}).collect(Collectors.toMap(key -> (String)key[0], val -> val[1])),
                                        Stream.of(new Object[][]
                                                {{"ProgramStateId",2},{"IsActive",true},{"YDProgramId",34}}).collect(Collectors.toMap(key -> (String)key[0], val -> val[1])),
                                        Stream.of(new Object[][]
                                                {{"ProgramStateId",2},{"IsActive",false},{"YDProgramId",35}}).collect(Collectors.toMap(key -> (String)key[0], val -> val[1]))).collect(Collectors.toList())
                        }}
                ).collect(Collectors.toMap(key -> (String)key[0], val -> val[1]));

        String packageName = "com.genericrule";
        String mainObjectName = "RequestModel";
        String returnObjectName = "CheckApplyResponse";

        StatelessKieSession sks = getContainer().newStatelessKieSession();

        FactType mainObjectFact = sks.getKieBase().getFactType(packageName,mainObjectName);
        Object mainObject = mainObjectFact.newInstance();
        FactType returnObjectFact = sks.getKieBase().getFactType(packageName,returnObjectName);
        Object returnObject = returnObjectFact.newInstance();

        mainObjectFact.setFromMap(mainObject,request);

        sks.execute(Arrays.asList(mainObject,returnObject));

        assertThat((String) returnObjectFact.getAsMap(returnObject).get("message")).contains("going");
    }

    @Test
    void test_StatelessKieSession_DynamicRuleNestedObjects_ByUsingJson_ObjectMapper() throws Exception{
        String packageName = "com.genericrule";
        String mainObjectName = "RequestModel";
        String returnObjectName = "CheckApplyResponse";

        StatelessKieSession sks = getContainer().newStatelessKieSession();

        FactType mainObjectFact = sks.getKieBase().getFactType(packageName,mainObjectName);
        Object mainObject = mainObjectFact.newInstance();
        FactType returnObjectFact = sks.getKieBase().getFactType(packageName,returnObjectName);
        Object returnObject = returnObjectFact.newInstance();

        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"programsList\":[{\"programStateId\":2 ,\"isActive\":true, \"programId\":15},{\"programStateId\":2 ,\"isActive\":true, \"programId\":35},{\"programStateId\":2 ,\"isActive\":true, \"programId\":34}]}";

        mainObject = mapper.readValue(json, mainObject.getClass());

        sks.execute(Arrays.asList(mainObject,returnObject));

        assertThat((String) returnObjectFact.getAsMap(returnObject).get("message")).contains("going");
    }

    KieContainer getContainer(){
        KieServices kieServices = KieServices.Factory.get();
        KieRepository kr = kieServices.getRepository();
        kr.addKieModule(() -> kr.getDefaultReleaseId());
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource("applyConditions.drl"));

        KieBuilder kb = kieServices.newKieBuilder(kieFileSystem);
        kb.buildAll();

        KieModule kieModule = kb.getKieModule();
        KieContainer kContainer = kieServices.newKieContainer(kieModule.getReleaseId());
        return kContainer;
    }
}
