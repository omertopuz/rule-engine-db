package com.rules.model;

import com.rules.config.DroolsCustomEventListener;
import com.rules.model.entity.RuleContent;
import lombok.Getter;
import lombok.Setter;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class RuleInventory {

    private Map<RuleContent, StatelessKieSession> allRules;
    private static String RESOURCE_FOLDER_PATH = "src/main/resources/";
    private final DroolsCustomEventListener eventListener;

    public RuleInventory(DroolsCustomEventListener eventListener) {
        this.eventListener = eventListener;
        allRules = new HashMap<>();
    }

    public static KieContainer loadContainerFromString(String ruleStr) {
        KieServices kieServices = KieServices.Factory.get();

        KieRepository kr = kieServices.getRepository();
        KieFileSystem kfs = kieServices.newKieFileSystem();

        kfs.write(RESOURCE_FOLDER_PATH + ruleStr.hashCode() + ".drl", ruleStr);

        KieBuilder kb = kieServices.newKieBuilder(kfs);
        kb.buildAll();

        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
        }
        KieContainer kContainer = kieServices.newKieContainer(kr.getDefaultReleaseId());

        return kContainer;
    }

    public void reloadRule(RuleContent rule){
        StatelessKieSession sks = loadContainerFromString(rule.getRuleContent()).newStatelessKieSession();
        sks.addEventListener(eventListener);
        getAllRules().put(rule, sks);
    }

}
