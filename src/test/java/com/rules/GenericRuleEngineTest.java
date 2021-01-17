package com.rules;

import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class GenericRuleEngineTest {

    @Test
    void test_StatelessKieSession_DynamicRuleNestedObjects_DeclaredVariables_Standalone1() throws Exception{
//        Map<String,Object> fields = new HashMap<>();
//        //processors < 2 || memory <= 1024 || diskSpace <= 250
//        fields.put("Server",
//            Stream.of(new Object[][] {
//                    {"name","server001"},
//                    {"processors",4},
//                    {"memory",8192},
//                    {"diskSpace",1281},
//                    {"cpuUsage",3},
//                {"virtualizations"
//                        ,Stream.of(
//                                new HashMap<String,Object>(){{
//                                    put("name","virtualization - 1");
//                                    put("diskSpace",4);
//                                    put("memory",1024);}},
//                        new HashMap<String,Object>(){{
//                            put("name","virtualization - 2");
//                            put("diskSpace",8);
//                            put("memory",2048);}}
//                                    ).collect(Collectors.toList())}
//        }).collect(Collectors.toMap(key -> (String)key[0], val -> val[1])));

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
    void test_StatelessKieSession_DynamicRuleNestedObjects_DeclaredVariables_Standalone2() throws Exception{

        Map<String,Object> Server =
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

        assertThat((String) mainObjectFact.get(mainObject,"ruleMessage")).contains("rejected");
    }

    @Test
    void test_StatelessKieSession_DynamicRuleNestedObjects_DeclaredVariables_Standalone() throws Exception{
        Map<String,Object> fields =
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

        mainObjectFact.setFromMap(mainObject,fields);
        sks.execute(mainObject);

        assertThat((String) mainObjectFact.get(mainObject,"ruleMessage")).contains("rejected");
    }

}
